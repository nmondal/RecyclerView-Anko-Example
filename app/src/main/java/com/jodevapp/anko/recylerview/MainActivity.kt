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

    private var clubs: MutableList<Club> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()

        verticalLayout {
            lparams(matchParent, wrapContent)

            recyclerView {
                layoutManager = LinearLayoutManager(context)
                adapter = RecyclerViewAdapter(context, clubs) {
                    startActivity<SecondActivity>("clubBundle" to it)
                } as RecyclerView.Adapter<*>
            }
        }
    }

    private fun initData() {
        val clubName = resources.getStringArray(club_name)
        val clubImage = resources.obtainTypedArray(club_image)
        val clubDesc = resources.getStringArray(club_desc)
        clubs.clear()
        for ( inx in 0..1000) {
            for (i in clubName.indices) {
                val id = "${inx}_${i}"
                val name = "${id}_${clubName[i]}"
                clubs.add(Club(id, name, clubImage.getResourceId(i, 0), clubDesc[i]))
            }
        }
        //Recycle the typed array
        clubImage.recycle()
    }
}
