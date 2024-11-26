package com.example.storyapp.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.storyapp.ui.isValidEmail
import com.google.android.material.textfield.TextInputLayout


class MyEditTextEmail @JvmOverloads constructor(
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
                    val email = s.toString().trim()
                    if (TextUtils.isEmpty(email) || !isValidEmail(email)) {
                        setError("Email yang Anda Masukkan Tidak Valid")
                    } else {
                        setError(null)
                    }
                }
            })
        }



    }

}