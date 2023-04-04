package com.dev.engineerrant.animations

import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class typeWriter {
    fun typeWrite(textView: TextView,lifecycleOwner: LifecycleOwner, text: String, intervalMs: Long) {
        textView.typeWrite(lifecycleOwner,text,intervalMs);
    }

    companion object {
        fun TextView.typeWrite(lifecycleOwner: LifecycleOwner, text: String, intervalMs: Long) {
            this@typeWrite.text = ""
            lifecycleOwner.lifecycleScope.launch {
                repeat(text.length) {
                    delay(intervalMs)
                    this@typeWrite.text = text.take(it + 1)
                }
            }
        }
    }
}