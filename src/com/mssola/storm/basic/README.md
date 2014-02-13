# Basic

This is a basic example so we get to know Storm a bit better. It creates
one spout and two bolts. The spout will be spitting random sentences to
the first bolt. The first bolt will then split the received sentence into
words and it will emit these words to the last bolt. The last bolt keeps
track of the words that it has seen.

This example doesn't do any proper output (like printing to a file, etc.).
The programmer can be sure that it's working by checking the debug messages.
There the programmer can see things like:

    Emitting: count default [nature, 14]

This message comes from the second bolt and it says that it has counted
the word "nature" 14 times.

Copyright &copy; 2014 Miquel Sabaté Solà (mikisabate@gmail.com).
See the LICENSE file.
