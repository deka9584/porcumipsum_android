package org.porcumipsum.fragments

import android.os.Bundle
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
import org.porcumipsum.R
import org.porcumipsum.models.PickerViewModel
import org.porcumipsum.utils.PorkUtils

class PickerFragment : Fragment() {
    private lateinit var model: PickerViewModel
    private var preImpPicker: NumberPicker? = null
    private var saintsPicker: NumberPicker? = null
    private var postImpPicker: NumberPicker? = null
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
        val randomBtn = view.findViewById<Button>(R.id.random_button)

        textDisplay = view.findViewById(R.id.text_display)
        preImpPicker = view.findViewById(R.id.pre_imp_picker)
        saintsPicker = view.findViewById(R.id.saints_picker)
        postImpPicker = view.findViewById(R.id.post_imp_picker)

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
            preImpPicker?.maxValue = list.size - 1
            preImpPicker?.displayedValues = list.toTypedArray()
        })

        model.saints.observe(viewLifecycleOwner, Observer { list ->
            saintsPicker?.maxValue = list.size - 1
            saintsPicker?.displayedValues = list.toTypedArray()
        })

        model.postImp.observe(viewLifecycleOwner, Observer { list ->
            postImpPicker?.maxValue = list.size - 1
            postImpPicker?.displayedValues = list.toTypedArray()
        })

        val pickerHandler = { _: NumberPicker, _: Int, _: Int ->
            updateText()
        }

        preImpPicker?.setOnValueChangedListener(pickerHandler)
        saintsPicker?.setOnValueChangedListener(pickerHandler)
        postImpPicker?.setOnValueChangedListener(pickerHandler)

        randomBtn.setOnClickListener {
            randomize()
        }

        model.load(requireContext())
    }

    private fun randomize() {
        preImpPicker?.value = PorkUtils.getRndInt(0, preImpPicker?.maxValue ?: 0)
        saintsPicker?.value = PorkUtils.getRndInt(0, saintsPicker?.maxValue ?: 0)
        postImpPicker?.value = PorkUtils.getRndInt(0, postImpPicker?.maxValue ?: 0)
        updateText()
    }

    private fun updateText() {
        val preImp = model.preImp.value?.getOrNull(preImpPicker?.value ?: 0)
        val saint = model.saints.value?.getOrNull(saintsPicker?.value ?: 0)
        val postImp = model.postImp.value?.getOrNull(postImpPicker?.value ?: 0)

        saint?.let {
            val textOut = "$preImp $saint $postImp"
            textDisplay?.text = textOut
        }
    }
}