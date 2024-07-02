package com.example.gymreminder

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.gymreminder.databinding.FragmentCameraBinding


class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraView: PreviewView
    private lateinit var captureButton: ImageButton
    private lateinit var retakeButton: Button
    private lateinit var saveButton: Button
    private lateinit var previewImage: ImageView
    private lateinit var cameraController: CameraController

    private var photoUri: Uri? = null
    private var navigatingFromSettings = false
    private val contentResolver by lazy {
        requireContext().contentResolver
    }

    companion object {
        private val cameraPermission = arrayOf( Manifest.permission.CAMERA)
        const val TAG = "GymApp"
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if(result) {
            Log.d(TAG, "permission granted ")
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        requestAndCheckPermissions()
        configureCamera()
        configureCaptureButton()
        configurePreviewImage()
        configureRetakeButton()
        configureSaveButton()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(navigatingFromSettings) {
            navigatingFromSettings = false
            val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if(!result) {
                showPermissionDeniedDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        photoUri = null
    }

    private fun requestAndCheckPermissions() {
        if(!checkPermissions()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            val shouldShow = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            if(shouldShow) {
                // show a dialog box and redirect to settings
                Log.d(TAG, "Should show rationale")
                showRationaleDialog()
            }
        } else {
            Log.d(TAG, "Permission granted already")
        }
    }

    private fun checkPermissions() : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Camera permission needed")
            .setMessage("Camera is needed in order to capture user' pictures")
            .setPositiveButton("Okay") { dialog,_ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                dialog.cancel()
            }
            .setNegativeButton("Cancel") {_,_ ->
                findNavController().popBackStack()
            }.create()
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission denied")
            .setMessage("Camera permission denied. You can enable it in app settings")
            .setPositiveButton("Settings") {dialog, _ ->
                val intent  = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }

                startActivity(intent)
                navigatingFromSettings = true
                dialog.cancel()
            }.setNegativeButton("Cancel") {_,_ ->
                findNavController().popBackStack()
            }.create().show()
    }

    private fun configureCamera() {
        cameraView = binding.cameraView
        val controller = LifecycleCameraController(requireContext())
        controller.bindToLifecycle(viewLifecycleOwner)
        controller.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraView.controller = controller
        this.cameraController = controller
    }

    private fun configureCaptureButton() {
        val button = binding.captureImage
        button.setOnClickListener {
            takePhoto()
        }
        captureButton = button
    }

    private fun configureRetakeButton() {
        val button = binding.retakeImage
        button.setOnClickListener {
            photoUri?.let {
                contentResolver.delete(it, null)
                hidePreviewImage()
            }
        }
        retakeButton = button
    }

    private fun configureSaveButton() {
        saveButton = binding.saveImage
        saveButton.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("photoUri", photoUri.toString())
                }
            Log.d(TAG, "configureSaveButton: $photoUri")
                setFragmentResult("cameraFragment", bundle)
                findNavController().popBackStack()
        }
    }

    private fun configurePreviewImage() {
        previewImage = binding.previewImage
    }

    private fun takePhoto() {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/GymReminder")
        }
        val contentResolver = requireContext().contentResolver
        val fileOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()
        cameraController.takePicture(fileOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let {
                    photoUri = it
                    showPreviewImage(it)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.d(TAG, "onError: unable to capture image")
            }
        })
    }

    private fun showPreviewImage(uri: Uri) {
        previewImage.visibility = View.VISIBLE
        retakeButton.visibility = View.VISIBLE
        saveButton.visibility = View.VISIBLE
        cameraView.visibility = View.GONE
        captureButton.visibility = View.GONE
        previewImage.setImageURI(uri)
    }

    private fun hidePreviewImage() {
        previewImage.visibility = View.GONE
        retakeButton.visibility = View.GONE
        saveButton.visibility = View.GONE
        cameraView.visibility = View.VISIBLE
        captureButton.visibility = View.VISIBLE
    }
}