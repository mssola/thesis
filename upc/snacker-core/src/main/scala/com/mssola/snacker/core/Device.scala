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

package com.mssola.snacker.core

import com.newzly.phantom.Implicits._
import scala.concurrent.{ Future => ScalaFuture }
import com.datastax.driver.core.{ ResultSet, Row }


/**
 * Represents an iCity device. This case class is used to map a device
 * as stored in Cassandra.
 */
case class Device(
  id: Int,
  name: String,
  cityId: Int,
  longitude: Double,
  latitude: Double,
  properties: List[String]
)

/**
 * This is a helper class that maps a device as given by JSON responses.
 */
case class DeviceJSON(
  deviceID: String,
  name: String,
  cityID: String,
  longitude: String,
  latitude: String,
  properties: List[String]
)

/**
 * This is the class that interacts with Cassandra regarding devices.
 *
 * It uses the Device case class to map each device.
 */
sealed class Devices extends CassandraTable[Devices, Device] {
  object id extends IntColumn(this) with PartitionKey[Int]
  object name extends StringColumn(this)
  object cityId extends IntColumn(this) with Index[Int]
  object longitude extends DoubleColumn(this)
  object latitude extends DoubleColumn(this)
  object properties extends ListColumn[Devices, Device, String](this)

  /**
   * This method is used by Phantom to map a device to a Device object.
   */
  override def fromRow(row: Row): Device = {
    Device(
      id(row),
      name(row),
      cityId(row),
      longitude(row),
      latitude(row),
      properties(row)
    )
  }
}

/**
 * The companion object for the Devices class. This object should be used
 * to interact with the DB.
 */
object Devices extends Devices with DBConnector {
  /**
   * Eventually insert the given Device into Cassandra.
   */
  def insertDevice(p: Device): ScalaFuture[ResultSet] = {
    insert.value(_.id, p.id)
      .value(_.name, p.name)
      .value(_.cityId, p.cityId)
      .value(_.longitude, p.longitude)
      .value(_.latitude, p.latitude)
      .value(_.properties, p.properties)
      .future()
  }

  /**
   * Fetch the id and the properties of the devices from the given city.
   *
   * This method returns a Scala future.
   */
  def idsFromCity(id: Int): ScalaFuture[Seq[(Int, List[String])]] = {
    select(_.id, _.properties).where(_.cityId eqs id).fetch
  }

  // TODO
  def getProperties(id: Int): ScalaFuture[Seq[List[String]]] = {
    select(_.properties).where(_.id eqs id).fetch
  }
}
