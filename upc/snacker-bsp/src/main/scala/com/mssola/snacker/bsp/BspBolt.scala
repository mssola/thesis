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


// Java.
import java.net.Socket

// Storm.
import backtype.storm.tuple.{ Fields, Tuple, Values }
import backtype.storm.topology.base.{ BaseBasicBolt }
import backtype.storm.topology.{ BasicOutputCollector, OutputFieldsDeclarer }

// Other Scala stuff.
import scala.collection.mutable.ListBuffer
import net.liftweb.json._
import scala.concurrent.duration._
import scala.concurrent.Await

// Core.
import com.mssola.snacker.core.{ Devices }


/**
 * @class BspBolt
 *
 * The class BspBolt is a bolt that belongs to the BSP service. This bolt
 * basically fetches its input to perform a call to the iCity API. After
 * this call has been made, the response is then given to the client.
 */
class BspBolt extends BaseBasicBolt {
  /**
   * The execute function calls the iCity API with the values given by input
   * in order to complete the API call.
   */
  def execute(t: Tuple, collector: BasicOutputCollector) = {
    try {
      val socket = new Socket("localhost", t.getString(1).toInt)
      val out = socket.getOutputStream()
      out.write(generateJSON(t).getBytes);
      out.flush();
      out.close();
      socket.close();
    } catch {
      // Even if the bolt is protecting us for this case, I prefer to
      // check this exception anyways...
      case e: java.net.ConnectException => {
        println("The client has closed the connection.")
      }
    }
  }

  /**
   * Not used. It has to be defined to make BaseBasicBolt happy.
   */
  def declareOutputFields(declarer: OutputFieldsDeclarer) = { }

  /**
   * Returns the JSON response for the given tuple of values.
   *
   * This tuple of values has been passed by the BspSpout. This function
   * is safe: exceptions and errors are already handled here. If something
   * goes wrong, an empty string will be returned.
   */
  private def generateJSON(t: Tuple): String = {
    val id = t.getString(0)
    var result = collection.mutable.Map[String, Map[String,String]]()
    implicit val formats = DefaultFormats

    // Get the value for the requested device or devices.
    if (id == "all") {
      val devices = Devices.idsFromCity(BspComponent.cityId)
      var res = new ListBuffer[String]()
      val devs = Await.result(devices, 10 seconds)
      devs foreach (d => result(d._1.toString) = getResponse(d._1.toString))
    } else {
      result(id) = getResponse(id)
    }

    // Serialize the response and return it.
    val json = Serialization.write(result.toMap)
    return json
  }

  /**
   * Returns a Map containing the response for the given id.
   *
   * Note that this function will request for all the properties available
   * for the device with the given id.
   */
  private def getResponse(id: String): Map[String, String] = {
    val ds = Devices.getProperties(id.toInt)
    val properties = Await.result(ds, 10 seconds)

    var values = collection.mutable.Map[String,String]()
    for (p <- properties) {
      p foreach { prop =>
        val value = Response(id, prop)
        values(prop) = value
      }
    }
    return values.toMap
  }
}
