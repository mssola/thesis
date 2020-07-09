/*
Copyright (C) 2014-2020 Miquel Sabaté Solà <mikisabate@gmail.com>

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


/**
 * This object is the one to be used when performing a request to the
 * iCity API.
 *
 * The only public function is the apply function. It handles timeouts,
 * the content-type, the charset, etc.
 */
object Request {
  // The base URL to the iCity API.
  val ApiUrl: String = "http://icity-gw.icityproject.com:8080/developer"

  // The timeout to be used in the apply function.
  val Timeout = 20000

  /**
   * Perform an HTTP request to the given URI.
   *
   * The timeout being used is the one set at the Request.Timeout constant.
   * NOTE: This function does not handle timeout exceptions.
   * NOTE: this function will kill the application if the "SNACKER_API_KEY"
   * environment variable is not set.
   */
  def apply(uri: String) = {
    Http(joinUrl(ApiUrl, uri))
      .param("apikey", apiKey)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(Request.Timeout))
      .option(HttpOptions.connTimeout(Request.Timeout))
  }

  /**
   * Returns both of the given parameters joint by a slash.
   */
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

  /**
   * Returns the API key.
   *
   * If the "SNACKER_API_KEY" is not set, the application will be killed.
   */
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

