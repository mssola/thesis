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

package com.mssola.snacker.benchmark


// TODO: sort this mess

import com.mssola.snacker.core.{ Base, BaseComponent }
import net.liftweb.json._

import java.util.Map

// Storm.
import backtype.storm.tuple.{Fields, Tuple, Values}
import backtype.storm.topology.base.{ BaseBasicBolt }
import backtype.storm.topology.{ BasicOutputCollector, OutputFieldsDeclarer }
import backtype.storm.topology.{ TopologyBuilder }

import scala.concurrent.ExecutionContext.Implicits.global
import java.net.ServerSocket
import java.io.{BufferedReader, InputStreamReader, FileWriter}

// Storm.
import backtype.storm.task.TopologyContext;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.topology.base.{ BaseRichSpout }

import net.liftweb.json._
import scalaj.http.{Http, HttpOptions}

class BenchmarkSpout extends BaseRichSpout {
  var collector: SpoutOutputCollector = _

  override def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
    collector = col
  }

  override def nextTuple() = {
    val value = Http("http://127.0.0.1:8080/").asString
    collector.emit(new Values(value))
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("value"))
  }
}

class BenchmarkBolt extends BaseBasicBolt {
  var counter = 0

  def execute(t: Tuple, collector: BasicOutputCollector) = {
    val value = t.getString(0)
    val fw = new FileWriter("/tmp/snacker.txt", true)
    try {
      counter += 1
      fw.write(value + " " + counter + "\n")
    }
    finally fw.close()
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("value"))
  }
}

object BenchmarkComponent extends BaseComponent {
  override def cityId = Base.London

  override def buildTopology(builder: TopologyBuilder) = {
    builder.setSpout("benchmarks", new BenchmarkSpout, 1)
    builder.setBolt("benchmarkb", new BenchmarkBolt, 8).shuffleGrouping("benchmarks")
  }
}
