package com.paulklauser.formattededittext

import android.content.ClipData
import android.content.ClipboardManager
import android.view.KeyEvent
import androidx.core.content.ContextCompat.getSystemService
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FormattedEditTextTest {
    @Test
    fun enter_16_digits_formats_correctly() {
        launchActivity<MainActivity>()

        onView(withId(R.id.editText))
            .perform(typeText("1234567890123456"))

        onView(withId(R.id.editText))
            .check(matches(withText("1234 5678 9012 3456")))
    }

    @Test
    fun enter_19_digits_ignores_extra() {
        launchActivity<MainActivity>()

        onView(withId(R.id.editText))
            .perform(typeText("1234567890123456789"))

        onView(withId(R.id.editText))
            .check(matches(withText("1234 5678 9012 3456")))
    }

    @Test
    fun enter_16_digits_plus_letters_strips_letters() {
        launchActivity<MainActivity>()

        onView(withId(R.id.editText))
            .perform(typeText("1234a5678b9012c3456"))

        onView(withId(R.id.editText))
            .check(matches(withText("1234 5678 9012 3456")))
    }

    @Test
    fun paste_16_digits_plus_letters_strips_letters() {
        launchActivity<MainActivity>().onActivity {
            val clipboard: ClipboardManager = getSystemService(it, ClipboardManager::class.java)!!
            val clip = ClipData.newPlainText("number", "1234a5678b9012c3456")
            clipboard.setPrimaryClip(clip)
        }
        onView(withId(R.id.editText))
            .perform(click())
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressKeyCode(
            KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK
        )

        onView(withId(R.id.editText))
            .check(matches(withText("1234 5678 9012 3456")))
    }

    @Test
    fun delete_number_before_space_with_cursor_after_space() {
        launchActivity<MainActivity>()

        onView(withId(R.id.editText))
            .perform(typeText("1234567890123456"))
            .perform(setSelection(5))
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressDelete()


        onView(withId(R.id.editText))
            .check(matches(withText("1235 6789 0123 456")))
    }

    @Test
    fun delete_number_after_space() {
        launchActivity<MainActivity>()

        onView(withId(R.id.editText))
            .perform(typeText("1234567890123456"))
            .perform(setSelection(6))
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressDelete()


        onView(withId(R.id.editText))
            .check(matches(withText("1234 6789 0123 456")))
    }

    @Test
    fun paste_valid_number() {
        launchActivity<MainActivity>().onActivity {
            val clipboard: ClipboardManager = getSystemService(it, ClipboardManager::class.java)!!
            val clip = ClipData.newPlainText("number", "1234 5678 9012 3456")
            clipboard.setPrimaryClip(clip)
        }

        onView(withId(R.id.editText))
            .perform(click())
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressKeyCode(
            KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK
        )

        onView(withId(R.id.editText))
            .check(matches(withText("1234 5678 9012 3456")))
    }

    @Test
    fun paste_numbers_into_center_of_valid_number_replaces_numbers_after_paste() {
        launchActivity<MainActivity>().onActivity {
            val clipboard: ClipboardManager = getSystemService(it, ClipboardManager::class.java)!!
            val clip = ClipData.newPlainText("number", "1234 5678 9012 3456")
            clipboard.setPrimaryClip(clip)
        }

        onView(withId(R.id.editText))
            .perform(typeText("1234567890123456"))
            .perform(setSelection(7))
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressKeyCode(
            KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK
        )

        onView(withId(R.id.editText))
            .check(matches(withText("1234 5612 3456 7890")))
    }

    @Test
    fun paste_numbers_into_center_of_partial_number_adds_them() {
        launchActivity<MainActivity>().onActivity {
            val clipboard: ClipboardManager = getSystemService(it, ClipboardManager::class.java)!!
            val clip = ClipData.newPlainText("number", "4321")
            clipboard.setPrimaryClip(clip)
        }

        onView(withId(R.id.editText))
            .perform(typeText("1234567890"))
            .perform(setSelection(4))
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressKeyCode(
            KeyEvent.KEYCODE_V, KeyEvent.META_CTRL_MASK
        )

        onView(withId(R.id.editText))
            .check(matches(withText("1234 4321 5678 90")))
    }

    @Test
    fun delete_number_from_end() {
        launchActivity<MainActivity>()

        onView(withId(R.id.editText))
            .perform(typeText("1234567890123456"))
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressDelete()


        onView(withId(R.id.editText))
            .check(matches(withText("1234 5678 9012 345")))
    }

    @Test
    fun custom_grouping_and_separator_respected() {
        launchActivity<MainActivity>().onActivity {
            it.findViewById<FormattedEditText>(R.id.editText)
                .setGrouping(arrayOf(3, 2, 1, 4), '-')
        }

        onView(withId(R.id.editText))
            .perform(typeText("1231211234"))

        onView(withId(R.id.editText))
            .check(matches(withText("123-12-1-1234")))
    }

}