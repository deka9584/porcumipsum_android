package org.porcumipsum.fragments

import android.app.Dialog
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.porcumipsum.R
import org.porcumipsum.utils.FavouritesUtils
import org.porcumipsum.utils.PorkUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

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
        val shareBtn = view.findViewById<ImageButton>(R.id.share_btn)
        val addFavouriteBtn = view.findViewById<Button>(R.id.add_favourite_btn)
        val saveBtn = view.findViewById<Button>(R.id.save_btn)
        val qrCodeDisplay = view.findViewById<ImageView>(R.id.qrcode_display)
        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)

        val qrCodeBitmap = PorkUtils.generateQRCode("$textSelected", 200, 200)

        textDisplay.text = textSelected
        addFavouriteBtn.isEnabled = !FavouritesUtils.isFavorite(textSelected)

        qrCodeBitmap?.let {
            qrCodeDisplay.setImageBitmap(it)
        }

        shareBtn.setOnClickListener {
            qrCodeBitmap?.let {
                shareImage(qrCodeBitmap)
            }
        }

        addFavouriteBtn.setOnClickListener {
            addToFavourites(textSelected)
            addFavouriteBtn.isEnabled = false
        }

        saveBtn.setOnClickListener {
            qrCodeBitmap?.let {
                saveToGallery(qrCodeBitmap)
            }
        }

        dismissBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun addToFavourites(textSelected: String?) {
        textSelected?.let {
            FavouritesUtils.addFavourite(requireContext(), it)
            Toast.makeText(context, getString(R.string.added), Toast.LENGTH_SHORT).show()
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

            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                Toast.makeText(context, getString(R.string.saved_gallery), Toast.LENGTH_SHORT).show()
            }
        } catch (e: FileNotFoundException){
            e.printStackTrace()
            Toast.makeText(context, getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareImage(bitmap: Bitmap) {
        val imagePath = "${context?.externalCacheDir}/image.png"
        val imageFile = File(imagePath)

        try {
            val fileOutputStream = FileOutputStream(imageFile)

            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                fileOutputStream.flush()
                fileOutputStream.close()

                val uri = FileProvider.getUriForFile(requireContext(),
                "${context?.applicationContext?.packageName}.provider", imageFile)

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    clipData = ClipData.newRawUri("", uri)
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            }
        }
        catch (e: IOException) {
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