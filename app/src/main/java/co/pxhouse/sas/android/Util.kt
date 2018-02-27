package co.pxhouse.sas.android

import android.content.Context
import android.util.TypedValue
import android.util.TypedValue.*

object Util {
    fun dp(context: Context, dp: Float) =
        applyDimension(COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}