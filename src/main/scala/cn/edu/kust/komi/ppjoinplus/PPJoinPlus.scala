package cn.edu.kust.komi.ppjoinplus

import cn.edu.kust.komi.ppjoinplus.models.Records

import scala.collection.mutable
import scala.math.min

object PPJoinPlus {
  def ppjoin(records: Records, t: Double): Unit = {
    var S: Set[(Int, Int)] = Set[(Int, Int)]()
    //      ppjoin(records, t)
    val I: mutable.HashMap[String, Set[(Int, Int)]] = mutable.HashMap[String, Set[(Int, Int)]]()
    for ((record, recordId) <- records.recordList.zipWithIndex) {
      val A: mutable.HashMap[Int, Int] = mutable.HashMap[Int, Int]()
      val px = record.prefix(t)
      for ((token, tokenIndex) <- record.tokens.take(px).zipWithIndex) {
        I.getOrElseUpdate(token, Set[(Int, Int)]())
          .filter {
            case (targetRecordId, _) => records.recordList(targetRecordId).tokens.length >= t * record.tokens.length
          }
          .foreach {
            case (targetRecordId, tokenIndex) =>
              val unbound = 1 + min(record.tokens.length - tokenIndex, records.recordList(targetRecordId).tokens.length - tokenIndex)
              if (A.getOrElseUpdate(targetRecordId, 0) + unbound >= record.alphaWith(records.recordList(targetRecordId), t))
                A(targetRecordId) = A(targetRecordId) + 1
              else
                A(targetRecordId) = 0
          }
        I(token) = I(token) + ((recordId, tokenIndex))
      }
      println(recordId, record, A)
      //      verify(record, A, alpha)
      for (relevantRecordID <- A.keySet) {
        val py = records.recordList(relevantRecordID).prefix(t)
        val alpha = record.alphaWith(records.recordList(relevantRecordID), t)
        if (px < py) {
          val ubound = A(relevantRecordID) + record.tokens.length - px
          if (ubound >= alpha)
            A(relevantRecordID) = record.tokens.length + records.recordList(relevantRecordID).tokens.length - px
        }
        else {
          val ubound = A(relevantRecordID) + records.recordList(relevantRecordID).tokens.length - py
          if (ubound >= alpha)
            A(relevantRecordID) = record.tokens.length + records.recordList(relevantRecordID).tokens.length - py
        }
        if (A(relevantRecordID) > alpha)
          S = S + ((recordId, relevantRecordID))
      }
    }
    println("I", I)
    println("S", S)
    //    val I: mutable.HashMap[String, ListBuffer[(Int, Int)]] = mutable.HashMap[String, ListBuffer[(Int, Int)]]()
    //
    //    for ((record, id) <- records.recordList.zipWithIndex) {
    //      val A: mutable.HashMap[Int, Int] = mutable.HashMap[Int, Int]()
    //      val p = record.tokens.length - (t * record.tokens.length).ceil.toInt + 1
    //      for ((token, index) <- record.tokens.zipWithIndex) {
    //        I.getOrElseUpdate(token, new ListBuffer[(Int, Int)]()).foreach {
    //          case (y, j) => {
    //            if (records.recordList(y).tokens.length >= record.tokens.length * t) {
    //              val unbound = 1 + min(record.tokens.length - index, records.recordList(y).tokens.length - j)
    //              val alpha = ((t / (1 + t)) * (records.recordList(y).tokens.length + record.tokens.length)).ceil
    //              if (A.keySet.contains(y) && A(y) + unbound >= alpha) A(y) = A.getOrElse(y, 0) + 1
    //              else A.put(y, 0)
    //            }
    //          }
    //        }
    //        I(token) += Tuple2(id, index)
    //      }
    //      println(id, " - ", record, " : ", A)

    //      verify(record, A, alpha)
    //      for (y <- A.keySet) {
    //        val wx = record.tokens(p)
    //        val wy = records.recordList(y).tokens(p)
    //        val O = A(y)
    //      }
  }

}
