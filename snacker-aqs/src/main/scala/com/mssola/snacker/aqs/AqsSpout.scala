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

// Scala + Java.
import java.util.Map
import scala.concurrent.ExecutionContext.Implicits.global

// Storm.
import backtype.storm.task.TopologyContext;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{ Fields, Tuple, Values }
import backtype.storm.topology.base.{ BaseRichSpout }
import backtype.storm.topology.{ OutputFieldsDeclarer }

import net.liftweb.json._

class AqsSpout extends BaseRichSpout {
  var devices = Seq[(Int, List[String])]()
  var counter = 0

  val globalTime = 5000
  val requestTime = 200

  override def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
    Devices.idsFromCity(3) onSuccess {
      case devs => {
        devices = devs
        counter = devices.length - 1
      }
    }
  }

//   def getValue(id: Int): Int = {
//     implicit val formats = DefaultFormats
//     val req = Request("/devices/$id").asString
//     val res = parse(req).extract[List[DeviceJSON]]
//     cj
//   }

  override def nextTuple() = {
    // If we've completed a round, sleep for globalTime seconds before
    // doing another round.
    if (counter < 0) {
      counter = devices.length - 1
      Thread.sleep(globalTime)
    } else {
      // Sleep so we don't make too many requests to iCity...
      Thread.sleep(requestTime)
    }

    // Fetch the current value for the given device.
//     val value = getValue(devices(counter))
    counter -= 1
    // TODO: send value
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("value"))
  }
}
