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

package com.mssola.snacker.bsp

// Scala + Java.
import java.util.Map
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.ServerSocket
import java.io.{BufferedReader, InputStreamReader}

// Storm.
import backtype.storm.task.TopologyContext;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{ Fields, Tuple, Values }
import backtype.storm.topology.base.{ BaseRichSpout }
import backtype.storm.topology.{ OutputFieldsDeclarer }

import net.liftweb.json._

class BspSpout extends BaseRichSpout {
//   var _devices = Seq[(Int, List[String])]()
  var collector: SpoutOutputCollector = _


  override def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
//     Devices.idsFromCity(3) onSuccess {
//       case devs => { _devices = devs }
//     }

    BspSpout.server = new ServerSocket(BspSpout.PORT)
    collector = col
  }

  override def nextTuple() = {
    val socket = BspSpout.server.accept()
    val stream = new InputStreamReader(socket.getInputStream())
    val in = new BufferedReader(stream)
    val text = in.readLine()
    in.close()

    // TODO: check things
    println(text)
    val ary = text.split(":")
    collector.emit(new Values(ary(0), ary(1)))
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("id", "port"))
  }
}

object BspSpout {
  val PORT = 8002
  var server: ServerSocket = _
}
