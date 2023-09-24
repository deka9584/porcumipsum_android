package org.porcumipsum.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object PorkUtils {
    fun copyToClipboard(context: Context, text: CharSequence?) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}