package com.patrickiv.demo.parallax.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.patrickiv.demo.parallax.R
import com.patrickiv.demo.parallax.Show
import com.patrickiv.demo.parallax.extensions.inflate

class ShowAdapter(private val items: List<Show>) : RecyclerView.Adapter<ShowViewHolder>() {
    init {
        setHasStableIds(true)
    }
    override fun getItemCount() = items.size
    override fun getItemId(position: Int) = items[position].hashCode().toLong()
    override fun onCreateViewHolder(parent: ViewGroup, vt: Int) = ShowViewHolder(parent.inflate(R.layout.item_show))
    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) = holder.bind(items[position])
}