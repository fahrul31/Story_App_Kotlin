package com.example.storyapp.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.storyapp.ui.isValidPassword
import com.google.android.material.textfield.TextInputLayout

class MyEditTextPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputLayout(context, attrs) {


    init {
        post{
            val editText = editText

            editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //"Not yet implemented"
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //"Not yet implemented"
                }

                override fun afterTextChanged(s: Editable?) {
                    val password = s.toString()

                    if (TextUtils.isEmpty(password) || !isValidPassword(password)) {
                        setError("Password Minimal 8 Karakter")
                    } else {
                        setError(null)
                    }
                }
            })
        }
    }
}

