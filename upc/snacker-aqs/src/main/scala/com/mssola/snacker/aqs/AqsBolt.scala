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

// Java.
import java.net.Socket

// Storm.
import backtype.storm.tuple.{ Fields, Tuple, Values }
import backtype.storm.topology.base.{ BaseBasicBolt }
import backtype.storm.topology.{ BasicOutputCollector, OutputFieldsDeclarer }

// Other Scala stuff.
import scala.collection.mutable.ListBuffer
import com.newzly.phantom.Implicits._
import net.liftweb.json._
import scala.concurrent.duration._
import scala.concurrent.Await

// Core.
import com.mssola.snacker.core.{ Request, Devices }


/**
 * @class AqsBolt
 *
 * The class AqsBolt is a bolt that belongs to the AQS service. This bolt
 * basically fetches its input to perform a call to the iCity API. After
 * this call has been made, the response is then given to the client.
 */
class AqsBolt extends BaseBasicBolt {
  /**
   * The execute function calls the iCity API with the values given by input
   * in order to complete the API call.
   */
  override def execute(t: Tuple, collector: BasicOutputCollector) = {
    val socket = new Socket("localhost", t.getString(4).toInt)
    val out = socket.getOutputStream()
    out.write(getIntervals(t).getBytes);
    out.flush();
    out.close();
    socket.close();
  }

  /**
   * Not used. It has to be defined to make BaseBasicBolt happy.
   */
  override def declareOutputFields(declarer: OutputFieldsDeclarer) = { }

  /**
   * Get the intervals as expected by the HTTP request.
   */
  private def getIntervals(t: Tuple): String = {
    // Leave early if it's just one user.
    if (t.getString(0) != "all") {
      return Request("/api/observations/interval")
              .param("id", t.getString(0))
              .param("from", t.getString(1))
              .param("to", t.getString(2))
              .param("property", t.getString(3))
              .asString
    }

    // The client wants to get all the users.
    implicit val formats = DefaultFormats
    val devices = Devices.idsFromCity(AqsComponent.cityId)
    var res = new ListBuffer[String]()
    val devs = Await.result(devices, 10 seconds)
    for (d <- devs) {
      // Check that we can call this property.
      if (d._2.contains(t.getString(3))) {
        val v = Request("/api/observations/interval")
                  .param("id", d._1.toString)
                  .param("from", t.getString(1))
                  .param("to", t.getString(2))
                  .param("property", t.getString(3))
                  .asString
        if (v != "") {
          res += v
          println(res(res.length - 1))
        }
      }
    }
    return "[" + res.mkString(",") + "]"
  }
}
