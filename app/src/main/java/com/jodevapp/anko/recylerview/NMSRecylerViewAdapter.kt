package com.jodevapp.anko.recylerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.leftPadding


class NMSRecyclerViewAdapter<T>(private val context: Context, private val tree: VTree<T>, private val listener: (TreeNode<T>) -> Unit)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodes: List<TreeNode<T>> = tree.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        return NodeViewHolder(TreeUi().createView(AnkoContext.create(context, parent)))
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val visibleNodes = nodes.filter {  true /* it.visible */ }
        if ( position >= visibleNodes.size ) return
        holder.bindItem(visibleNodes[position], listener)
    }

    override fun getItemCount(): Int = nodes.size

    class NodeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        private var checkBox: CheckBoxTriStates = itemView.findViewById(TreeUi.checkBoxId)

        private val onClick = View.OnClickListener {
            checkBox.tag?.let {
                val tNode = it as TreeNode<*>
                print(checkBox.selectionState)
            }
        }

        fun <T> bindItem(item: TreeNode<T>, listener: (TreeNode<T>) -> Unit) {
            checkBox.text = item.displayName
            checkBox.layoutParams.leftMargin = item.d * 80
            checkBox.selectionState = item.selectionState
            checkBox.tag = item
            checkBox.setOnClickListener(onClick)
        }
    }
}