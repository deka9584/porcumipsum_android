package org.porcumipsum.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import org.porcumipsum.R
import org.porcumipsum.adapters.ListAdapter
import org.porcumipsum.models.ListViewModel
import org.porcumipsum.utils.PorkUtils

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

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val scanQrBtn = view.findViewById<Button>(R.id.scan_qr_button)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val adapter = ListAdapter(
            onTextClick = { text ->
                CreateQrFragment.newInstance(text)
                    .show(requireActivity().supportFragmentManager, "CreateQrFragmentTag")
            },
            onCopyClick = { text ->
                PorkUtils.copyToClipboard(requireContext(), text)
                Toast.makeText(context, getString(R.string.clipboard_copy), Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { index ->
                model.removeElement(requireContext(), index)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.isNestedScrollingEnabled = false

        model.favourites.observe(viewLifecycleOwner, Observer { list ->
            adapter.setData(list)
        })

        swipeRefreshLayout.setOnRefreshListener {
            model.loadFavourites()
            swipeRefreshLayout.isRefreshing = false
        }

        scanQrBtn.setOnClickListener {
            ScanQrFragment().show(requireActivity().supportFragmentManager, "ScanQrFragmentTag")
        }

        val appInfoLink = view.findViewById<TextView>(R.id.app_info_link)
        appInfoLink.setOnClickListener {
            AppInfoFragment().show(requireActivity().supportFragmentManager, "AppInfoFragment")
        }

        model.loadFavourites()
    }
}