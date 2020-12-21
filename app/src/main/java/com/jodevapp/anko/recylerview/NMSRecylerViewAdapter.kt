package com.jodevapp.anko.recylerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.checkBox
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.imageButton
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.toggle
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoContext


class NMSRecyclerViewAdapter<T>(private val context: Context, private val tree: VTree<T>, private val listener: (TreeNode<T>) -> Unit)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodes: List<TreeNode<T>> = tree.toList()
    private val cachedViews: MutableMap<String, View> = mutableMapOf()

    init {
        nodes.forEach { it.visible = it.d == 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        return NodeViewHolder(NMSItemUI().createView(AnkoContext.create(context, parent)), this)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val visibleNodes = nodes.filter { it.visible }
        if (position >= visibleNodes.size) return
        holder.bindItem(visibleNodes[position], listener)
    }

    override fun getItemCount(): Int = nodes.size

    class NodeViewHolder(override val containerView: View,
                         private val nmsAdapter: NMSRecyclerViewAdapter<*>) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        private var checkBox: CheckBoxTriStates = itemView.checkBox
        private var button: ImageView = itemView.imageButton
        private lateinit var tNode: TreeNode<*>

        private fun propagateParent(treeNode: TreeNode<*>) {
            treeNode.parent?.let { parent ->
                // are all my siblings same state ?
                val found = parent.children.find { sibling ->
                    sibling.selectionState != treeNode.selectionState
                }
                parent.selectionState = if (found != null) {
                    CheckBoxTriStates.Companion.SelectionState.Indeterminate
                } else {
                    treeNode.selectionState
                }
                nmsAdapter.cachedViews[parent.id]?.let { it.checkBox.selectionState = parent.selectionState }
                propagateParent(parent)
            }
        }

        private fun propagateChild(treeNode: TreeNode<*>) {
            treeNode.children.forEach { child ->
                child.selectionState = treeNode.selectionState
                nmsAdapter.cachedViews[child.id]?.let { it.checkBox.selectionState = checkBox.selectionState }
                propagateChild(child)
            }
        }

        private val checkBoxOnClick = View.OnClickListener {
            tNode.selectionState = checkBox.selectionState
            if (checkBox.selectionState !=
                    CheckBoxTriStates.Companion.SelectionState.Indeterminate) {
                propagateChild(tNode)
            }
            propagateParent(tNode)
        }

        private val buttonOnClick = View.OnClickListener {
            tNode.expanded = !tNode.expanded
            itemView.toggle( tNode.expanded )
            tNode.children.forEach { immediateChild ->
                immediateChild.visible = tNode.expanded
            }
            nmsAdapter.notifyDataSetChanged()
        }

        fun <T> bindItem(item: TreeNode<T>, listener: (TreeNode<T>) -> Unit) {
            tNode = item
            if (item.children.isNotEmpty()) {
                checkBox.auto3State = true
                button.visibility = View.VISIBLE
                button.setOnClickListener(buttonOnClick)
            } else {
                checkBox.auto3State = false
                button.visibility = View.GONE
                button.setOnClickListener(null)
            }
            nmsAdapter.cachedViews[item.id] = itemView
            checkBox.text = item.displayName
            checkBox.layoutParams.leftMargin = item.d * 80
            checkBox.selectionState = item.selectionState
            // make only directs visible
            //itemView.visibleRecycler = ( item.d == 0 )
            checkBox.setOnClickListener(checkBoxOnClick)
        }
    }
}