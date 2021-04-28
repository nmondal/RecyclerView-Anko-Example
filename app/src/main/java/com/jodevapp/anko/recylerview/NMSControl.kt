package com.jodevapp.anko.recylerview

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jodevapp.anko.recylerview.CheckBoxTriStates.Companion.checkBox3
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.checkBox
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.imageButton
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
import org.jetbrains.anko.leftPadding
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.singleLine
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent


data class TreeNode<T>(
        val id: String,
        val displayName: String,
        val value: T?,
        var parent: TreeNode<T>?,
        var children: List<TreeNode<T>>,
        var selectionState: CheckBoxTriStates.Companion.SelectionState
        = CheckBoxTriStates.Companion.SelectionState.UnChecked,
        var visible: Boolean = false,
        var expanded: Boolean = false,
        var d: Int = 0
)

data class VTree<T>(
        var roots: List<TreeNode<T>> = emptyList()
)

internal fun <T> VTree<T>.toList( nodeIdMap : MutableMap<String,TreeNode<T>>): List<TreeNode<T>> {
    fun list(node: TreeNode<T>, depth: Int, accList: MutableList<TreeNode<T>>) {
        node.d = depth
        accList.add(node)
        nodeIdMap[node.id] = node
        node.children.forEach { child -> list(child, depth + 1, accList) }
    }

    val l = mutableListOf<TreeNode<T>>()
    roots.forEach { child -> list(child, 0, l) }
    return l
}

internal class NMSRecyclerViewAdapter<T>(private val context: Context, private val searchText: EditText, tree: VTree<T>)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodeIds: MutableMap<String,TreeNode<T>> = mutableMapOf()
    private val nodes: List<TreeNode<T>> = tree.toList(nodeIds)

    private fun expandSelection( node : TreeNode<T> ){
        // set me up
        node.selectionState =  CheckBoxTriStates.Companion.SelectionState.Checked
        // set all children up
        node.children.filter {
            it.selectionState != CheckBoxTriStates.Companion.SelectionState.Checked
        }.forEach {
            expandSelection(it)
        }
        val parent = node.parent
        if ( parent == null || parent.selectionState
                == CheckBoxTriStates.Companion.SelectionState.Checked){
            return
        }
        // now, here ...
        // are all my siblings same state ?
        val found = parent.children.find { sibling ->
            sibling.selectionState != CheckBoxTriStates.Companion.SelectionState.Checked
        }
        if ( found != null ){
            parent.selectionState = CheckBoxTriStates.Companion.SelectionState.Indeterminate
        } else {
            parent.selectionState = CheckBoxTriStates.Companion.SelectionState.Checked
            expandSelection(parent)
        }
    }

    var selectionIds: List<String>
        get() {
            return nodes.filter {
                it.children.isEmpty() &&
                        it.selectionState == CheckBoxTriStates.Companion.SelectionState.Checked
            }.map { it.id }
        }
        set(value) {
            value.forEach { expandSelection( nodeIds[it]!!) }
        }

    private fun resetNodeVisibility() {
        nodes.forEach {
            it.visible = it.d == 0
            it.expanded = false
        }
    }

    private fun updateExpansion(node: TreeNode<*>, position: Int) {
        val text = searchText.text.toString()
        val childrenToUpdate = when {
            !node.expanded && text.isEmpty() -> node.children
            else -> node.children.filter { it.displayName.contains(text, true) }
        }
        var count = 0
        for (child in childrenToUpdate) {
            if (!node.expanded) {
                count += getVisibleCountAndHide(child)
            }
            child.visible = node.expanded
        }
        if (node.expanded) {
            this.notifyItemChanged(position)
            this.notifyItemRangeInserted(position + 1, childrenToUpdate.size)
        } else {
            this.notifyItemChanged(position)
            this.notifyItemRangeRemoved(position + 1, count)
        }
    }

    private fun getVisibleCountAndHide(node: TreeNode<*>): Int = if (node.visible) {
        node.visible = false
        node.expanded = false
        1 + node.children.sumBy { getVisibleCountAndHide(it) }
    } else 0

    private fun updateExpansion(text: String) {
        if (text.isEmpty()) {
            resetNodeVisibility()
        } else {
            // First set visibility
            for (node in nodes) {
                node.visible = node.displayName.contains(text, true)
            }
            nodes.filter { it.visible }.forEach { propagateVisible(it) }
            // Set expansion state based on visibility of children
            nodes.forEach { node -> node.expanded = node.children.find { it.visible } != null }
        }
        this.notifyDataSetChanged()
    }

    private fun propagateVisible(treeNode: TreeNode<*>) {
        treeNode.visible = true
        treeNode.parent?.let { propagateVisible(it) }
    }

    inner class NMSTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) = updateExpansion(s.toString())
    }

    init {
        resetNodeVisibility()
        if (nodes.size < 10) {
            searchText.visibility = View.GONE
        } else {
            searchText.addTextChangedListener(NMSTextWatcher())
            searchText.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        return NodeViewHolder(NMSItemUI().createView(AnkoContext.create(context, parent)), this)
    }

    private val visibleNodes : List<TreeNode<*>>
        get() {
            return nodes.filter { it.visible }
        }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.bindItem(visibleNodes[position])
    }

    override fun getItemCount(): Int {
        return visibleNodes.size
    }

    class NodeViewHolder(override val containerView: View,
                         private val nmsAdapter: NMSRecyclerViewAdapter<*>) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        private var checkBox: CheckBoxTriStates = itemView.checkBox
        private var button: ImageView = itemView.imageButton
        private lateinit var tNode: TreeNode<*>

        private fun propagateParent(treeNode: TreeNode<*>, position: Int) {
            treeNode.parent?.let { parent ->
                val parentPosition = position - (parent.children.filter { it.visible }.indexOf(treeNode) + 1)
                // are all my siblings same state ?
                val found = parent.children.find { sibling ->
                    sibling.selectionState != treeNode.selectionState
                }
                parent.selectionState = if (found != null) {
                    CheckBoxTriStates.Companion.SelectionState.Indeterminate
                } else {
                    treeNode.selectionState
                }
                propagateParent(parent, parentPosition)
                nmsAdapter.notifyItemChanged(parentPosition)
            }
        }

        private fun propagateChild(treeNode: TreeNode<*>): Int {
            var updatedCount = 0
            treeNode.children.forEach { child ->
                child.selectionState = treeNode.selectionState
                if (child.visible) updatedCount++
                updatedCount += propagateChild(child)
            }
            return updatedCount
        }

        private val checkBoxOnClick = View.OnClickListener {
            tNode.selectionState = checkBox.selectionState
            if (checkBox.selectionState != CheckBoxTriStates.Companion.SelectionState.Indeterminate) {
                val updatedCount = propagateChild(tNode)
                nmsAdapter.notifyItemRangeChanged(adapterPosition, 1 + updatedCount)
            }
            propagateParent(tNode, adapterPosition)
        }

        private val buttonOnClick = View.OnClickListener {
            tNode.expanded = !tNode.expanded
            nmsAdapter.updateExpansion(tNode, adapterPosition)
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
            checkBox.text = item.displayName
            if (item.expanded) {
                button.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
            } else {
                button.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
            }
            button.tag = item.expanded
            checkBox.layoutParams.leftMargin = item.d * 80
            checkBox.selectionState = item.selectionState
            checkBox.setOnClickListener(checkBoxOnClick)
        }
    }
}

internal class NMSItemUI : AnkoComponent<ViewGroup> {
    companion object {
        private const val checkBoxId = 1
        private const val buttonId = 2

        val View.imageButton : ImageView
            get() = findViewById(buttonId)

        val View.checkBox : CheckBoxTriStates
            get() = findViewById(checkBoxId)
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        linearLayout {
            this.orientation = LinearLayout.HORIZONTAL
            lparams(matchParent, wrapContent)
            padding = dip(16)

            checkBox3 {
                id = checkBoxId
                textSize = 16f
                leftPadding = dip(32)
            }.lparams {
                height = wrapContent
                width = dip(0)
                weight = 9.0f
                gravity = Gravity.START
            }
            imageView {
                id = buttonId
                imageResource = R.drawable.ic_baseline_arrow_drop_down_24
                tag = false
            }.lparams {
                height = dip(24)
                width = dip(0)
                weight = 1.0f
                gravity = Gravity.CENTER
            }
        }
    }
}

class NMSControl( private val tree : VTree<*>) : AnkoComponent<Context> {

    private lateinit var recycleViewAdapter:  NMSRecyclerViewAdapter<*>

    var selectionIds : List<String>
        get() {
            return recycleViewAdapter.selectionIds
        } set(value) {
            recycleViewAdapter.selectionIds = value
        }


    override fun createView(ui: AnkoContext<Context>): View {
        return with(ui){
            verticalLayout {
                lparams(matchParent, wrapContent)
                val searchText = editText {
                    padding = dip(16)
                    maxLines = 1
                    minLines = 1
                    singleLine = true
                }.lparams(matchParent, wrapContent)
                recyclerView {
                    layoutManager = LinearLayoutManager(context)
                    recycleViewAdapter = NMSRecyclerViewAdapter(context, searchText, tree)
                    adapter = recycleViewAdapter
                }
            }
        }
    }
}

fun <T> Activity.nmsControl(tree: VTree<T>) : NMSControl {
    val ctrl = NMSControl(tree)
    val view =  ctrl.createView(AnkoContext.create(this))
    this.setContentView(view)
    return ctrl
}
