package com.shizuku.tools.notes.utils

import android.content.Context
import android.content.res.Configuration

class DayNightUtils {
    companion object {
        @JvmStatic
        fun getDark(context: Context): Boolean {
            val nightModeFlags: Int = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        }
    }
}
