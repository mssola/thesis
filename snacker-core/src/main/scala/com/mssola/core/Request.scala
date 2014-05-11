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

package com.mssola.core

import java.io.InputStreamReader
import com.twitter.logging.Level
import scalaj.http.Http

object Request {
  val ApiUrl: String = "http://icity-gw.icityproject.com:8080/developer"

  def apply(uri: String) = {
    Http(ApiUrl + uri).param("apikey", apiKey)
  }

  private def apiKey: String = {
    try {
      sys.env("SNACKER_API_KEY")
    } catch {
      case no: java.util.NoSuchElementException =>{
        log(Level.FATAL, "You have to set the SNACKER_API_KEY env variable")
        sys.exit(1)
      }
      case e: Exception =>{
        log.error(e, "Oops! %s", e.getMessage)
        sys.exit(1)
      }
    }
  }
}

