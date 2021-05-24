package com.example.izzy.ui.permissions

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.izzy.R
import com.example.izzy.models.PermissionManager
import com.example.izzy.ui.main.MainFragment
import kotlinx.coroutines.*

class PermissionFragment : Fragment() {

    companion object {
        fun newInstance() = PermissionFragment()
    }

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Default)

    private lateinit var layout : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.try_again_button).setOnClickListener {
            requestPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        scope.cancel()
    }

    private fun requestPermissions() {
        scope.launch {
            withContext(Dispatchers.Main) {
                handleResult(
                    PermissionManager.requestPermissions(
                        this@PermissionFragment, 1,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
    }

    private fun handleResult(permissionResult: PermissionController.PermissionResult) {
        when(permissionResult) {
            is PermissionController.PermissionResult.PermissionGranted -> {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commit()
            }

            is PermissionController.PermissionResult.PermissionDenied -> {
                //Toast.makeText(requireContext(), R.string.access_denied, Toast.LENGTH_LONG).show()
            }
            is PermissionController.PermissionResult.PermissionBanned -> {
               //Toast.makeText(requireContext(), R.string.access_banned, Toast.LENGTH_LONG).show()
            }
            is PermissionController.PermissionResult.ShowRational -> {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.gps_allow_message)
                    .setTitle("Дозвіл")
                    .setPositiveButton("Так, хочу") { _, _ ->
                        scope.launch(Dispatchers.Main) {
                            handleResult(
                                PermissionManager.requestPermissions(
                                    this@PermissionFragment,
                                    1,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )
                        }
                    }.setNegativeButton("Ні, не хочу") { dialog, _ ->
                        dialog.dismiss()
                    }.create()
                alertDialog.show()
            }
        }
    }


}