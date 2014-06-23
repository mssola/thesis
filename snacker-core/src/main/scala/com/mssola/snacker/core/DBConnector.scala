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

package com.mssola.snacker.core

import scala.concurrent. { blocking, Future }
import com.datastax.driver.core.{ Cluster, Session }
import com.newzly.phantom.Implicits._


/**
 * This class does the magic to allow this application to connect with
 * Cassandra. It assumes that the keyspace is "snacker" and that Cassandra
 * is listening to the port "9042" (Cassandra's default).
 */
object DBConnector {
  /**
   * The key space.
   */
  val keySpace = "snacker"

  /**
   * The port of Cassandra.
   */
  val cassandraPort = 9042

  /**
   * This is our cluster object. With this object we can create
   * new sessions.
   */
  lazy val cluster =  Cluster.builder()
    .addContactPoint("localhost")
    .withPort(cassandraPort)
    .withoutJMXReporting()
    .withoutMetrics()
    .build()

  /**
   * A session is a connection to Cassandra.
   */
  lazy val session = blocking {
    cluster.connect(keySpace)
  }
}

/**
 * All the classes that map a resource into Cassandra has to extend this trait.
 */
trait DBConnector {
  // Magic!
  self: CassandraTable[_, _] =>

  /**
   * Create the table for this resource.
   */
  def createTable(): Future[Unit] = create.future() map (_ => ())

  // Magic!
  implicit lazy val datastax: Session = DBConnector.session
}
