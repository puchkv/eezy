package com.example.izzy.ui.main.compass

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.izzy.R

class CompassFragment : Fragment() {

    companion object {
        fun newInstance() = CompassFragment()
    }

    private val viewModel: CompassViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.compass_fragment, container, false)

        viewModel.CompassLiveData().observe(viewLifecycleOwner, Observer {
            root.findViewById<LinearLayoutCompat>(R.id.compass_layout).startAnimation(it.rotateAnimation)
            root.findViewById<TextView>(R.id.compass_degree_tv).text = getString(R.string.degree, -it.currentDegree.toInt())
        })

        return root
    }

}