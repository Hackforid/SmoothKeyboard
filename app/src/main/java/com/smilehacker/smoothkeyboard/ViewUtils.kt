package com.smilehacker.smoothkeyboard

import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by quan.zhou on 2018-11-02.
 */

/**
 * remove when view detached from window
 */
fun View.addAutoRemovableOnPreDrawListener(listener: ViewTreeObserver.OnPreDrawListener) {
    viewTreeObserver.addOnPreDrawListener(listener)
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
        }

        override fun onViewDetachedFromWindow(v: View?) {
            viewTreeObserver.removeOnPreDrawListener(listener)
        }
    })
}

fun View.addAutoRemovableOnGlobalLayoutListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
    viewTreeObserver.addOnGlobalLayoutListener(listener)
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {
        }

        override fun onViewDetachedFromWindow(v: View?) {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    })
}

fun View.setHeight(height: Int) {
    val lp = layoutParams
    lp.height = height
    layoutParams = lp
}