package com.smilehacker.smoothkeyboard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private val mBtnInput: Button by lazy { findViewById<Button>(R.id.btn_input) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtnInput.setOnClickListener {
            showInputDialog()
        }
    }

    private fun showInputDialog() {
        val dialog = InputerFragment()
        dialog.show(supportFragmentManager, "input")
    }
}
