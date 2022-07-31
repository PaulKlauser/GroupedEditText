package com.paulklauser.groupededittext

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

class GroupedEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    private var groupedTextWatcher = GroupedTextWatcher(arrayOf(4, 4, 4, 4), ' ')

    init {
        setRawInputType(EditorInfo.TYPE_CLASS_NUMBER)
        addTextChangedListener(groupedTextWatcher)
    }

    /**
     * Set the grouping and separator used to display the contents of the EditText.
     *
     * Ex. [3, 2, 5, 6], ' ' would result in: "XXX XX XXXXX XXXXXX"
     * Ex. [2, 3, 4], '-' would result in: "XX-XXX-XXXX"
     */
    fun setGrouping(grouping: Array<Int>, separator: Char) {
        if (!TextUtils.isEmpty(text)) {
            throw IllegalStateException(
                "Don't change the grouping and separator unless the field is empty!"
            )
        }
        removeTextChangedListener(groupedTextWatcher)
        groupedTextWatcher = GroupedTextWatcher(grouping, separator)
        addTextChangedListener(groupedTextWatcher)
    }
}