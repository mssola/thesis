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

import com.mssola.snacker.core.{ Base, BaseComponent }
import net.liftweb.json._
import backtype.storm.topology.{ TopologyBuilder }

object BspComponent extends BaseComponent {
  override def cityId = Base.Barcelona

  override def initialize() = {
    implicit val formats = DefaultFormats
    val res = parse(devices().asString).extract[List[DeviceJSON]]
    for (d <- res) {
      val dev = new Device(d.deviceID.toInt, d.name, d.cityID.toInt,
                           d.longitude.toDouble, d.latitude.toDouble,
                           d.properties)
      Devices.insertDevice(dev)
    }
  }

  override def buildTopology(builder: TopologyBuilder) = {
    // TODO: vals...
    builder.setSpout("bsps", new BspSpout, 1)
    builder.setBolt("bspb", new BspBolt, 8).shuffleGrouping("bsps")
  }
}
