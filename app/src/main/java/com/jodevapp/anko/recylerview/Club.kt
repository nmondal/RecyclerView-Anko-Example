package com.jodevapp.anko.recylerview

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by jodevapp on 8/29/18.
 */
@Parcelize
data class Club(val id: String, val name: String?, val image: Int?, val desc: String?) : Parcelable

data class TreeNode(
        val id: String,
        val displayName: String,
        val value: Any?,
        val parent: TreeNode?,
        val children: List<TreeNode>,
        var selectionState: CheckBoxTriStates.Companion.SelectionState,
        var visible: Boolean,
        var d: Int = 0
)

data class VTree(
        var roots: List<TreeNode> = emptyList()
)

fun VTree.toList(): List<TreeNode> {
    fun list(node: TreeNode, depth: Int, accList: MutableList<TreeNode>) {
        node.d = depth
        accList.add(node)
        node.children.forEach { child -> list(child, depth + 1, accList) }
    }
    val l = mutableListOf<TreeNode>()
    roots.forEach { child -> list(child, 0, l) }
    return l
}





