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

package snacker

import java.util.UUID
import org.joda.time.DateTime
import com.mssola.core.Request
import net.liftweb.json._

object Snacker {
  def main(args: Array[String]) {
    println(args.length)
    println(args)
    return
    val r = Request("/api/cities/3/devices").asString
    implicit val formats = DefaultFormats
    val devices = parse(r).extract[List[DeviceJSON]]
    for (d <- devices) {
      val dev = new Device(d.deviceID.toInt, d.name, d.cityID.toInt,
                           d.longitude.toDouble, d.latitude.toDouble)
//       Devices.insertDevice(dev)
      println(dev)
    }
  }
}
