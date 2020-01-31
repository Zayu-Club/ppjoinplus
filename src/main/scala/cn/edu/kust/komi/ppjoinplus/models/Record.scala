package cn.edu.kust.komi.ppjoinplus.models

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Record(val line: String) {
  val original: String = line
  var tokens: List[String] = normalize()

  def normalize(): List[String] = {
    //    val wordCount = line.split(" ").filter(_.nonEmpty).groupBy(identity).mapValues(_.length)
    //    val tokenBuffer = new ListBuffer[String]
    //    wordCount.foreach {
    //      case (k, v) => {
    //        for (i <- 0 until v)
    //          if (i == 0) tokenBuffer += k
    //          else tokenBuffer += k.concat(i.toString)
    //      }
    //    }
    val wordCount = new mutable.HashMap[String, Int]()
    val tokenBuffer = new ListBuffer[String]
    line.split(" ").filter(_.nonEmpty).foreach {
      word => {
        wordCount.getOrElseUpdate(word, 0) match {
          case 0 => tokenBuffer += word
          case index => tokenBuffer += word.concat(index.toString)
        }
        wordCount.put(word, wordCount(word) + 1)
      }
    }
    tokenBuffer.toList
  }

  def order(statistics: Map[String, Int]): Unit = {
    tokens = tokens.sortBy(word => statistics(word))
  }

  def prefix(t: Double): Int = {
    tokens.length - (t * tokens.length).ceil.toInt + 1
  }

  def alphaWith(target: Record, t: Double): Int = {
    ((t / (1 + t)) * (tokens.length + target.tokens.length)).ceil.toInt
  }

  override def toString: String = tokens.toString()
}