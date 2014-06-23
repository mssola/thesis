// Copyright (C) 2014 Miquel Sabaté Solà <mikisabate@gmail.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package main

import (
	"fmt"
	"io"
	"io/ioutil"
	"net"
	"net/http"

	"github.com/codegangsta/negroni"
	"github.com/gorilla/mux"
)

var (
	// The base TCP port to be used.
	basePort = 3000
)

// Perform a call to the Storm application. The connection will be kept open
// if it is streaming and the responses will be flushed out as they come. At
// end of this function the given ResponseWriter will be closed.
//
// Besides the ResponseWriter, the caller must pass the parameters to be sent
// to the Storm application. The third and the fourth parameters of this
// function are ports: the former is the port of the current handler, and the
// latter is the target port (the port to the Storm service). The last
// parameter to be passed tells this function if this is a streaming endpoint
// or not.
func storm(w http.ResponseWriter, pars string, port, target int, streaming bool) {
	c := make(chan int)
	listener, err := net.Listen("tcp", fmt.Sprintf(":%v", port))
	if err != nil {
		fmt.Printf("Could not connect %v!", err)
		return
	}

	// This is the real deal. It's an anonymous function that will be run
	// as a goroutine. This function will:
	//
	//	1. Read the response from Storm and pass it to the ResponseWriter.
	//	2. Handle gracefully the case when the client closes the connection.
	//	3. Handle streaming and non-streaming responses transparently.
	//
	// When it has finished, it will send a 1 to the c channel.
	go func() {
		// Open a notifier to check if the client has closed the connection.
		clientClosed := make(<-chan bool)
		if cn, ok := w.(http.CloseNotifier); ok {
			clientClosed = cn.CloseNotify()
		}

		for {
			select {
			case <-clientClosed:
				c <- 1
				return
			default:
				conn, err := listener.Accept()
				if err != nil {
					fmt.Printf("Something wrong happenned: %v", err)
					c <- 1
					return
				}
				data, _ := ioutil.ReadAll(conn)
				io.WriteString(w, string(data))
				w.(http.Flusher).Flush()
				if !streaming {
					c <- 1
					return
				}
			}
		}
	}()

	// Send Storm the port and the id.
	addr, _ := net.ResolveTCPAddr("tcp", fmt.Sprintf("localhost:%v", target))
	conn, err := net.DialTCP("tcp", nil, addr)
	_, err = conn.Write([]byte(fmt.Sprintf("%v&%v", pars, port)))
	if err != nil {
		fmt.Printf("We could not open the TCP socket")
		return
	}
	conn.Close()

	// Wait until we're sure that we've got everything from Storm.
	<-c
	listener.Close()
}

// The handler for the AQS service. It forms the parameters and calls
// the storm function with streaming = false.
func aqsHandler(w http.ResponseWriter, req *http.Request) {
	vars := mux.Vars(req)

	// Set the parameters for the Storm app.
	from, to := req.FormValue("from"), req.FormValue("to")
	p := req.FormValue("property")
	v := fmt.Sprintf("%v&%v&%v&%v", vars["id"], from, to, p)

	// Call the storm application.
	basePort = basePort + 1
	storm(w, v, basePort, 8001, false)
}

// The handler for the BSP service. It forms the parameters and calls
// the storm function with streaming = true.
func bspHandler(w http.ResponseWriter, req *http.Request) {
	vars := mux.Vars(req)

	// Setup the headers to chunked.
	w.Header().Set("Transfer-Encoding", "chunked")
	w.Header().Set("Connection", "close")

	// Set the parameters for the Storm app.
	v := fmt.Sprintf("create&%v", vars["id"])

	// Call the storm application.
	basePort = basePort + 1
	storm(w, v, basePort, 8002, true)
}

func main() {
	n := negroni.Classic()

	r := mux.NewRouter()
	r.HandleFunc("/bsp/s/{id}", bspHandler).Methods("GET")
	r.HandleFunc("/aqs/{id}/interval", aqsHandler).Methods("GET")
	n.UseHandler(r)

	n.Run(":3000")
}
