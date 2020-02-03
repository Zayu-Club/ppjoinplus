package cn.edu.kust.komi.ppjoinplus.models

import scala.collection.mutable

class Record(line: String) {
  val original: String = line
  var prefix: Int = 0
  var tokens: List[String] = normalize()

  def normalize(): List[String] = {
    val wordCount = new mutable.HashMap[String, Int]()
    val tokenBuffer = new mutable.ListBuffer[String]
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

  def order(statistics: Map[String, Int], t: Double): Unit = {
    tokens = tokens.sortBy(word => statistics(word))
    prefix = tokens.length - (t * tokens.length).ceil.toInt + 1
  }

  //  def prefix(t: Double): Int = tokens.length - (t * tokens.length).ceil.toInt + 1

  def alphaWith(target: Record, t: Double): Int = ((t / (1 + t)) * (tokens.length + target.tokens.length)).ceil.toInt

  override def toString: String = tokens.toString()

}
