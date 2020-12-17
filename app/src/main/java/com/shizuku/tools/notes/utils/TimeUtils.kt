package com.shizuku.tools.notes.utils

import java.util.*

class TimeUtils {
    companion object {
        @JvmStatic
        fun getMills(): Long {
            return Calendar.getInstance().timeInMillis
        }

        @JvmStatic
        fun display(time: Long): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = time
            return displayDateTime(cal)
        }

        @JvmStatic
        fun displayDateTime(cal: Calendar): String {
            return when (cal.timeInMillis) {
                in todayStart()..todayEnd() -> displayTime(cal)
                else -> displayDate(cal)
            }
        }

        @JvmStatic
        fun displayDate(cal: Calendar): String {
            return when (cal.timeInMillis) {
                in thisYearStart()..thisYearEnd() -> {
                    (cal.get(Calendar.MONTH) + 1).toString() + "-" +
                            cal.get(Calendar.DAY_OF_MONTH).toString()
                }
                else -> {
                    cal.get(Calendar.YEAR).toString() + "-" +
                            (cal.get(Calendar.MONTH) + 1).toString() + "-" +
                            cal.get(Calendar.DAY_OF_MONTH).toString()
                }
            }
        }

        @JvmStatic
        fun displayTime(cal: Calendar): String {
            var min = cal.get(Calendar.MINUTE).toString()
            if (min.length <= 1) {
                min = "0$min"
            }
            return cal.get(Calendar.HOUR_OF_DAY).toString() + ":" + min
        }

        @JvmStatic
        fun todayStart(): Long {
            val c: Calendar = Calendar.getInstance()
            c.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
            )
            return c.timeInMillis
        }

        @JvmStatic
        fun todayEnd(): Long {
            val c: Calendar = Calendar.getInstance()
            c.set(
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH),
                23,
                59,
                59
            )
            return c.timeInMillis
        }

        @JvmStatic
        fun thisYearStart(): Long {
            val c: Calendar = Calendar.getInstance()
            c.set(
                c.get(Calendar.YEAR),
                0,
                1,
                0,
                0,
                0
            )
            return c.timeInMillis
        }

        @JvmStatic
        fun thisYearEnd(): Long {
            val c: Calendar = Calendar.getInstance()
            c.set(
                c.get(Calendar.YEAR),
                11,
                31,
                0,
                0,
                0
            )
            return c.timeInMillis
        }
    }
}
