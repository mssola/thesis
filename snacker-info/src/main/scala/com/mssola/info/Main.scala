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

import scalaj.http.Http

object Main {
  def main(args: Array[String]) = {
    val v = Http("http://icity-gw.icityproject.com:8080/developer/api/cities")
      .param("apikey", "l7xx3d894c125a8f4166acdaecae5df8d1c9").asString


    println("here")
  }
}
