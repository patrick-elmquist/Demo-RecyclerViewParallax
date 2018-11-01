package com.patrickiv.demo.parallax.recyclerview

import android.graphics.Matrix
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.patrickiv.demo.parallax.Show
import kotlinx.android.synthetic.main.item_show.view.*
import kotlin.math.abs

class ShowViewHolder(view: View) : ViewHolder(view) {
    private val title = itemView.title
    private val description = itemView.description
    private val thumbnail = itemView.thumbnail
    private val poster = itemView.poster

    private val interpolator = FastOutLinearInInterpolator()

    /**
     * Offset the thumbnail and text with a factor [-1.0..1.0] of the total width
     */
    var offset: Float = 0f
        set(v) {
            // Ensure that the value is in the range [-1.0..1.0]
            field = v.coerceIn(-1f, 1f)

            // The interpolator wants an input value in the [0.0..1.0] range so
            // let's preserve the direction (field < 0 = left, field > 0 = right)...
            val direction = if (field < 0) -1f else 1f

            // ...and provide the absolute field value to the interpolator.
            val interpolatedValue = interpolator.getInterpolation(abs(field))

            // Calculate the translation...
            val translationX = direction * itemView.measuredWidth * interpolatedValue

            // ...and apply it to the thumbnail, title and description
            title.translationX = translationX
            description.translationX = translationX
            thumbnail.translationX = translationX
        }

    fun bind(viewModel: Show) {
        title.setText(viewModel.title)
        description.setText(viewModel.description)
        thumbnail.setImageResource(viewModel.image)
        poster.setImageResource(viewModel.image)
        poster.doOnPreDraw {
            val m = Matrix(poster.imageMatrix)
            val scaleX = poster.measuredWidth / poster.drawable.intrinsicWidth.toFloat()
            m.preScale(scaleX, scaleX)
            m.postTranslate(0f, -poster.drawable.intrinsicHeight * scaleX / 2.5f)
            poster.imageMatrix = m
        }
    }
}