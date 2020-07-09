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


// JSON stuff.
import com.newzly.phantom.Implicits._
import net.liftweb.json._

// Snacker!
import com.mssola.snacker.core.Request


/**
 * The response as given by the iCity API.
 */
case class ResponseJSON(
  time: String,
  value: String,
  units: String
)

/**
 * The object Response takes care of calling the iCity API for the BSP service.
 *
 * It only defines the apply function that already does the magic.
 */
object Response {
  /**
   * Perform a request to the iCity API to get the value of the given
   * id and property pair.
   *
   * This function already checks errors. If an error occurred (p.e. the
   * server returned a 500 status code), then an empty string is returned.
   */
  def apply(id: String, property: String): String = {
    // We are limitied by the BSP service to 10 queries/second.
    Thread.sleep(100)

    try {
      val response = Request("/api/observations/last")
                      .param("id", id)
                      .param("n", "1")
                      .param("property", property)
                      .asString

      if (response != "") {
        implicit val formats = DefaultFormats
        val res = parse(response).extract[ResponseJSON]
        return res.value
      }
    } catch {
      case e: scalaj.http.HttpException => {
        println("Server returned 500")
        return ""
      }
    }
    return ""
  }
}
