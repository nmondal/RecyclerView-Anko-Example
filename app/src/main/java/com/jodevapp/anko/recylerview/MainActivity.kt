package com.jodevapp.anko.recylerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jodevapp.anko.recylerview.R.array.*
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

/**
 * Created by jodevapp on 8/29/18.
 */
class MainActivity : AppCompatActivity() {

    private val tree: VTree = initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()

        verticalLayout {
            lparams(matchParent, wrapContent)

            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = NMSRecyclerViewAdapter(context, tree) {
                    //startActivity<SecondActivity>("clubBundle" to it)
                }
            }
        }
    }

    private fun initData() : VTree {
        val vTree = VTree()
        val roots = (1..1000).map {
            TreeNode( it.toString(), it.toString(), it, null, emptyList(), SelectionState.UnChecked, true )
        }
        vTree.roots = roots
        return vTree
    }
}
