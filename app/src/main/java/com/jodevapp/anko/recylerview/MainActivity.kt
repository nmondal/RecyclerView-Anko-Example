package com.jodevapp.anko.recylerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by jodevapp on 8/29/18.
 */
class MainActivity : AppCompatActivity() {

    // TOTAL items would be maxItems^3
    private val maxItems = 20 // so this is 8000 items

    private val tree: VTree<String> = initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        val ctrl = nmsControl(tree)
        ctrl.selectionIds = listOf("2-1", "3-2", "1-3", "4")
    }

    private fun initData() : VTree<String> {
        val vTree = VTree<String>()
        val checked = CheckBoxTriStates.Companion.SelectionState.UnChecked
        val roots = (1..maxItems).map { parent->
            val id = parent.toString()
            val pNode = TreeNode( id, id, id, null, emptyList(), checked , true )
            pNode.children = (1..maxItems).map { child ->
                val cid = "$parent-$child"
                val cNode = TreeNode( cid, cid, cid, pNode , emptyList(), checked, true )
                cNode.children = ( 1..maxItems).map { gchild ->
                    val gcid = "$parent-$child-$gchild"
                    TreeNode( gcid, gcid, gcid, cNode , emptyList(), checked, true )
                }

                cNode
            }
            pNode
        }
        vTree.roots = roots
        return vTree
    }
}
