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

package com.mssola.snacker

import java.util.UUID
import org.joda.time.DateTime
import com.mssola.snacker.core.{ Request, BaseComponent }
import com.mssola.snacker.aqs.AqsComponent
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




object Snacker {
  // TODO: try to avoid the new thingie.
  val components: Array[BaseComponent] = Array(new AqsComponent)

  def initialize() = components foreach (c => c.initialize)

  def main(args: Array[String]) {
    // Initialize some settings if specified.
    if (args.length > 0) {
      initialize()
    }

    val builder = new TopologyBuilder
    components foreach (c => c.buildTopology(builder))

    val conf = new Config
    conf.setDebug(true)
    conf.setMaxTaskParallelism(components.size * 2)

    val cluster = new LocalCluster
    cluster.submitTopology("snacker", conf, builder.createTopology)
  }
}
