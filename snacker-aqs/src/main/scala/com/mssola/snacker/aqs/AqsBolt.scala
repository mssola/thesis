/*
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
*/

package com.mssola.snacker.aqs

// Java.
import java.util.Map

// Storm.
import backtype.storm.tuple.{Fields, Tuple, Values}
import backtype.storm.topology.base.{ BaseBasicBolt }
import backtype.storm.topology.{ BasicOutputCollector, OutputFieldsDeclarer }

class AqsBolt extends BaseBasicBolt {
  def execute(t: Tuple, collector: BasicOutputCollector) = {
    println(t.getString(0))
    println(t.getString(1))
    println(t.getString(2))
    println(t.getString(3))
    // TODO
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    // TODO
  }
}
