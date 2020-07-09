/*
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
*/

package com.mssola.snacker.bsp

// Scala + Java.
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Map
import java.net.{ Socket, ServerSocket }
import java.io.{ BufferedReader, InputStreamReader }

// Storm.
import backtype.storm.task.TopologyContext;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{ Fields, Tuple, Values }
import backtype.storm.topology.base.{ BaseRichSpout }
import backtype.storm.topology.{ OutputFieldsDeclarer }


/**
 * @class BspSpout
 *
 * This class is the spout for the BSP service.
 *
 * It will run a separate thread in the background that will listen to a
 * TCP socket in order to fetch subscription requests from the API layer. The
 * nextTuple function consists on iterating over the subscriptions to update
 * them in the BspBolt.
 *
 * This spout will also handle closed clients gracefully. This spout assumes
 * that when a client goes down (regardless of the causes), it must unsubscribe
 * this same client.
 */
class BspSpout extends BaseRichSpout {
  var collector: SpoutOutputCollector = _
  val list: collection.mutable.Map[String, String] = collection.mutable.Map()

  override def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
    collector = col

    /*
     * Run a server in the background. This server will listen to the API
     * layer for new subscriptions.
     */
    future {
      val server = new ServerSocket(8002)

      // Hey, listen!
      while (true) {
        val socket = server.accept()
        val stream = new InputStreamReader(socket.getInputStream())
        val in = new BufferedReader(stream)
        val text = in.readLine()
        in.close()

        // Parse the parameters.
        val ary = text.split("&")
        if (ary.length == 2 && ary(0) == "destroy") {
          list -= ary(1)
        } else if (ary.length == 3 && ary(0) == "create") {
          list(ary(1).trim) = ary(2).trim
        }
      }
    }
  }

  override def nextTuple() = {
    Thread.sleep(2000)

    // Update all the open sockets.
    list foreach { case (id, port) =>
      try {
        // Check that the connection is still open.
        val socket = new Socket("localhost", port.toInt)
        socket.close()

        // Emit the data.
        collector.emit(new Values(id, port))
      } catch {
        case e: java.net.ConnectException => {
          // This means that the client has closed the connection: unsubscribe!
          list -= id
        }
      }
    }
  }

  override def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("id", "port"))
  }
}
