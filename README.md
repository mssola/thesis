# Snacker [![Build Status](https://travis-ci.org/mssola/thesis.svg?branch=master)](https://travis-ci.org/mssola/thesis)

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
with the value of your API key. In the root directory of this project you'll
find the `env.sh` file. I usually have here my API token and when I want
to run this platform I just perform the following commnad:

    $ source env.sh

This is all you need to know in regards to the iCity API.

## The Storm application

The Storm application is distributed into multiple directories:

* The `src` directory contains the executable.
* The `snacker-core` directory contains the common library being used by the
other components.
* The `snacker-aqs` and the `snacker-bsp` directories contain the services
that I have built for my Thesis.
* The `snacker-benchmark` directory contains a benchmark that is optionally
activated.
* The `project` directory contain all the configuration files to build and run
the Storm application.
* The `storm-example` is an example used in my final report as an appendix.

I have written this application with Scala and I have used SBT as my main tool
of development. Therefore, if you want to build and run this application, you
should download and install SBT first. You can find instructions on how to do
this in SBT's webpage. Once you've got SBT, you can build and run this project
by performing the following commands:

    $ sbt run

Before going any further, note that this application uses Cassandra. Therefore,
the first thing that you have to do is to have Cassandra running. After
this, if it's the first time that you're running this application, you will
need to create the `devices` table. In order to do this, perform the following
command:

    $ sbt
    > run migrate

This migration will create the `devices` in a new keyspace named `snacker`.
After this, you still need to initialize this table. In order to do this you
might want to perform the following command:

    $ sbt
    > run init

Note that this will run the application after the initialization process has
succeeded.

## The API layer

TO do

# License

Copyright (C) 2014 Miquel Sabaté Solà <mikisabate@gmail.com>

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

