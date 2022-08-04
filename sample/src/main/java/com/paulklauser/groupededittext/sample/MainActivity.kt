package com.paulklauser.groupededittext.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paulklauser.groupededittext.GroupedEditText
import com.paulklauser.groupededittext.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editText = findViewById<GroupedEditText>(R.id.editText)
        editText.setGrouping(arrayOf(2, 2, 4), '/')
    }
}