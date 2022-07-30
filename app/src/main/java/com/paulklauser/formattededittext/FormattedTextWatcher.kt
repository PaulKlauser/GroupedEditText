package com.paulklauser.formattededittext

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import java.lang.Integer.max
import kotlin.math.min

class FormattedTextWatcher : TextWatcher {

    private var forward = false
    private var inTextChange = false
    private var start = 0
    private var count = 0

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (inTextChange) {
            return
        }
        this.start = start
        this.count = count
        forward = count > 0
    }

    override fun afterTextChanged(s: Editable) {
        if (inTextChange) {
            return
        }
        inTextChange = true
        var selection = Selection.getSelectionEnd(s)
        val strippedBuilder = StringBuilder(s.toString().replace(" ", "").stripNonDigits())
        if (count <= 1) { // Typed
            val charactersStrippedBeforeStart = s.toString()
                .substring(0..min(start, s.length - 1))
                .count { it == ' ' || !it.isDigit() }
            val adjustedStart = start - charactersStrippedBeforeStart
            if (strippedBuilder.length > 16) { // Max length, ignore input
                strippedBuilder.delete(adjustedStart, adjustedStart + 1)
                selection = max(selection - 1, 0)
            } else {
                when (start) { // Use ol' start so that we know if we deleted a space
                    4, 9, 14 -> {
                        if (forward) {
                            selection++
                        } else {
                            selection--
                            strippedBuilder.delete(adjustedStart - 1, adjustedStart)
                        }
                    }
                }
            }
        } else { // Pasted
            if (strippedBuilder.length > 16) {
                strippedBuilder.delete(16, s.length)
            }
        }

        val reformatted = format(strippedBuilder.toString())

        s.clear()
        s.append(reformatted)
        val newSelection = min(s.length, selection)
        Selection.setSelection(s, newSelection)
        inTextChange = false
    }

    private fun format(text: String): String {
        return StringBuilder(text).run {
            if (text.length > 4) {
                insert(4, " ")
            }
            if (text.length > 8) {
                insert(9, " ")
            }
            if (text.length > 12) {
                insert(14, " ")
            }
            toString()
        }
    }

    private fun String.stripNonDigits(): String {
        return StringBuilder().run {
            this@stripNonDigits.forEach {
                if (it.isDigit())
                    append(it)
            }
            toString()
        }
    }
}