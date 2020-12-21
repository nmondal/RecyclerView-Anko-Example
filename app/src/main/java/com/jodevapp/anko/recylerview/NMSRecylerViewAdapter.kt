package com.jodevapp.anko.recylerview

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.checkBox
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.imageButton
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.toggle
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


class NMSRecyclerViewAdapter<T>(private val context: Context, searchText : EditText, tree: VTree<T>)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodes: List<TreeNode<T>> = tree.toList()
    private val cachedViews: MutableMap<String, View> = mutableMapOf()

    private fun resetNodeVisibility(){
        nodes.forEach {
            it.visible = it.d == 0
            it.expanded = false
        }
    }

    inner class NMSTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if ( text.isEmpty() ){
                resetNodeVisibility()
            } else {
                nodes.forEach {
                    it.visible = it.displayName.contains(text, true)
                }
                nodes.filter { it.visible }.forEach { it.parent?.let { p -> p.visible = true } }
            }
            this@NMSRecyclerViewAdapter.notifyDataSetChanged()
        }
    }

    init {
        resetNodeVisibility()
        if ( nodes.size < 10 ) {
            searchText.visibility = View.GONE
        } else {
            searchText.addTextChangedListener(NMSTextWatcher())
            searchText.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        return NodeViewHolder(NMSItemUI().createView(AnkoContext.create(context, parent)), this)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val visibleNodes = nodes.filter { it.visible }
        if (position >= visibleNodes.size) return
        holder.bindItem(visibleNodes[position])
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

        fun <T> bindItem(item: TreeNode<T>) {
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

inline fun <T> Activity.nmsControl(tree: VTree<T>, init: (@AnkoViewDslMarker LinearLayout).( ) -> Unit): LinearLayout {
    return ankoView({
        verticalLayout {
            lparams(matchParent, wrapContent)
            val searchText= editText {
                padding = dip(16)
                maxLines = 1
                minLines = 1

            }.lparams(matchParent, wrapContent)
            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = NMSRecyclerViewAdapter(context, searchText, tree)
            }
        }
    }, 0, init)
}