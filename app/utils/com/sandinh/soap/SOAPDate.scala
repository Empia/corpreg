package com.sandinh.soap

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat.{yearMonthDay, dateHourMinuteSecond}

class SOAPDate(date: DateTime, dateFormatter: DateTimeFormatter) {
  override def toString = dateFormatter.print(date)
  def toDate = date
}

object SOAPDate {
  def apply(date: DateTime) = new SOAPDate(date, dateHourMinuteSecond)
  def apply(dateText: String) = new SOAPDate(textToDate(dateText), dateHourMinuteSecond)

  def textToDate(dateText: String): DateTime = {
    if (dateText.length == 10) //"yyyy-MM-dd".length
      yearMonthDay.parseDateTime(dateText)
    else
      dateHourMinuteSecond.parseDateTime(dateText)
  }
}
