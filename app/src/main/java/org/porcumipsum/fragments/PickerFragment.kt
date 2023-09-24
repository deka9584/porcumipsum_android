package org.porcumipsum.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.pipsum.R
import org.porcumipsum.models.GeneratorViewModel
import org.porcumipsum.models.PickerViewModel
import org.porcumipsum.utils.PorkUtils

class PickerFragment : Fragment() {
    private lateinit var model: PickerViewModel
    private val selection = HashMap<String, String>()
    private var textDisplay: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::model.isInitialized) {
            model = ViewModelProvider(this)[PickerViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val preImpPicker = view.findViewById<NumberPicker>(R.id.pre_imp_picker)
        val saintsPicker = view.findViewById<NumberPicker>(R.id.saints_picker)
        val postImpPicker = view.findViewById<NumberPicker>(R.id.post_imp_picker)

        textDisplay = view.findViewById(R.id.text_display)

        textDisplay?.setOnLongClickListener {
            PorkUtils.copyToClipboard(requireContext(), textDisplay?.text)
            Toast.makeText(context, getString(R.string.clipboard_copy), Toast.LENGTH_SHORT).show()
            true
        }

        model.loading.observe(viewLifecycleOwner, Observer { status ->
            progressBar.visibility = if (status) View.VISIBLE else View.GONE

            if (!status) {
                updateText()
            }
        })

        model.preImp.observe(viewLifecycleOwner, Observer { list ->
            updatePicker(preImpPicker, list)
            selection["preImp"] = list.firstOrNull() ?: ""
        })

        model.saints.observe(viewLifecycleOwner, Observer { list ->
            updatePicker(saintsPicker, list)
            selection["saint"] = list.firstOrNull() ?: ""
        })

        model.postImp.observe(viewLifecycleOwner, Observer { list ->
            updatePicker(postImpPicker, list)
            selection["postImp"] = list.firstOrNull() ?: ""
        })

        preImpPicker.setOnValueChangedListener { _, _, newVal ->
            selection["preImp"] = model.preImp.value?.getOrNull(newVal) ?: ""
            updateText()
        }

        saintsPicker.setOnValueChangedListener { _, _, newVal ->
            selection["saint"] = model.saints.value?.getOrNull(newVal) ?: ""
            updateText()
        }

        postImpPicker.setOnValueChangedListener { _, _, newVal ->
            selection["postImp"] = model.postImp.value?.getOrNull(newVal) ?: ""
            updateText()
        }

        model.load(requireContext())
    }

    override fun onResume() {
        super.onResume()

        if (selection.isNotEmpty()) {
            updateText()
        }
    }

    private fun updatePicker(picker: NumberPicker, list: List<String>) {
        picker.maxValue = list.size - 1
        picker.displayedValues = list.toTypedArray()
    }

    private fun updateText() {
        val textOut = "${selection["preImp"]} ${selection["saint"]} ${selection["postImp"]}"
        textDisplay?.text = textOut
    }
}