package com.jodevapp.anko.recylerview

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by jodevapp on 8/29/18.
 */
@Parcelize
data class Club(val id: String, val name: String?, val image: Int?, val desc: String?) : Parcelable

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





