package com.paulklauser.formattededittext

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

class FormattedEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    init {
        setRawInputType(EditorInfo.TYPE_CLASS_NUMBER)
        addTextChangedListener(FormattedTextWatcher())
    }
}