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
import java.security.SecureRandom

/**
 * Created by jodevapp on 8/29/18.
 */
class MainActivity : AppCompatActivity() {

    private val tree: VTree<String> = initData()

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

    private fun initData() : VTree<String> {
        val vTree = VTree<String>()
        val r = SecureRandom()
        val roots = (1..1000).map {
            val checked = when ( r.nextInt(3) ){
                0 -> CheckBoxTriStates.Companion.SelectionState.UnChecked
                1 -> CheckBoxTriStates.Companion.SelectionState.Checked
                else -> CheckBoxTriStates.Companion.SelectionState.Indeterminate
            }
            TreeNode( it.toString(), it.toString(), it, null, emptyList(), checked, r.nextBoolean() )
        }
        vTree.roots = roots as List<TreeNode<String>>
        return vTree
    }
}
