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

case class Property(
  id: UUID,
  name: String,
  value: String,
  deviceId: Int,
  createdAt: DateTime
)

sealed class Properties extends CassandraTable[Properties, Property] {
  object id extends  UUIDColumn(this) with PartitionKey[UUID]
  object name extends StringColumn(this)
  object value extends StringColumn(this)
  object deviceId extends IntColumn(this)
  object createdAt extends DateTimeColumn(this)

  def fromRow(row: Row): Property = {
    Property(
      id(row),
      name(row),
      value(row),
      deviceId(row),
      createdAt(row)
    )
  }
}

object Properties extends Properties with DBConnector {
  def insertNewRecord(p: Property): ScalaFuture[ResultSet] = {
    insert.value(_.id, p.id)
      .value(_.name, p.name)
      .value(_.value, p.value)
      .value(_.deviceId, p.deviceId)
      .value(_.createdAt, p.createdAt)
      .future()
  }

  def insertValue(id: Int, name: String, value: String): ScalaFuture[ResultSet] = {
    val r = new Property(UUID.randomUUID(), name, value, id, DateTime.now())
    insertNewRecord(r)
  }
}
