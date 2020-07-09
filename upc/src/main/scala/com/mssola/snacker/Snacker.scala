/*
Copyright (C) 2014-2020 Miquel Sabaté Solà <mikisabate@gmail.com>

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

// Scala + Storm.
import net.liftweb.json._
import backtype.storm.{ Config, LocalCluster }
import backtype.storm.topology.{ TopologyBuilder }

// Snacker!
import com.mssola.snacker.aqs.{ AqsComponent }
import com.mssola.snacker.bsp.{ BspComponent }
import com.mssola.snacker.core.{ BaseComponent, Devices }
import com.mssola.snacker.benchmark.{ BenchmarkComponent }


/**
 * This object contains the main class. It loads each component, initializes
 * them and finally submits the topology.
 */
object Snacker {
  // The components to be loaded always.
  var components: Array[BaseComponent] = Array(AqsComponent, BspComponent)

  def main(args: Array[String]) {
    // Handle the arguments now.
    if (args.length > 0) {
      args(0) match {
        case "benchmark" => components ++= Array(BenchmarkComponent)
        case "init" => components foreach { _.initialize }
        case "migrate" => Devices.createTable()
      }
    }

    // Tell each component to build their topology.
    val builder = new TopologyBuilder
    components foreach (c => c.buildTopology(builder))

    // Pretty standard configuration.
    val conf = new Config
    conf.setDebug(true)
    conf.setMaxTaskParallelism(components.size * 2)

    // Finally submit the topology.
    val cluster = new LocalCluster
    cluster.submitTopology("snacker", conf, builder.createTopology)
  }
}
