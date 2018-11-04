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
    private lateinit var mKeyboardContainer: FrameLayout



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
        mDecorView.addAutoRemovableOnPreDrawListener(detector)
    }

    private fun getKeyboardHeight(): Int {
        val r = Rect()
        mUnResizableContentView.getWindowVisibleDisplayFrame(r)
        return mUnResizableContentView.bottom - r.bottom
    }


    private inner class KeyBoardDetector: ViewTreeObserver.OnPreDrawListener {
        private var mPreContentHeight = -1

        override fun onPreDraw(): Boolean {
            Log.i("xx", "onPreDraw keyboardHeight = ${getKeyboardHeight()} contentHeight = ${mResizableContentView.height} preContentHeight = ${mPreContentHeight}")
            val contentHeight = mResizableContentView.height
            val keyboardHeight = getKeyboardHeight()
            if (contentHeight == mPreContentHeight) {
                return true
            }

            if (mPreContentHeight != -1) {
                val isKeyShow = getKeyboardHeight() > 0
                Log.i("xx", "$isKeyShow ${mDecorView.height} $contentHeight $mPreContentHeight")
                //mResizableContentView?.layoutParams?.height = mContentHeight
                animateInputContent(isKeyShow, contentHeight, mPreContentHeight)

            }

            mPreContentHeight = contentHeight

            return false
        }

    }

    private fun animateInputContent(isKeyboardShown: Boolean, contentHeight: Int, preContentHeight: Int) {
        val keyboardHeight = getKeyboardHeight()
        mHeightAnimator.cancel()
        mHeightAnimator = ObjectAnimator.ofInt(preContentHeight, contentHeight)
        mHeightAnimator.interpolator = FastOutSlowInInterpolator()
        mHeightAnimator.duration = 300
        Log.i("xx", "isKeyShow $isKeyboardShown $preContentHeight to $contentHeight ")

        mHeightAnimator.addUpdateListener {
            val lp = mContainer?.layoutParams
            Log.i("xx", "anim height to ${it.animatedValue}")
            lp?.height = it.animatedValue as Int
            mContainer?.requestLayout()
        }

        mHeightAnimator.start()
    }
}