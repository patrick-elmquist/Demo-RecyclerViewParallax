package com.patrickiv.demo.parallax.util

import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

private const val DEFAULT_TARGET = RecyclerView.NO_POSITION
private const val SCROLL_SLOW_FACTOR = 4.0f
class SlowLinearSmoothScroller(context: Context, target: Int = DEFAULT_TARGET) : LinearSmoothScroller(context) {
    init {
        targetPosition = target
    }
    override fun calculateSpeedPerPixel(dm: DisplayMetrics?) = super.calculateSpeedPerPixel(dm) * SCROLL_SLOW_FACTOR
}