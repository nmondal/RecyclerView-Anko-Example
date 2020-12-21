package com.jodevapp.anko.recylerview

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.jodevapp.anko.recylerview.CheckBoxTriStates.Companion.checkBox3
import org.jetbrains.anko.*

/**
 * Noga
 */
class NMSItemUI : AnkoComponent<ViewGroup> {

    companion object {
        private const val checkBoxId = 1
        private const val buttonId = 2

        val View.imageButton : ImageView
            get() = findViewById(buttonId)

        val View.checkBox : CheckBoxTriStates
            get() = findViewById(checkBoxId)

        val View.expanded : Boolean
            get() = imageButton.tag == true

        fun View.toggle( ) {
            imageButton.imageResource = if ( expanded ) {
                imageButton.tag = false
                R.drawable.ic_baseline_arrow_drop_down_24
            }else {
                imageButton.tag = true
                R.drawable.ic_baseline_arrow_drop_up_24
            }
        }

        var View.visibleRecycler : Boolean
            get() {
                return layoutParams.height != 0
            }
            set(value) {
                if ( value ){
                    tag?.let {
                        val old = it as Pair<*, *>
                        layoutParams.height = old.first as Int
                        layoutParams.width = old.second as Int
                        requestLayout()
                    }
                }else{
                    val old = Pair( layoutParams.height, layoutParams.width)
                    layoutParams.height = 0
                    layoutParams.width = 0
                    tag = old
                }
                //requestLayout()
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