package com.smilehacker.smoothkeyboard

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.*

/**
 * Created by quan.zhou on 2018-11-01.
 */
class InputerFragment: DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_inputer, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.window?.decorView?.viewTreeObserver?.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    private val mOnGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        Log.i("xx", "height = ${getKeyboardHeight()}")
    }

    fun getKeyboardHeight(): Int {
        val r = Rect()
        dialog.window.decorView.getWindowVisibleDisplayFrame(r)
        val dm = this.resources.displayMetrics
        return dialog.window.decorView.bottom - r.bottom
    }
}