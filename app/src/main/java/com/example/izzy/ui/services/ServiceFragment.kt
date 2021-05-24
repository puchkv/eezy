package com.example.izzy.ui.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.izzy.R
import com.example.izzy.models.Config
import com.example.izzy.models.LocationManager
import com.example.izzy.ui.main.MainFragment
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsStates
import kotlinx.coroutines.*

class ServiceFragment : Fragment() {

    companion object {
        fun newInstance() = ServiceFragment()
    }

    private val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Default)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.try_again_button).setOnClickListener {
                requestLocationServices(requireContext())
            }
        }

    private fun requestLocationServices(context: Context) {
        scope.launch {
            withContext(Dispatchers.Main) {
                val result = LocationManager.requestLocationServices(context)
                if(result.success!!) {
                    returnToMain()
                }
                else {
                    Toast.makeText(context, "Помилка: $result.ex?.localizedMessage", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Config.REQUEST_CHECK_SETTINGS_CODE) {
            when(resultCode) {
                Activity.RESULT_OK -> {
                    returnToMain()
                    Toast.makeText(context,"Служби локації працюють!", Toast.LENGTH_LONG).show();
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(requireContext(), "Служби локації вимкнуті", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun returnToMain() {
        requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(this.id, MainFragment.newInstance())
                .commit()
    }

}