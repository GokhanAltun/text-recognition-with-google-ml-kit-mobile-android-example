package com.example.readtextfromimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.readtextfromimage.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptions
import com.theartofdev.edmodo.cropper.CropImage
import java.io.IOException

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickImage.setOnClickListener {
            imageLauncher.launch("image/*")
        }
    }

    private val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        CropImage.activity(uri).start(this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            var result =CropImage.getActivityResult(data)

            if (resultCode == Activity.RESULT_OK){
                val resultUri = result.uri
                val image: InputImage
                try {
                    image = InputImage.fromFilePath(this@MainActivity, resultUri)
                    imageProcess(image)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun imageProcess(image: InputImage){
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        val result = recognizer.process(image)
            .addOnSuccessListener { visionText ->

                binding.edtxRecognizedText.setText(visionText.text)
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }

}