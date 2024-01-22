package org.porcumipsum.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import org.porcumipsum.fragments.CreateQrFragment
import org.porcumipsum.fragments.ScanQrFragment

object PorkUtils {
    private const val CREATE_QR_FRAGMENT_TAG = "CreateQrFragment"
    private const val SCAN_QR_FRAGMENT_TAG = "ScanQrFragment"

    fun copyToClipboard(context: Context, text: CharSequence?) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun getRndInt(min: Int, max: Int): Int {
        return if (min >= max) min else (min..(max)).random()
    }

    fun generateQRCode(text: String, width: Int, height: Int): Bitmap? {
        try {
            val hints = HashMap<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

            val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )

            val qrCodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    qrCodeBitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            return qrCodeBitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }
    }

    fun showCreateQrSheet(fragmentManager: FragmentManager, text: String) {
        if (fragmentManager.findFragmentByTag(CREATE_QR_FRAGMENT_TAG) == null) {
            CreateQrFragment.newInstance(text)
                .show(fragmentManager, CREATE_QR_FRAGMENT_TAG)
        }
    }

    fun showScanQrSheet(fragmentManager: FragmentManager) {
        if (fragmentManager.findFragmentByTag(SCAN_QR_FRAGMENT_TAG) == null) {
            ScanQrFragment().show(fragmentManager, SCAN_QR_FRAGMENT_TAG)
        }
    }
}