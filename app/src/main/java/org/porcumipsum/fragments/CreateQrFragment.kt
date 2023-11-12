package org.porcumipsum.fragments

import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import org.porcumipsum.utils.FavouritesUtils
import org.porcumipsum.utils.PorkUtils
import java.io.FileNotFoundException

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
            behavior.isDraggable = false
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
        textDisplay.text = textSelected

        val qrCodeDisplay = view.findViewById<ImageView>(R.id.qrcode_display)
        val qrCodeBitmap = PorkUtils.generateQRCode("$textSelected", 200, 200)

        qrCodeBitmap?.let {
            qrCodeDisplay.setImageBitmap(it)
        }

        val addFavouriteBtn = view.findViewById<Button>(R.id.add_favourite)
        addFavouriteBtn.isEnabled = !FavouritesUtils.isFavorite(textSelected)
        addFavouriteBtn.setOnClickListener {
            FavouritesUtils.addFavourite(requireContext(), "$textSelected")
            Toast.makeText(context, getString(R.string.added), Toast.LENGTH_SHORT).show()
            addFavouriteBtn.isEnabled = false
        }

        val saveBtn = view.findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener {
            if (qrCodeBitmap != null && saveToGallery(qrCodeBitmap)) {
                Toast.makeText(context, getString(R.string.saved_gallery), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
                Log.e("qrcode", "Unable to save image")
            }
        }

        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)
        dismissBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun saveToGallery(bitmap: Bitmap): Boolean {
        val contentResolver = context?.contentResolver
        val insertUri = contentResolver?.insert (
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        )

        return try {
            val outputStream = insertUri?.let {
                contentResolver.openOutputStream(it, "rw")
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } catch (e: FileNotFoundException){
            e.printStackTrace()
            false
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