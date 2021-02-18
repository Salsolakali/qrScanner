package com.example.qrscanner

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Audio
import kotlinx.android.synthetic.main.fragment_camera.*


class CameraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        requestPermissions(permissions, 100)
    }

    override fun onResume() {
        super.onResume()
        initCameraView()
        initListeners()
        camera_view?.open()
    }

    override fun onPause() {
        super.onPause()
        camera_view?.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera_view?.destroy()
    }

    private fun initCameraView() {
        tvCamera?.visibility = View.GONE
        camera_view?.setLifecycleOwner(this)
        camera_view?.audio = Audio.OFF
    }

    private fun initListeners() {
        camera_view?.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                result.toBitmap() {
                    scanImage(it)
                }
                super.onPictureTaken(result)
            }

        })

        takePhoto.setOnClickListener {
            takePicture()
        }
    }

    private fun scanImage(imageBitmap: Bitmap?) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_ALL_FORMATS
            )
            .build()
        if (imageBitmap != null) {
            val image = InputImage.fromBitmap(imageBitmap, 0)

            val scanner = BarcodeScanning.getClient(options)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (!barcodes.isNullOrEmpty()) {
                        processBarCodes(barcodes)
                    } else {
                        takePicture()
                    }
                }
                .addOnFailureListener {
                    takePicture()
                    showToast("Ha fallado la captura de la imagen")
                }
        }
    }

    private fun processBarCodes(barcodes: List<Barcode>) {
        if (barcodes.size == 1) {
            for (barcode in barcodes) {
                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {
                        onpenNavigator(barcode.url.url)
                    }
                    else -> takePicture()
                }
            }
        } else {
            showToast("Demasiados codigos encontrados")
        }
    }


    fun onpenNavigator(url: String?) {
        if (!url.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
                context?.startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                intent.setPackage(null)
                context?.startActivity(intent)
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_SHORT)
            .show()
    }

    private fun takePicture() {
        if (camera_view.isOpened) {
            camera_view?.takePictureSnapshot()
            tvCamera?.visibility = View.VISIBLE
        } else {
            tvCamera?.visibility = View.GONE
        }
    }

}