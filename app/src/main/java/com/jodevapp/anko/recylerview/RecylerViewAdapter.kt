package com.jodevapp.anko.recylerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import it.sephiroth.android.library.checkbox3state.CheckBox3
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoContext

/**
 * Created by jodevapp on 8/29/18.
 */
class RecyclerViewAdapter(private val context: Context, private val clubs: List<Club>, private val listener: (Club) -> Unit)
    : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TreeUi().createView(AnkoContext.create(context, parent)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(clubs[position], listener)
    }

    override fun getItemCount(): Int = clubs.size

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        var ivImage: ImageView = itemView.findViewById(TreeUi.ivImage)
        var checkBox: CheckBox = itemView.findViewById(TreeUi.tvName)

        fun bindItem(items: Club, listener: (Club) -> Unit) {
            checkBox.text = items.name
            Glide.with(containerView).load(items.image).into(ivImage)
            ivImage.setOnClickListener { listener(items) }
        }
    }
}