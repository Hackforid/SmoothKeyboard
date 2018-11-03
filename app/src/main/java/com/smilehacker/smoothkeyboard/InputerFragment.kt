package com.smilehacker.smoothkeyboard

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.RelativeLayout

/**
 * Created by quan.zhou on 2018-11-01.
 */
class InputerFragment: DialogFragment() {


    private val mBottomContent by lazy { view?.findViewById<FrameLayout>(R.id.bottom_content) }
    private val mEtInput by lazy { view?.findViewById<EditText>(R.id.et_input) }
    private val mDecorView by lazy { dialog.window.decorView as ViewGroup }
    private val mContentView by lazy { mDecorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT) }


    private var mHeightAnimator: ValueAnimator = ObjectAnimator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_inputer, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        initUI()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    private fun initUI() {
        val detector = KeyBoardDetector()
        mDecorView.addAutoRemovableOnPreDrawListener(detector)
    }


    private fun getKeyboardHeight(): Int {
        val r = Rect()
        dialog.window.decorView.getWindowVisibleDisplayFrame(r)
        val dm = this.resources.displayMetrics
        return dialog.window.decorView.bottom - r.bottom
    }


    private fun animateInput(isKeyboardShown: Boolean, contentHeight: Int, preContentHeight: Int) {
        val keyboardHeight = getKeyboardHeight()
        mHeightAnimator.cancel()
        mHeightAnimator = ObjectAnimator.ofInt(0, keyboardHeight)
        mHeightAnimator.interpolator = FastOutSlowInInterpolator()
        mHeightAnimator.duration = 300

        mHeightAnimator.addUpdateListener {
            val lp = mBottomContent?.layoutParams as RelativeLayout.LayoutParams
            lp.bottomMargin = it.animatedValue as Int
        }

        mHeightAnimator.start()
    }

    private inner class KeyBoardDetector: ViewTreeObserver.OnPreDrawListener {
        private var mPreContentHeight = -1

        override fun onPreDraw(): Boolean {
            val contentHeight = mContentView.height
            if (contentHeight == mPreContentHeight) {
                return true
            }

            if (mPreContentHeight != -1) {
                val isKeyShow = mDecorView.height - mContentView.top > contentHeight
                Log.i("xx", "$isKeyShow ${mDecorView.height} $contentHeight $mPreContentHeight")
            }

            mPreContentHeight = contentHeight

            return true
        }

    }
}