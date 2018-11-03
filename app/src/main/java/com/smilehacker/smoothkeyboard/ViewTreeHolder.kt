package com.smilehacker.smoothkeyboard

import android.view.ViewGroup
import android.view.Window

/**
 * Created by quan.zhou on 2018-11-02.
 */
class ViewTreeHolder {

    val decorView: ViewGroup

    /**
     * When Window is FULLSCREEN:
     * View tree looks like:
     *
     * DecorView <- does not get resized, contains space for system Ui bars.
     * - LinearLayout
     * -- FrameLayout <- gets resized
     * --- Custom Views
     */
    constructor(window: Window) {
        decorView = window.decorView as ViewGroup

        val contentView = decorView.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
    }
}