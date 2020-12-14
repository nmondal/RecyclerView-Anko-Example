package com.jodevapp.anko.recylerview

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.ViewManager
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatCheckBox
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView

// Copyright © 2020 Compass. All rights reserved.


/**
 * From here...
 * https://gist.github.com/kevin-barrientos/d75a5baa13a686367d45d17aaec7f030
 *
 * Base on https://stackoverflow.com/a/40939367/3950497 answer.
 */
class CheckBoxTriStates : AppCompatCheckBox {

    private var state = 0

    /**
     * This is the listener set to the super class which is going to be evoke each
     * time the check state has changed.
     */
    private val privateListener: OnCheckedChangeListener = OnCheckedChangeListener { buttonView, isChecked ->

        // checkbox status is changed from uncheck to checked.
        when (state) {
            UNKNOWN -> {
                setState(UNCHECKED)
            }
            UNCHECKED -> setState(CHECKED)
            CHECKED -> setState(UNKNOWN)
        }
    }

    /**
     * Holds a reference to the listener set by a client, if any.
     */
    private var clientListener: OnCheckedChangeListener? = null

    /**
     * This flag is needed to avoid accidentally changing the current [.state] when
     * [.onRestoreInstanceState] calls [.setChecked]
     * evoking our [.privateListener] and therefore changing the real state.
     */
    private var restoring = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun getState(): Int {
        return state
    }

    fun setState(state: Int) {
        if (!restoring && this.state != state) {
            this.state = state
            clientListener?.onCheckedChanged(this, this.isChecked)
            updateBtn()
        }
    }


    fun listener(@Nullable listener: OnCheckedChangeListener) {

        // we never truly set the listener to the client implementation, instead we only hold
        // a reference to it and evoke it when needed.
        if (privateListener !== listener) {
            clientListener = listener
        }

        // always use our implementation
        super.setOnCheckedChangeListener(privateListener)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState: Parcelable? = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.state = state
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        restoring = true // indicates that the ui is restoring its state
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.getSuperState())
        setState(ss.state)
        requestLayout()
        restoring = false
    }

    private fun init() {
        state = UNKNOWN
        updateBtn()
        setOnCheckedChangeListener(privateListener)
    }

    private fun updateBtn() {
        var btnDrawable: Int = R.drawable.ic_indeterminate_check_box_24px
        when (state) {
            UNKNOWN -> btnDrawable = R.drawable.ic_indeterminate_check_box_24px
            UNCHECKED -> btnDrawable = R.drawable.ic_check_box_outline_blank_24px
            CHECKED -> btnDrawable = R.drawable.ic_check_box_24px
        }
        setButtonDrawable(btnDrawable)
    }

    internal class SavedState : BaseSavedState {
        var state = 0

        constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            state = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(state)
        }

        override fun toString(): String {
            return ("CheckboxTriState.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " state=" + state + "}")
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        private const val UNKNOWN = -1
        private const val UNCHECKED = 0
        private const val CHECKED = 1

        enum class SelectionState(val value: Int) {
            UnChecked(UNCHECKED),
            Indeterminate(UNKNOWN),
            Checked(CHECKED)
        }

        inline fun ViewManager.checkBox3(init: (@AnkoViewDslMarker CheckBoxTriStates).() -> Unit): CheckBoxTriStates {
            return ankoView({ CheckBoxTriStates(it) }, 0, init)
        }
    }
}