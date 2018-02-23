package com.emilabraham.marveltimemachine

import org.joda.time.DateTime
import java.util.logging.Logger

class Comic(val title: String, var imagePath: String, var description: String, var dates: List<ComicDate>) {
    private val log = Logger.getLogger(Comic::class.java.name)

    //Return the date the comic went on sale.
    fun getSaleDate() :DateTime {
        var dateToReturn: DateTime = DateTime()

        dates.forEach { date ->
            if (date.type.equals("onsaleDate")) {
                dateToReturn =  DateTime(date.date)
            }
        }

        return dateToReturn
    }
}