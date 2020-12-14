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
        val parent: TreeNode<T>?,
        val children: List<TreeNode<T>>,
        var selectionState: CheckBoxTriStates.Companion.SelectionState,
        var visible: Boolean,
        var d: Int = 0
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





