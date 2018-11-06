package com.smilehacker.smoothkeyboard

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout


/**
 * Created by quan.zhou on 2018-11-01.
 */
class InputerFragment: DialogFragment() {


    private val mBottomContent by lazy { getViews().findViewById<FrameLayout>(R.id.bottom_content) }
    private val mEtInput by lazy { getViews().findViewById<EditText>(R.id.et_input) }
    private val mDecorView by lazy { dialog.window.decorView as ViewGroup }
    private val mContainer by lazy { getViews().findViewById<ViewGroup>(R.id.container) }
    private val mEmpytView by lazy { getViews().findViewById<View>(R.id.v_empty) }

    private val mResizableContentView by lazy { mDecorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT) }
    private val mUnResizableContentView by lazy { mResizableContentView.parent as ViewGroup }
    private lateinit var mKeyboardContainer: FrameLayout

    private var mOriginContainerHeight = 0
    private var mLastKeyboardHeight = 0

    private var mShouldShowBottom = false



    private var mHeightAnimator: ValueAnimator = ObjectAnimator()

    // 正在执行finish动画
    private var mIsFinishing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Dialog_FullScreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_inputer2, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onStart() {
        super.onStart()

        // dialog全屏透明
        dialog?.window?.let {
            it.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            val windowParams = it.attributes
            windowParams.dimAmount = 0.0f
            it .attributes = windowParams
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        dialog.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (!isKeyboardShown() && isBottomShown()) {
                        close()
                        return true
                    }
                }

                return false
            }
        })
        return dialog
    }

    private fun close() {
        if (!mIsFinishing) {
            if (isKeyboardShown()) {
                hideKeyBoard()
            } else if (isBottomShown()) {
                animateBottomContent(false)
            } else {
                dismissAllowingStateLoss()
            }
        }
        mIsFinishing = true
    }


    private fun initUI() {

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_inputer, mDecorView, true)
        mContainer.post {
            mOriginContainerHeight = mContainer.height - (mDecorView.bottom - mUnResizableContentView.bottom)
            mContainer.setHeight(mOriginContainerHeight)
        }

        val detector = KeyBoardDetector()
        mDecorView.addAutoRemovableOnPreDrawListener(detector)

        view.findViewById<View>(R.id.btn_emoji).setOnClickListener {
            showBottomView(!mShouldShowBottom)
        }

        view.bringToFront()

        mEtInput.post {
            mEtInput.requestFocus()
            showKeyBoard()
        }
        mEmpytView.setOnClickListener {
            close()
        }
    }

    private fun getViews() : View {
        return mDecorView
    }

    private fun getKeyboardHeight(): Int {
        val r = Rect()
        mUnResizableContentView.getWindowVisibleDisplayFrame(r)
        val height = mUnResizableContentView.bottom - r.bottom
        if (height > 0) {
            mLastKeyboardHeight = height
        }
        return height
    }

    private fun isKeyboardShown(): Boolean {
        return getKeyboardHeight() > 0
    }

    private fun isBottomShown(): Boolean {
        return mBottomContent.height > 0
    }


    private inner class KeyBoardDetector: ViewTreeObserver.OnPreDrawListener {
        private var mPreContentHeight = -1

        override fun onPreDraw(): Boolean {
//            Log.i("xx", "onPreDraw keyboardHeight = ${getKeyboardHeight()} contentHeight = ${mResizableContentView.height} preContentHeight = ${mPreContentHeight}")
            val contentHeight = mResizableContentView.height
            if (contentHeight == mPreContentHeight) {
                return true
            }

            if (mPreContentHeight != -1) {
                val isKeyShow = isKeyboardShown()
                Log.i("xx", "$isKeyShow ${mDecorView.height} $contentHeight $mPreContentHeight ")
                handleKeyboardToggle(isKeyShow)
            }

            mPreContentHeight = contentHeight

            return false
        }

    }

    private fun handleKeyboardToggle(show: Boolean) {
        if (show) {
            mShouldShowBottom = false
            animateBottomContent(true)
        } else {
            if (mShouldShowBottom) {
                return
            } else {
                animateBottomContent(false)
            }
        }
    }

    private fun animateBottomContent(show: Boolean) {

        mHeightAnimator.cancel()

        if (show) {
            mHeightAnimator = ObjectAnimator.ofInt(mBottomContent.height, mLastKeyboardHeight)
            Log.i("xx", "from ${mBottomContent.height} to $mLastKeyboardHeight")
        } else {
            mHeightAnimator = ObjectAnimator.ofInt(mBottomContent.height, 0)
            Log.i("xx", "from ${mBottomContent.height} to 0")
        }
        mHeightAnimator.interpolator = FastOutSlowInInterpolator()
        mHeightAnimator.duration = if (show) 150 else 300

        mHeightAnimator.addUpdateListener {
            mBottomContent?.setHeight(it.animatedValue as Int)
        }
        mHeightAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!show) {
                    mEtInput.post {
                        this@InputerFragment.dismissAllowingStateLoss()
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        mHeightAnimator.start()
    }

    private fun showBottomView(show: Boolean) {
        mShouldShowBottom = show
        if (show && mLastKeyboardHeight > 0) {
            if (isKeyboardShown()) {
                hideKeyBoard()
            }
            animateBottomContent(true)
        } else {
            if (isKeyboardShown()) {
                return
            } else {
                showKeyBoard()
            }

        }
    }

    private fun hideKeyBoard() {
        if (mEtInput != null) {
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(mEtInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun showKeyBoard() {
        if (mEtInput != null) {
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(mEtInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}