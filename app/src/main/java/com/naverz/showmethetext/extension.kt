package com.naverz.showmethetext

/**
 * ShowMeTheText
 * Copyright (c) 2022-present NAVER Z Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * */

import android.util.DisplayMetrics
import android.util.TypedValue
import kotlin.math.roundToInt

class DimensionUtils {
    companion object {

        fun dp2IntPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, App.instance.resources.displayMetrics
            ).roundToInt()
        }

        fun sp2px(sp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp,
                App.instance.resources.displayMetrics
            )

        }

        fun px2sp(pxValue: Float): Int {
            val fontScale = App.instance.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        fun px2Dp(px: Float): Int {
            val displayMetrics = App.context.resources.displayMetrics
            return (px / (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
        }
    }

}

val Int.dp2px: Int
    get() {
        return DimensionUtils.dp2IntPx(this.toFloat())
    }

val Int.sp2px: Float
    get() {
        return DimensionUtils.sp2px(this.toFloat())
    }

val Float.px2sp: Int
    get() {
        return DimensionUtils.px2sp(this)
    }

val Int.px2sp: Int
    get() {
        return DimensionUtils.px2sp(this.toFloat())
    }

val Int.px2dp: Int
    get() {
        return DimensionUtils.px2Dp(this.toFloat())
    }
