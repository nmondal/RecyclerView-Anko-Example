package com.jodevapp.anko.recylerview

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import it.sephiroth.android.library.checkbox3state.CheckBox3
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

/**
 * Noga
 */
class TreeUi : AnkoComponent<ViewGroup> {

    companion object {
        const val checkBoxId = 1
        const val buttonId = 2

        inline fun ViewManager.checkBox3(init: (@AnkoViewDslMarker CheckBox3).() -> Unit): CheckBox3 {
            return ankoView({ CheckBox3(it) }, 0, init)
        }
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        verticalLayout {
            this.orientation = LinearLayout.HORIZONTAL
            lparams(matchParent, wrapContent)
            padding = dip(16)

            checkBox {
                id = checkBoxId
                textSize = 16f
                //setChecked(false,true)
                //setCycle(intArrayOf(1, 0, 1, 0))
            }.lparams {
                height = wrapContent
                width = dip(200)
                gravity = Gravity.CENTER
            }

            imageView {
                id = buttonId
            }.lparams {
                height = dip(40)
                width = dip(40)
                gravity = Gravity.CENTER
            }
        }
    }
}