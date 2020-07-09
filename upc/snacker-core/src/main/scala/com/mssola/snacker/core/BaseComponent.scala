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

import backtype.storm.topology.{ TopologyBuilder }

// TODO: move to its own file.
object Base {
  val London = 3
  val Barcelona = 7
  val Genoa = 8
  val Bologna = 9
}

trait BaseComponent {
  def cityId: Int
  def initialize() = {}

  def devices() = Request("/api/cities/" + cityId + "/devices")

  def buildTopology(builder: TopologyBuilder)
}
