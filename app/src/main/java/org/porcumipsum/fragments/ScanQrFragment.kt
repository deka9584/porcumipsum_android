package org.porcumipsum.fragments

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.journeyapps.barcodescanner.*
import org.porcumipsum.R
import org.porcumipsum.utils.PorkUtils

class ScanQrFragment : BottomSheetDialogFragment() {
    private var cameraPreview: BarcodeView? = null

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
        return inflater.inflate(R.layout.fragment_scan_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sheetContainer = view.parent as? ViewGroup
        sheetContainer?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        val dismissBtn = view.findViewById<ImageButton>(R.id.dismiss_btn)
        cameraPreview = view.findViewById(R.id.camera_preview)

        when (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                startDecode()
            }

            PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    100
                )
            }
        }

        dismissBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        cameraPreview?.resume()
    }

    override fun onPause() {
        super.onPause()
        cameraPreview?.pause()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        cameraPreview?.stopDecoding()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startDecode()
        }
    }

    private fun startDecode() {
        cameraPreview?.decodeContinuous { result ->
            if (result != null) {
                PorkUtils.showCreateQrSheet(requireActivity().supportFragmentManager, result.text)
                dismiss()
            }
        }
    }
}