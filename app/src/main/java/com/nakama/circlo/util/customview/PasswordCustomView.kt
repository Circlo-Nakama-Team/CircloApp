package com.nakama.circlo.util.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.nakama.circlo.R

class PasswordCustomView: AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                this@PasswordCustomView.error =
                    if (s.count() < 8 ) resources.getString(R.string.enter_valid_password) else null
                if (s.count() < 8 ) {
                    this@PasswordCustomView.setError(resources.getString(R.string.enter_valid_password))
                } else this@PasswordCustomView.setError(null)

                this@PasswordCustomView.requestFocus()

            }
            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }


}