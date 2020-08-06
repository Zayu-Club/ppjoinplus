package komi.ppjoinplus.models

import scala.collection.mutable

class Record(line: String) {
  val original: String = line
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

  def order(statistics: Map[String, Int]): Unit = {
    tokens = tokens.sortBy(word => statistics(word))
  }

  def prefix(threshold: Double): Int = tokens.length - (threshold * tokens.length).ceil.toInt + 1

  def prefixTokens(threshold: Double):List[String]  = tokens.take(this.prefix(threshold))

  def suffix(threshold: Double): Int = (threshold * tokens.length).ceil.toInt - 1

  def suffixTokens(threshold: Double):List[String]  = tokens.takeRight(this.suffix(threshold))

  override def toString: String = tokens.toString()

}
