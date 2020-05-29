package com.patrickiv.demo.parallax

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.State
import com.patrickiv.demo.parallax.Direction.LEFT
import com.patrickiv.demo.parallax.Direction.RIGHT
import com.patrickiv.demo.parallax.recyclerview.ShowAdapter
import com.patrickiv.demo.parallax.recyclerview.ShowViewHolder
import com.patrickiv.demo.parallax.util.SlowLinearSmoothScroller
import kotlinx.android.synthetic.main.activity_main.*

private const val START_DELAY = 750L
private const val SCROLL_DELAY = 1250L
private const val ACTIVE_ALPHA = 1f
private const val DISABLED_ALPHA = 0.28f

private enum class Direction { LEFT, RIGHT }

class MainActivity : AppCompatActivity() {

    private val tvShows = ShowRepository.getAll()
    private val handler = Handler()
    private val scrollTask = { handleScroll() }

    private lateinit var activeView: View

    private var switchListAnimator: Animator? = null
    private var index = 0
    private var direction = RIGHT
    private var hasTouch = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupList(topList, parallax = true)
        setupList(bottomList, parallax = false)

        activeView = topList
        topLabel.alpha = ACTIVE_ALPHA
        topList.alpha = ACTIVE_ALPHA
        bottomLabel.alpha = DISABLED_ALPHA
        bottomList.alpha = DISABLED_ALPHA
    }

    override fun onResume() = super.onResume().also { startShowcaseAnimation() }
    override fun onPause() = super.onPause().also { endShowcaseAnimation() }

    private fun setupList(recyclerView: RecyclerView, parallax: Boolean) {
        recyclerView.apply {
            adapter = ShowAdapter(tvShows)
            layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
                override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: State?, position: Int) {
                    // Override the default smoothScroll to make the scroll animation behave like a user scroll
                    startSmoothScroll(SlowLinearSmoothScroller(context, position))
                }
            }

            PagerSnapHelper().attachToRecyclerView(this)

            if (parallax) {
                setupParallaxScrollListener()
            }
            setupTouchListener()
        }
    }

    private fun RecyclerView.setupParallaxScrollListener() {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = layoutManager as? LinearLayoutManager ?: return

                val scrollOffset = recyclerView.computeHorizontalScrollOffset()
                val offsetFactor = (scrollOffset % measuredWidth) / measuredWidth.toFloat()

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                findViewHolderForAdapterPosition(firstVisibleItemPosition)?.let {
                    (it as? ShowViewHolder)?.offset = -offsetFactor // Moves from 0 to 1
                }

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (firstVisibleItemPosition != lastVisibleItemPosition) {
                    findViewHolderForAdapterPosition(lastVisibleItemPosition)?.let {
                        (it as? ShowViewHolder)?.offset = 1 - offsetFactor // Moves from -1 to 0
                    }
                }
            }
        })
    }

    private fun RecyclerView.setupTouchListener() {
        setOnTouchListener { _, event ->
            hasTouch = when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> false
                else -> hasTouch
            }
            return@setOnTouchListener false
        }
    }

    private fun startShowcaseAnimation() = handler.postDelayed(scrollTask, START_DELAY)

    private fun endShowcaseAnimation() {
        switchListAnimator?.end()
        switchListAnimator = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun scrollActiveList(index: Int) = when (activeView) {
        topList -> topList.smoothScrollToPosition(index)
        else -> bottomList.smoothScrollToPosition(index)
    }

    private fun switchActiveList(onFinishedAction: () -> Unit) {
        activeView = when (activeView) {
            topList -> bottomList
            else -> topList
        }

        switchListAnimator = AnimatorSet().apply {
            val topAlpha = if (topList == activeView) ACTIVE_ALPHA else DISABLED_ALPHA
            val bottomAlpha = if (bottomList == activeView) ACTIVE_ALPHA else DISABLED_ALPHA

            playTogether(
                    ObjectAnimator.ofFloat(topLabel, View.ALPHA, topAlpha),
                    ObjectAnimator.ofFloat(topList, View.ALPHA, topAlpha),
                    ObjectAnimator.ofFloat(bottomLabel, View.ALPHA, bottomAlpha),
                    ObjectAnimator.ofFloat(bottomList, View.ALPHA, bottomAlpha)
            )

            doOnEnd { onFinishedAction() }

            start()
        }
    }

    private fun handleScroll() {
        if (hasTouch) {
            handler.postDelayed(scrollTask, SCROLL_DELAY)
            return
        }

        var switchActiveList = false
        when (direction) {
            LEFT -> {
                if (index - 1 < 0) {
                    direction = RIGHT
                    index += 1
                    switchActiveList = true
                } else {
                    index -= 1
                }
            }
            RIGHT -> {
                if (index + 1 >= tvShows.size) {
                    direction = LEFT
                    index -= 1
                } else {
                    index += 1
                }
            }
        }

        if (switchActiveList) {
            switchActiveList {
                scrollActiveList(index)
                handler.postDelayed(scrollTask, SCROLL_DELAY)
            }
        } else {
            scrollActiveList(index)
            handler.postDelayed(scrollTask, SCROLL_DELAY)
        }
    }
}
