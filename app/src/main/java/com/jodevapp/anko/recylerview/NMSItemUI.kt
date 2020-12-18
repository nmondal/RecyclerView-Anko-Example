package com.jodevapp.anko.recylerview

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.jodevapp.anko.recylerview.CheckBoxTriStates.Companion.checkBox3
import org.jetbrains.anko.*

/**
 * Noga
 */
class NMSItemUI : AnkoComponent<ViewGroup> {

    companion object {
        const val checkBoxId = 1
        const val buttonId = 2
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
                imageResource = R.drawable.ic_baseline_arrow_drop_up_24
            }.lparams {
                height = dip(40)
                width = dip(40)
                gravity = Gravity.END
            }
        }
    }
}