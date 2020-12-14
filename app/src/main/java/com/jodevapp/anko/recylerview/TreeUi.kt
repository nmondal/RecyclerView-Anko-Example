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
 * Created by jodevapp on 8/29/18.
 */
class TreeUi : AnkoComponent<ViewGroup> {

    companion object {
        const val tvName = 1
        const val ivImage = 2
        const val checkId = 3

        inline fun ViewManager.checkBox3(init: (@AnkoViewDslMarker CheckBox3).() -> Unit): CheckBox3 {
            return ankoView({ CheckBox3(it) }, 0, init)
        }
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {

        verticalLayout {
            this.orientation = LinearLayout.HORIZONTAL
            lparams(matchParent, wrapContent)
            padding = dip(16)

            checkBox3 {
                id = checkId
            }

            imageView {
                id = ivImage
            }.lparams {
                height = dip(40)
                width = dip(40)
                gravity = Gravity.CENTER
            }

            textView {
                id = tvName
                textSize = 16f
            }.lparams {
                gravity = Gravity.CENTER
                margin = dip(10)
            }
        }
    }
}