package com.example.izzy.ui.permissions

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class PermissionController : Fragment() {

    enum class PermissionCode {
        GRANTED,
        DENIED,
        BANNED
    }

    private val rationalRequest = mutableMapOf<Int, Boolean>()

    protected abstract fun onPermissionResult(permissionResult: PermissionResult)

    sealed class PermissionResult(val requestCode: Int) {
        class PermissionGranted(requestCode: Int) : PermissionResult(requestCode)
        class PermissionDenied(requestCode: Int, val deniedPermissions: List<String>) : PermissionResult(requestCode)
        class PermissionBanned(requestCode: Int, val bannedPermissions: List<String>) : PermissionResult(requestCode)
        class ShowRational(requestCode: Int) : PermissionResult(requestCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(grantResults.isNotEmpty() &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionResult(PermissionResult.PermissionGranted(requestCode))
        } else if (permissions.any { shouldShowRequestPermissionRationale(it) }) {
            onPermissionResult(
                PermissionResult.PermissionDenied(
                    requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                )
            )
        } else {
            onPermissionResult(
                PermissionResult.PermissionBanned(
                    requestCode,
                    permissions.filterIndexed { index, _ ->
                        grantResults[index] == PackageManager.PERMISSION_DENIED
                    }
                )
            )
        }
    }

    protected fun requestPermissions(requestId: Int, vararg permissions: String) {
        rationalRequest[requestId]?.let {
            requestPermissions(permissions, requestId)
            rationalRequest.remove(requestId)
            return
        }

        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        when {
            notGranted.isEmpty() ->
                onPermissionResult(PermissionResult.PermissionGranted(requestId))
            notGranted.any { shouldShowRequestPermissionRationale(it)} -> {
                rationalRequest[requestId] = true
                onPermissionResult(PermissionResult.ShowRational(requestId))
            }
            else -> {
                requestPermissions(notGranted, requestId)
            }
        }
    }
}