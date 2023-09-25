package org.porcumipsum.fragments

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.porcumipsum.R
import org.porcumipsum.utils.PorkUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class CreateQrFragment : BottomSheetDialogFragment() {
    private var textSelected: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            textSelected = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sheetContainer = view.parent as? ViewGroup
        sheetContainer?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        val textDisplay = view.findViewById<TextView>(R.id.text_display)
        val qrCodeDisplay = view.findViewById<ImageView>(R.id.qrcode_display)
        val qrCodeBitmap = PorkUtils.generateQRCode("$textSelected", 200, 200)
        val saveBtn = view.findViewById<Button>(R.id.save_btn)
        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)

        textDisplay.text = textSelected

        qrCodeBitmap?.let {
            qrCodeDisplay.setImageBitmap(it)
        }

        saveBtn.setOnClickListener {
            if (qrCodeBitmap != null) {
                saveToGallery(qrCodeBitmap)
            }
        }

        dismissBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun saveToGallery(bitmap: Bitmap) {
        val contentResolver = context?.contentResolver
        val insertUri = contentResolver?.insert (
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        )

        try {
            val outputStream = insertUri?.let {
                contentResolver.openOutputStream(it, "rw")
            }

            if (bitmap.compress(Bitmap.CompressFormat.PNG,100, outputStream)) {
                Toast.makeText(context, getString(R.string.saved_gallery), Toast.LENGTH_SHORT).show()
            }
        } catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(param1: String) =
            CreateQrFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}