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

func storm(w http.ResponseWriter, pars string, port, target int, streaming bool) {
	c := make(chan int)
	listener, err := net.Listen("tcp", fmt.Sprintf(":%v", port))
	if err != nil {
		fmt.Printf("Could not connect %v!", err)
		return
	}

	go func() {
		for {
			conn, err := listener.Accept()
			if err != nil {
				fmt.Printf("Something wrong happenned: %v", err)
				break
			}
			data, _ := ioutil.ReadAll(conn)
			io.WriteString(w, string(data))
			w.(http.Flusher).Flush()
			if !streaming {
				break
			}
		}
		c <- 1
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

var port int

func handler(w http.ResponseWriter, req *http.Request) {
	vars := mux.Vars(req)

	// Setup the headers to chunked.
	w.Header().Set("Transfer-Encoding", "chunked")
	w.Header().Set("Connection", "close")

	// Get the port.
	port = port + 1

	// The guy that does the real job.
	storm(w, vars["id"], port, 8002, true)
}

func aqsHandler(w http.ResponseWriter, req *http.Request) {
	vars := mux.Vars(req)

	port = port + 1

	// Set the parameters for the Storm app.
	from, to := req.FormValue("from"), req.FormValue("to")
	p := req.FormValue("property")
	v := fmt.Sprintf("%v&%v&%v&%v", vars["id"], from, to, p)

	storm(w, v, port, 8001, false)
}

func main() {
	n := negroni.Classic()

	port = 3000

	r := mux.NewRouter()
	r.HandleFunc("/bsp/s/{id}", handler).Methods("GET")
	r.HandleFunc("/aqs/{id}/interval", aqsHandler).Methods("GET")
	n.UseHandler(r)

	n.Run(":3000")
}
