package com.jodevapp.anko.recylerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jodevapp.anko.recylerview.R.array.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.editText
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import java.security.SecureRandom

/**
 * Created by jodevapp on 8/29/18.
 */
class MainActivity : AppCompatActivity() {

    private val maxItems = 80

    private val tree: VTree<String> = initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        nmsControl(tree){ }
    }

    private fun initData() : VTree<String> {
        val vTree = VTree<String>()
        val checked = CheckBoxTriStates.Companion.SelectionState.UnChecked
        val roots = (1..maxItems).map { parent->
            val id = parent.toString()
            val pNode = TreeNode( id, id, id, null, emptyList(), checked , true )
            pNode.children = (1..maxItems).map { child ->
                val id = "$parent-$child"
                TreeNode( id, id, id, pNode , emptyList(), checked, true )
            }
            pNode
        }
        vTree.roots = roots
        return vTree
    }
}
