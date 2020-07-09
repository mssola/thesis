# Thesis [![Build Status](https://travis-ci.org/mssola/thesis.svg?branch=master)](https://travis-ci.org/mssola/thesis)

## About this repository

This repository holds all the files that form my Bachelor Degree Thesis. It
contains everything: documentation, the different layers of software, etc.

The documentation has been written in LaTeX and it's under the `docs`
directory. The rest of the directories are the source code that I have
used for my Thesis.

In order to have a better understanding of my Thesis, I recommend reading
my final report. This report can be found inside the `docs/report` directory.

## The iCity Platform

This application performs all the requests to the [iCity](http://icity-devp.icityproject.com/)
Project. This means that before doing anything you have to be a registered
developer of the platform.

The signup process is quite easy. After that you will receive and API token.
Finally, you'll need to set the `SNACKER_API_KEY` environment variable
with the value of your API key. I usually have this value in an `env.sh` file
in the root of the project. This way, when I want to run this platform I
just perform the following commnad:

    $ source env.sh

Note that the `.gitignore` file already ignores a file named `env.sh` in the
root directory.

## The Storm application

The Storm application is distributed into multiple directories (as you will
probably guess, this application is called "snacker" internally :P):

* The `src` directory contains the executable.
* The `snacker-core` directory contains the common library being used by the
other components.
* The `snacker-aqs` and the `snacker-bsp` directories contain the services
that I have built for my Thesis.
* The `snacker-benchmark` directory contains a benchmark that is optionally
activated.
* The `project` directory contains all the configuration files to build and run
the Storm application.
* The `storm-example` is an example used in my final report as an appendix.

I have written this application with Scala and I have used SBT as my main tool
of development. Therefore, if you want to build and run this application, you
should download and install SBT first. You can find instructions on how to do
this in SBT's webpage. Once you've got SBT, you can build and run this project
by performing the following command:

    $ sbt
    > run

Before going any further, note that this application uses Cassandra. Therefore,
the first thing that you have to do is to have Cassandra running. After
this, if it's the first time that you're running this application, you will
need to create the `devices` table. In order to do this, perform the following
commands:

    $ sbt
    > run migrate

This migration will create the `devices` table in a new keyspace named
`snacker`. After this, you still need to initialize this table. In order to
do this you might want to perform the following commands:

    $ sbt
    > run init

Note that this will run the application after the initialization process has
succeeded.

To run this project you need a version of Scala from the 2.10.x branch.
Moreover, I'm using SBT's 0.13 version, but I haven't tested any other versions
really. This application works in both OpenJDK7 and OracleJDK7.

## The API layer

The API layer is written in the Go programming language. All the code for
the API layer is contained in the file `api/server.go`. This file requires
Go 1.1 or higher.

If you don't have Go installed, you might find really useful the instructions
given in the [website](http://golang.org/doc/install) of the project. To
build the API layer you just need to perform the following command inside the
`api` directory:

    $ go build server.go

This will create the `server` executable file. Run this executable file to
start the API layer. Pretty easy, isn't it? :)

# License

```
Copyright (C) 2014-2015 Miquel Sabaté Solà <mikisabate@gmail.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
