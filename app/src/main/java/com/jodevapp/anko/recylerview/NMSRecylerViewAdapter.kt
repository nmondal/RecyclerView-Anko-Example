package com.jodevapp.anko.recylerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoContext


class NMSRecyclerViewAdapter<T>(private val context: Context, private val tree: VTree<T>, private val listener: (TreeNode<T>) -> Unit)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodes: List<TreeNode<T>> = tree.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        return NodeViewHolder(TreeUi().createView(AnkoContext.create(context, parent)))
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val visibleNodes = nodes.filter { it.visible }
        holder.bindItem(visibleNodes[position], listener)
    }

    override fun getItemCount(): Int = nodes.size

    class NodeViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        var checkBox: CheckBoxTriStates = itemView.findViewById(TreeUi.checkBoxId)

        fun <T> bindItem(item: TreeNode<T>, listener: (TreeNode<T>) -> Unit) {
            checkBox.text = item.displayName
            checkBox.setState(item.selectionState.value)
        }
    }

}