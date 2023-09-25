package org.porcumipsum.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
        val randomBtn = view.findViewById<Button>(R.id.random_button)

        textDisplay = view.findViewById(R.id.text_display)

        textDisplay?.setOnLongClickListener {
            PorkUtils.copyToClipboard(requireContext(), textDisplay?.text)
            Toast.makeText(context, getString(R.string.clipboard_copy), Toast.LENGTH_SHORT).show()
            true
        }

        model.loading.observe(viewLifecycleOwner, Observer { status ->
            if (status) {
                progressBar.visibility = View.VISIBLE
                textDisplay?.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                textDisplay?.visibility = View.VISIBLE
                updateText()
            }
        })

        model.preImp.observe(viewLifecycleOwner, Observer { list ->
            preImpPicker.tag = "preImp"
            updatePicker(preImpPicker, list)
        })

        model.saints.observe(viewLifecycleOwner, Observer { list ->
            saintsPicker.tag = "saint"
            updatePicker(saintsPicker, list)
        })

        model.postImp.observe(viewLifecycleOwner, Observer { list ->
            postImpPicker.tag = "postImp"
            updatePicker(postImpPicker, list)
        })

        preImpPicker.setOnValueChangedListener { picker, _, newVal ->
            selection["${picker.tag}"] = model.preImp.value?.getOrNull(newVal) ?: ""
            updateText()
        }

        saintsPicker.setOnValueChangedListener { picker, _, newVal ->
            selection["${picker.tag}"] = model.saints.value?.getOrNull(newVal) ?: ""
            updateText()
        }

        postImpPicker.setOnValueChangedListener { picker, _, newVal ->
            selection["${picker.tag}"] = model.postImp.value?.getOrNull(newVal) ?: ""
            updateText()
        }

        randomBtn.setOnClickListener {
            pickerRandom(preImpPicker, model.preImp.value)
            pickerRandom(saintsPicker, model.saints.value)
            pickerRandom(postImpPicker, model.postImp.value)
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

    private fun pickerRandom(picker: NumberPicker, list: List<String>?) {
        list?.let {
            val index = (0..picker.maxValue).random()
            picker.value = index
            selection["${picker.tag}"] = list.getOrNull(index) ?: ""
        }
    }

    private fun updatePicker(picker: NumberPicker, list: List<String>) {
        picker.maxValue = list.size - 1
        picker.displayedValues = list.toTypedArray()
        selection["${picker.tag}"] = list.firstOrNull() ?: ""
    }

    private fun updateText() {
        val textOut = "${selection["preImp"]} ${selection["saint"]} ${selection["postImp"]}"
        textDisplay?.text = textOut
    }
}