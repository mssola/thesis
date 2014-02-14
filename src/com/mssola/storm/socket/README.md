# Socket

In this example there's a spout that is listening to a TCP socket. When
something arrives from the socket, then this spout will spit the read line
to a bolt. This bolt will then process this word by storing it to Redis.

Let's run this example. First of all, fire up the redis daemon. In Archlinux
this is easy candy:

  $ sudo systemctl start redis

Ok, Redis is up and running. Now we need to run this example through sbt:

  $ sbt run

This will update all the dependencies and it will finally start a TCP
server that is listening to the port 9000. Let's send a word to this app:

  $ telnet 127.0.0.1 9000
  $ hello

After doing this, the server will close the client connection. Therefore,
if you want to pass more words you'll have to re-connect again and write
another word. I know that this is a bit tedious, but I wanted to keep this
app as simple as possible. A quick solution would've been to implement an
extra bolt filtering sentences, like the SplitBolt from the
com.mssola.storm.basic package. Anyways, we can check the results from
the previous example by accessing to the Redis CLI. That is:

  $ redis-cli
  127.0.0.1:6379) HMGET word-count hello
  1) "1"

This means that we've received the word "hello" 1 time. In order to list all
the words that we've received so far we can just execute the Redis command:

  127.0.0.1:6379) HKEYS word-count
  1) "word"

And that's all!

Copyright &copy; 2014 Miquel Sabaté Solà (mikisabate@gmail.com).
See the LICENSE file.
