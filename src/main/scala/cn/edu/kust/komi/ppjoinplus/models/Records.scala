package cn.edu.kust.komi.ppjoinplus.models

import scala.collection.mutable.ListBuffer

class Records(lines: String*) {
  var recordList: ListBuffer[Record] = ListBuffer[Record]()
  lines.foreach(addRecord)
  var tokenCount: Map[String, Int] = Map[String, Int]()

  def addRecord(line: String): Unit = {
    recordList += new Record(line)
  }

  def init() {
    tokenCount = recordList.flatMap(record => record.tokens).groupBy(identity).mapValues(_.length)
    recordList.foreach(_.order(tokenCount))
  }

  override def toString: String = recordList.toString()
}