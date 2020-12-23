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
import com.jodevapp.anko.recylerview.NMSItemUI.Companion.toggle
import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.imageView
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
        var visible: Boolean = children.isNotEmpty(),
        var expanded: Boolean = false, //TODO is there a better way ?
        var d: Int = 0 // TODO is there a better way ?
)

data class VTree<T>(
        var roots: List<TreeNode<T>> = emptyList()
)

fun <T> VTree<T>.toList(): List<TreeNode<T>> {
    fun list(node: TreeNode<T>, depth: Int, accList: MutableList<TreeNode<T>>) {
        node.d = depth
        accList.add(node)
        node.children.forEach { child -> list(child, depth + 1, accList) }
    }

    val l = mutableListOf<TreeNode<T>>()
    roots.forEach { child -> list(child, 0, l) }
    return l
}

internal class NMSRecyclerViewAdapter<T>(private val context: Context, searchText: EditText, tree: VTree<T>)
    : RecyclerView.Adapter<NMSRecyclerViewAdapter.NodeViewHolder>() {

    private val nodes: List<TreeNode<T>> = tree.toList()
    private val cachedViews: MutableMap<String, View> = mutableMapOf()

    internal fun resetView(){
        cachedViews.clear()
        notifyDataSetChanged()
    }

    var selectionIds: List<String>
        get() {
            return nodes.filter {
                it.children.isEmpty() &&
                        it.selectionState == CheckBoxTriStates.Companion.SelectionState.Checked
            }.map { it.id }
        }
        set(value) {
            value.forEach {  id ->

            }
        }

    private fun resetNodeVisibility() {
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
            if (text.isEmpty()) {
                resetNodeVisibility()
            } else {
                nodes.forEach {
                    it.visible = it.displayName.contains(text, true)
                }
                nodes.filter { it.visible }.forEach { it.parent?.let { p -> p.visible = true } }
            }
            // TODO
            this@NMSRecyclerViewAdapter.resetView()
        }
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
            itemView.toggle(tNode.expanded)
            tNode.expanded = !tNode.expanded
            tNode.children.forEach { immediateChild ->
                immediateChild.visible = tNode.expanded
            }
            nmsAdapter.resetView()
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

internal class NMSItemUI : AnkoComponent<ViewGroup> {

    companion object {
        private const val checkBoxId = 1
        private const val buttonId = 2

        val View.imageButton : ImageView
            get() = findViewById(buttonId)

        val View.checkBox : CheckBoxTriStates
            get() = findViewById(checkBoxId)

        fun View.toggle(currentStateIsExpanded: Boolean) {
            imageButton.imageResource = if ( currentStateIsExpanded ) {
                imageButton.tag = false
                R.drawable.ic_baseline_arrow_drop_down_24
            }else {
                imageButton.tag = true
                R.drawable.ic_baseline_arrow_drop_up_24
            }
        }

    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        verticalLayout {
            this.orientation = LinearLayout.HORIZONTAL
            lparams(matchParent, wrapContent)
            padding = dip(16)

            checkBox3 {
                id = checkBoxId
                textSize = 16f
            }.lparams {
                height = wrapContent
                width = dip(200)
                gravity = Gravity.CENTER
            }
            imageView {
                id = buttonId
                imageResource = R.drawable.ic_baseline_arrow_drop_down_24
                tag = false

            }.lparams {
                height = dip(40)
                width = dip(40)
                gravity = Gravity.END
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
