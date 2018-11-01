package com.patrickiv.demo.parallax.util

import android.content.Context
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView.LayoutManager

class SlowPagerSnapHelper(private val context: Context) : PagerSnapHelper() {
    override fun createScroller(lm: LayoutManager?) = SlowLinearSmoothScroller(context)
}
