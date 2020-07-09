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

package com.mssola.snacker.aqs

// Scala + Java.
import java.util.Map
import java.net.ServerSocket
import java.io.{ BufferedReader, InputStreamReader }

// Storm.
import backtype.storm.task.TopologyContext;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{ Fields, Tuple, Values }
import backtype.storm.topology.base.{ BaseRichSpout }
import backtype.storm.topology.{ OutputFieldsDeclarer }


/**
 * @class AqsSpout
 *
 * This is the spout for the AQS service. It just reads the data as given by
 * the API layer, it then validates the given data and passes this data
 * to the AqsBolt.
 */
class AqsSpout extends BaseRichSpout {
  var collector: SpoutOutputCollector = _

  override def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
    AqsSpout.server = new ServerSocket(AqsSpout.PORT)
    collector = col
  }

  override def nextTuple() = {
    // Get the request from the API layer.
    val socket = AqsSpout.server.accept()
    val stream = new InputStreamReader(socket.getInputStream())
    val in = new BufferedReader(stream)
    val text = in.readLine()
    in.close()

    // Emit what we have got.
    val ary = text.split("&")
    val p = trimName(ary(3))
    collector.emit(new Values(ary(0), ary(1), ary(2), p, ary(4)))
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("id", "from", "to", "property", "port"))
  }

  /**
   * Returns the property name as expected by the iCity API.
   */
  def trimName(s: String): String = "urn:air_quality" + s.toUpperCase
}

/**
 * The companion object just stores some useful constants.
 */
object AqsSpout {
  // The port that this spout will listen to.
  val PORT = 8001

  // The server socket that will read API instructions.
  var server: ServerSocket = _
}
