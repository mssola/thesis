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

import com.mssola.snacker.core.DBConnector
import java.util.UUID
import scala.concurrent.{ Future => ScalaFuture }
import org.joda.time.DateTime
import com.newzly.phantom.Implicits._
import com.datastax.driver.core.{ ResultSet, Row }

case class Device(
  id: Int,
  name: String,
  cityId: Int,
  longitude: Double,
  latitude: Double
)

case class DeviceJSON(
  deviceID: String,
  name: String,
  cityID: String,
  longitude: String,
  latitude: String
)

sealed class Devices extends CassandraTable[Devices, Device] {
  object id extends  IntColumn(this) with PartitionKey[Int]
  object name extends StringColumn(this)
  object cityId extends  IntColumn(this)
  object longitude extends  DoubleColumn(this)
  object latitude extends  DoubleColumn(this)

  def fromRow(row: Row): Device = {
    Device(
      id(row),
      name(row),
      cityId(row),
      longitude(row),
      latitude(row)
    )
  }
}

object Devices extends Devices with DBConnector {
  def insertDevice(p: Device): ScalaFuture[ResultSet] = {
    insert.value(_.id, p.id)
      .value(_.name, p.name)
      .value(_.cityId, p.cityId)
      .value(_.longitude, p.longitude)
      .value(_.latitude, p.latitude)
      .future()
  }
}
