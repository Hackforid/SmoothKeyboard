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

/**
 * Created by quan.zhou on 2018-11-01.
 */
class InputerFragment: DialogFragment() {


    private val mBottomContent by lazy { view?.findViewById<FrameLayout>(R.id.bottom_content) }
    private val mEtInput by lazy { view?.findViewById<EditText>(R.id.et_input) }
    private val mDecorView by lazy { dialog.window.decorView as ViewGroup }
    private val mContainer by lazy { view?.findViewById<ViewGroup>(R.id.container) }
    private val mResizableContentView by lazy { mDecorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT) }
    private val mUnResizableContentView by lazy { mResizableContentView.parent as ViewGroup }
    private var mContentHeight : Int = 0


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
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        return dialog
    }

    private fun initUI() {
        val detector = KeyBoardDetector()
        mResizableContentView?.post {  mContentHeight = mResizableContentView.height }
        mDecorView.addAutoRemovableOnPreDrawListener(detector)
    }

    private fun getKeyboardHeight(): Int {
        val r = Rect()
        mUnResizableContentView.getWindowVisibleDisplayFrame(r)
        return mUnResizableContentView.bottom - r.bottom
    }


    private inner class KeyBoardDetector: ViewTreeObserver.OnPreDrawListener {

        private var mLastKeyboardHeight = -1

        override fun onPreDraw(): Boolean {
            Log.i("xx", "onPreDraw keyboardHeight = ${getKeyboardHeight()}; lastKeyboard=$mLastKeyboardHeight; contentHeight = ${mResizableContentView.height} unResizeHeight=${mContentHeight}")
            val contentHeight = mResizableContentView.height
            val keyboardHeight = getKeyboardHeight()

            if (keyboardHeight == mLastKeyboardHeight || mLastKeyboardHeight == -1) {
                mLastKeyboardHeight = keyboardHeight
                return true
            }

            if (keyboardHeight > 0) {
                mResizableContentView.setHeight(mContentHeight)
                Log.i("xx", "show keyboard")
                animateBottomView(true, keyboardHeight)
                // animate bottom to show
            } else {
                mResizableContentView.setHeight(mContentHeight)
                // animate bottom to hide
                Log.i("xx", "hide keyboard")
                animateBottomView(false, mLastKeyboardHeight)
            }
            mLastKeyboardHeight = keyboardHeight

            return false
        }

    }

    private fun animateBottomView(show: Boolean, height: Int) {
        mHeightAnimator.cancel()
        if (show) {
            mBottomContent?.setHeight(0)
            mHeightAnimator = ObjectAnimator.ofInt(0, height)
            mHeightAnimator.duration = 150
        } else {
            mBottomContent?.setHeight(height)
            mHeightAnimator = ObjectAnimator.ofInt(height, 0)
            mHeightAnimator.duration = 300
        }

        mHeightAnimator.interpolator = FastOutSlowInInterpolator()
        mHeightAnimator.addUpdateListener {
            mBottomContent?.setHeight(it.animatedValue as Int)
        }

        mHeightAnimator.start()
    }

    private fun animateInputContent(isKeyboardShown: Boolean, contentHeight: Int, preContentHeight: Int) {
        mContainer?.setHeight(mContentHeight)
        mHeightAnimator.cancel()
        val keyboardHeight = getKeyboardHeight()

        if (isKeyboardShown) {
            mBottomContent?.setHeight(0)
            mHeightAnimator = ObjectAnimator.ofInt(0, keyboardHeight)
        } else {
            mBottomContent?.setHeight(0)
            mHeightAnimator = ObjectAnimator.ofInt(0, keyboardHeight)
        }

        mHeightAnimator.interpolator = FastOutSlowInInterpolator()
        mHeightAnimator.duration = 300
        Log.i("xx", "isKeyShow $isKeyboardShown $preContentHeight to $contentHeight ")

        mHeightAnimator.addUpdateListener {
            Log.i("xx", "anim height to ${it.animatedValue}")
            mBottomContent?.setHeight(it.animatedValue as Int)
//            mContainer?.requestLayout()
        }

        mHeightAnimator.start()
    }
}