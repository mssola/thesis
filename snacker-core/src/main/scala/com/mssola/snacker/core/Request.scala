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

import java.io.InputStreamReader
import scalaj.http.{Http, HttpOptions}

object Request {
  val ApiUrl: String = "http://icity-gw.icityproject.com:8080/developer"

  def apply(uri: String) = {
    // TODO: handle timeout exceptions gracefully
    Http(joinUrl(ApiUrl, uri))
      .param("apikey", apiKey)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(20000))
      .option(HttpOptions.connTimeout(20000))
  }

  // TODO: move this into another class.
  private def joinUrl(url: String, uri: String): String = {
    if (url.endsWith("/")) {
      if (uri.startsWith("/")) {
        return url + uri.slice(1, uri.length - 1)
      }
    } else if (!uri.startsWith("/")) {
      return url + '/' + uri
    }
    url + uri
  }

  private def apiKey: String = {
    try {
      sys.env("SNACKER_API_KEY")
    } catch {
      case no: java.util.NoSuchElementException =>{
        // TODO: proper logger
        println("You have to set the SNACKER_API_KEY environment variable!")
        sys.exit(1)
      }
      case e: Exception =>{
        // TODO: proper logger
        println("Oops! " + e.getMessage)
        sys.exit(1)
      }
    }
  }
}

