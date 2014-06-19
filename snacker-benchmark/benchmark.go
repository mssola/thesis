package main

import (
	"fmt"
	"math/rand"
	"net/http"
)

func handler(w http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(w, "%v", rand.Intn(1000))
}

func main() {
	rand.Seed(42)
	http.HandleFunc("/", handler)
	http.ListenAndServe(":8080", nil)
}
