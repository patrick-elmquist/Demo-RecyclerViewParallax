package com.patrickiv.demo.parallax

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Show(@StringRes val title: Int, @StringRes val description: Int, @DrawableRes val image: Int)

object ShowRepository {
    fun getAll(): List<Show> {
        return listOf(
            Show(
                R.string.the_grand_tour_title,
                R.string.the_grand_tour_description,
                R.drawable.grandtour
            ),
            Show(
                R.string.daredevil_title,
                R.string.daredevil_description,
                R.drawable.daredevil
            ),
            Show(
                R.string.john_oliver_title,
                R.string.john_oliver_description,
                R.drawable.johnoliver
            )
        )
    }
}
