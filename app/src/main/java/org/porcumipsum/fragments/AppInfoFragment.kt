package org.porcumipsum.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.porcumipsum.BuildConfig
import org.porcumipsum.R
import org.porcumipsum.clients.PorkApiClient

class AppInfoFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appNameDisplay = view.findViewById<TextView>(R.id.app_name)
        val appVersionDisplay = view.findViewById<TextView>(R.id.app_version)
        val websiteBtn = view.findViewById<Button>(R.id.website_btn)
        val dismissBtn = view.findViewById<Button>(R.id.dismiss_btn)

        appNameDisplay.text = getString(R.string.app_name)
        appVersionDisplay.text = BuildConfig.VERSION_NAME

        websiteBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PorkApiClient.getWebUrl()))
            startActivity(intent)
        }

        dismissBtn.setOnClickListener {
            dismiss()
        }
    }
}