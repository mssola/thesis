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

// package com.mssola.snacker ?
package snacker

import java.util.UUID
import org.joda.time.DateTime
import com.mssola.core.Request
import net.liftweb.json._

// Storm. TODO: sort this mess.
import backtype.storm.task.TopologyContext;
import backtype.storm.{Config, LocalCluster}
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{Fields, Tuple, Values}
import backtype.storm.topology.base.{BaseBasicBolt, BaseRichSpout}
import backtype.storm.topology.{TopologyBuilder, BasicOutputCollector, OutputFieldsDeclarer}

// Java thingies. TODO: sort this mess
import java.util.Map
import java.net.ServerSocket
import java.io.{BufferedReader, InputStreamReader}



class AqsSpout extends BaseRichSpout {
  override def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
    // TODO
  }

  override def close() = {
    // TODO
  }

  override def nextTuple() = {
    // TODO
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    // TODO
  }
}

class AqsBolt extends BaseBasicBolt {
  def execute(t: Tuple, collector: BasicOutputCollector) = {
    // TODO
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    // TODO
  }
}

// TODO: rename
object Main {
  def initialize() = {
    // TODO: specialize this guy.

    val r = Request("/api/cities/3/devices").asString
    implicit val formats = DefaultFormats
    val devices = parse(r).extract[List[DeviceJSON]]
    for (d <- devices) {
      val dev = new Device(d.deviceID.toInt, d.name, d.cityID.toInt,
                           d.longitude.toDouble, d.latitude.toDouble)
      Devices.insertDevice(dev)
    }
  }

  def main(args: Array[String]) {
    // Initialize some settings if specified.
    if (args.length > 0) {
      initialize()
    }

    val builder = new TopologyBuilder
    builder.setSpout("aqss", new AqsSpout, 1)
    builder.setBolt("aqsb", new AqsBolt, 8).shuffleGrouping("aqss")

    val conf = new Config
    conf.setDebug(true)
    conf.setMaxTaskParallelism(4)

    val cluster = new LocalCluster
    cluster.submitTopology("aqs", conf, builder.createTopology)
  }
}
