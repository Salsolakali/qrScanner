package com.example.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        btnScan.setOnClickListener {
            navigateToScanner()
        }
    }

    private fun requestPermissions() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                )
            }
            == PackageManager.PERMISSION_GRANTED &&
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            == PackageManager.PERMISSION_GRANTED) {
            navigateToScanner()
        } else {
            val permissions =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, 100)
        }
    }

    private fun navigateToScanner() {
        activity?.let { it1 ->
            findNavController(
                it1,
                R.id.nav_host_fragment
            ).navigate(R.id.action_homeFragment_to_cameraFragment)
        }
    }
}