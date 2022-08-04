package com.paulklauser.groupededittext

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import java.lang.Integer.max
import kotlin.math.min

class GroupedTextWatcher(
    private val groupings: Array<Int>,
    private val groupingSeparator: Char,
    private val onlyDigits: Boolean = false
) : TextWatcher {
    private val maxLength = groupings.sum()
    private val separatorIndices = getSeparatorIndices(groupings)

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
        val strippedBuilder =
            StringBuilder(s.toString().replace(groupingSeparator.toString(), "").run { if (onlyDigits) stripNonDigits() else this })
        if (count <= 1) { // Typed
            val charactersStrippedBeforeStart = s.toString()
                .substring(0..min(start, s.length - 1))
                .count { it == groupingSeparator || !it.isDigit() }
            val adjustedStart = start - charactersStrippedBeforeStart
            if (strippedBuilder.length > maxLength) { // Max length, ignore input
                strippedBuilder.delete(adjustedStart, adjustedStart + 1)
                selection = max(selection - 1, 0)
            } else {
                if (separatorIndices.contains(start)) { // Use ol' start so that we know if we deleted a space
                    if (forward) {
                        selection++
                    } else {
                        selection--
                        strippedBuilder.delete(adjustedStart - 1, adjustedStart)
                    }
                }
            }
        } else { // Pasted
            if (strippedBuilder.length > maxLength) {
                strippedBuilder.delete(maxLength, s.length)
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
            var position = 0
            for (i in groupings.indices) {
                position += groupings[i]
                if (text.length > position) {
                    insert(position + i, groupingSeparator)
                }
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

    private fun getSeparatorIndices(groupings: Array<Int>): Array<Int> {
        val indices = arrayOfNulls<Int>(groupings.size - 1)
        var position = 0
        for (i in indices.indices) {
            position += groupings[i]
            indices[i] = position + i
        }
        return indices as Array<Int>
    }
}