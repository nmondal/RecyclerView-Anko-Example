package com.jodevapp.anko.recylerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoContext


class NMSRecyclerViewAdapter(private val context: Context, private val tree: VTree, private val listener: (TreeNode) -> Unit)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodes: List<TreeNode> = tree.toList()

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

        var checkBox: CheckBox = itemView.findViewById(TreeUi.checkBoxId)

        fun bindItem(item: TreeNode, listener: (TreeNode) -> Unit) {
            checkBox.text = item.displayName
            when ( item.selectionState ){
                SelectionState.Checked -> checkBox.isChecked = true
                SelectionState.UnChecked -> checkBox.isChecked = false
                else -> { }
            }
        }
    }

}