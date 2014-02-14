// Copyright (C) 2014 Miquel Sabaté Solà <mikisabate@gmail.com>
// This file is licensed under the MIT license.
// See the LICENSE file.

package com.mssola.storm.socket


// Storm imports.
import backtype.storm.task.TopologyContext;
import backtype.storm.{Config, LocalCluster}
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{Fields, Tuple, Values}
import backtype.storm.topology.base.{BaseBasicBolt, BaseRichSpout}
import backtype.storm.topology.{TopologyBuilder, BasicOutputCollector, OutputFieldsDeclarer}

// Redis client.
import com.redis._

// Standard things from Java and Scala.
import java.util.Map
import java.net.ServerSocket
import java.io.{BufferedReader, InputStreamReader}


/**
 * A spout that just listens to a TCP socket. It then reads a line
 * and emits this line to the following bolt.
 */
class SocketSpout extends BaseRichSpout {
  var collector: SpoutOutputCollector = _
  var server: ServerSocket = _

  def open(conf: Map[_,_], ctx: TopologyContext, col: SpoutOutputCollector) = {
    server = new ServerSocket(9000)
    collector = col
  }

  override def close() = {
    server.close()
  }

  def nextTuple() = {
    val socket = server.accept()
    val stream = new InputStreamReader(socket.getInputStream())
    val in = new BufferedReader(stream)
    val text = in.readLine()
    in.close()
    socket.close()

    collector.emit(new Values(text));
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("text"))
  }
}

/**
 * This bolt will receive words from the SocketSpout and it will store them
 * through Redis. The results are stored in a hash in the "word-count" key.
 */
class RedisBolt extends BaseBasicBolt {
  def execute(t: Tuple, collector: BasicOutputCollector) = {
    val word = t.getString(0)
    val r = new RedisClient("localhost", 6379)
    r.hincrby("word-count", word, 1)
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("word"))
  }
}

/**
 * It defines the topology of this example. The topology in this case is
 * just: SocketSpout -> RedisBolt. Note that the SocketSpout can only handle
 * a level of concurrency of 1, because its implementation is not really
 * very clever.
 */
object SocketTopology {
  def main(args: Array[String]) = {
    val builder = new TopologyBuilder
    builder.setSpout("socket", new SocketSpout, 1)
    builder.setBolt("redis", new RedisBolt, 8)
    .shuffleGrouping("socket")

    val conf = new Config
    conf.setDebug(true)
    conf.setMaxTaskParallelism(3)

    val cluster = new LocalCluster
    cluster.submitTopology("word-count", conf, builder.createTopology)
  }
}
