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

package com.mssola.storm.basic


// Storm imports.
import backtype.storm.task.TopologyContext;
import backtype.storm.{Config, LocalCluster}
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.{Fields, Tuple, Values}
import backtype.storm.topology.base.{BaseBasicBolt, BaseRichSpout}
import backtype.storm.topology.{TopologyBuilder, BasicOutputCollector, OutputFieldsDeclarer}

// Standard things from Java and Scala.
import util.Random
import java.util.Map
import collection.mutable.{HashMap}


/**
 * The RandomSentenceSpout is a spout that generates sentences randomly.
 */
class RandomSentenceSpout extends BaseRichSpout {
  var _collector: SpoutOutputCollector = _
  val _sentences = List("the cow jumped over the moon",
                        "an apple a day keeps the doctor away",
                        "four score and seven years ago",
                        "snow white and the seven dwarfs",
                        "i am at two with nature")

  def open(conf: Map[_,_], context: TopologyContext, collector: SpoutOutputCollector) = {
    _collector = collector
  }

  def nextTuple() = {
    Thread.sleep(100);
    val sentence = _sentences(Random.nextInt(_sentences.length))
    _collector.emit(new Values(sentence));
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("word"))
  }
}

/**
 * The SplitSentence class has the RandomSentenceSpout for the input
 * and the WordCount bolt for the output. It receives a sentence and
 * emits the words of this sentence.
 */
class SplitSentence extends BaseBasicBolt {
  def execute(t: Tuple, collector: BasicOutputCollector) = {
    t.getString(0).split(" ").foreach {
      word => collector.emit(new Values(word))
    }
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("word"))
  }
}

/**
 * The WordCount only has an input, the SplitSentence bolt. It receives
 * a word and keeps track of how many times each word has been visited.
 */
class WordCount extends BaseBasicBolt {
  var counts = new HashMap[String, Integer]().withDefaultValue(0)

  def execute(t: Tuple, collector: BasicOutputCollector) {
    val word = t.getString(0)
    counts(word) += 1
    collector.emit(new Values(word, counts(word)))
  }

  def declareOutputFields(declarer: OutputFieldsDeclarer) = {
    declarer.declare(new Fields("word", "count"));
  }
}

/**
 * The object that holds the main function. It defines the topology which
 * is as follows: RandomSentenceSpout -> SplitSentence -> WordCount. This
 * topology will run locally.
 */
object BasicTopology {
  def main(args: Array[String]) = {
    val builder = new TopologyBuilder
    builder.setSpout("randsentence", new RandomSentenceSpout, 5)
    builder.setBolt("split", new SplitSentence, 8)
    .shuffleGrouping("randsentence")
    builder.setBolt("count", new WordCount, 12)
    .fieldsGrouping("split", new Fields("word"))

    val conf = new Config
    conf.setDebug(true)
    conf.setMaxTaskParallelism(3)

    val cluster = new LocalCluster
    cluster.submitTopology("word-count", conf, builder.createTopology)
    Thread.sleep(10000)
    cluster.shutdown
  }
}
