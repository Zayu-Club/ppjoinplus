package cn.edu.kust.komi.ppjoinplus

import cn.edu.kust.komi.ppjoinplus.models.Records

object Main {
  def main(args: Array[String]): Unit = {
    //    RecordTestMain()
    PPJoinPlusTestMain()
  }

  def PPJoinPlusTestMain(): Unit = {
    val records = new Records("C D F", "A B E F G", "A B C D E", "B C D E F")
    records.init()
    println(records)
    PPJoinPlus.ppjoin(records, 0.5)
  }

  def RecordTestMain(): Unit = {
    //    val r1: Record = new Record("yes as soon as possible")
    //    val r2: Record = new Record("as soon as possible please")

    //    println(r1.token)
    //    println(r2.token)

    //    val statistics: Map[String, Int] = Map[String, Int](
    //      "yes" -> 1,
    //      "as" -> 2,
    //      "soon" -> 2,
    //      "as1" -> 2,
    //      "possible" -> 2,
    //      "please" -> 1
    //    )

    //    val records = new Records(List(r1, r2))
    //    println(records.tokenCount)
    //    r1.order(records.tokenCount)
    //    r2.order(records.tokenCount)
    //    println(r1.token)
    //    println(r2.token)
    //    println(records.tokenCount)
    //    println(records.recordList)
    val records = new Records("yes as soon as possible")
    records.addRecord("as soon as possible please")
    records.init()
    println(records)

  }
}