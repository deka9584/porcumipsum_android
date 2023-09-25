package org.porcumipsum.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.porcumipsum.R
import org.porcumipsum.models.GeneratorViewModel
import org.porcumipsum.utils.PorkUtils

class GeneratorFragment : Fragment() {
    private lateinit var model: GeneratorViewModel
    private var parDisplay: TextView? = null
    private var nParInput: EditText? = null
    private var minLenInput: EditText? = null
    private var commasCheck: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::model.isInitialized) {
            model = ViewModelProvider(this)[GeneratorViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val generateBtn = view.findViewById<Button>(R.id.generate_button)

        parDisplay = view.findViewById(R.id.par_display)
        nParInput = view.findViewById(R.id.n_par_input)
        minLenInput = view.findViewById(R.id.min_len_input)
        commasCheck = view.findViewById(R.id.commas_check)

        nParInput?.setText("5")
        minLenInput?.setText("400")
        commasCheck?.isChecked = true

        model.textOut.observe(viewLifecycleOwner, Observer { text ->
            parDisplay?.text = text
        })

        model.loading.observe(viewLifecycleOwner, Observer { status ->
            progressBar.visibility = if (status) View.VISIBLE else View.GONE
        })

        generateBtn?.setOnClickListener {
            generate()
        }

        parDisplay?.setOnLongClickListener {
            PorkUtils.copyToClipboard(requireContext(), parDisplay?.text)
            Toast.makeText(context, getString(R.string.clipboard_copy), Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun generate() {
        val nPar = "${nParInput?.text}".toIntOrNull() ?: 0
        val minLen = "${minLenInput?.text}".toIntOrNull() ?: 0
        val commas = commasCheck?.isChecked ?: false

        if (nPar < 1 || minLen < 1) {
            Toast.makeText(context, getString(R.string.invalid_params), Toast.LENGTH_SHORT).show()
        } else if (nPar > 10 || minLen > 4000) {
            parDisplay?.text = getString(R.string.too_high_numbers)
        } else {
            model.generatePork(requireContext(), nPar, minLen, commas)
        }
    }

    private fun clear() {
        parDisplay?.text = getString(R.string.start_text)
        model.clearData()
    }
}