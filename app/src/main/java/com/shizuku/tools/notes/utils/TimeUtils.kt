package com.shizuku.tools.notes.utils

import java.util.*

class TimeUtils {
    companion object {
        @JvmStatic
        fun getMills(): Long {
            return Calendar.getInstance().timeInMillis
        }
    }

}
