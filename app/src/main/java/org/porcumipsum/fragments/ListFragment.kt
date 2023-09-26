package org.porcumipsum.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.porcumipsum.R
import org.porcumipsum.adapters.FavouritesAdapter
import org.porcumipsum.models.GeneratorViewModel
import org.porcumipsum.models.ListViewModel
import org.porcumipsum.models.PickerViewModel

class ListFragment : Fragment() {
    private lateinit var model: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::model.isInitialized) {
            model = ViewModelProvider(this)[ListViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanQrBtn = view.findViewById<Button>(R.id.scan_qr_button)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = FavouritesAdapter(model)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = false

        model.favourites.observe(viewLifecycleOwner, Observer { list ->
            adapter.setData(list)
        })

        scanQrBtn.setOnClickListener {
            ScanQrFragment().show(requireActivity().supportFragmentManager, "ScanQrFragmentTag")
        }

        model.loadFavourites()
    }
}