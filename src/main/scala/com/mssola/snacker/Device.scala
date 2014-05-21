
package snacker

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
