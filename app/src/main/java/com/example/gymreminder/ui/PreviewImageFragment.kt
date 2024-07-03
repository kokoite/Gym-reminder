package com.example.gymreminder.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.gymreminder.R
import com.example.gymreminder.databinding.FragmentPreviewImageBinding

class PreviewImageFragment : Fragment() {
    private lateinit var binding: FragmentPreviewImageBinding
    private lateinit var image: ImageView
    private var photoUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPreviewImageBinding.inflate(inflater, container, false)
        setupImage()
        addListener()
        return binding.root
    }
    
    private fun addListener() {
        setFragmentResultListener("userFragment") { requestKey: String, bundle: Bundle->
            val uri = bundle.getString("photoUri") ?: ""
            if(uri.isNotEmpty()) {
                Log.d(TAG, "addListener: Called Preview Image fragment")
                photoUri = Uri.parse(uri)
                configureImage()
            }
        }
    }
    
    fun setupImage() {
        image = binding.imageView
    }

    fun configureImage() {
        photoUri?.let {
            image.setImageURI(it)
        }
    }

    companion object {
        const val TAG = "gymApp"
    }
}