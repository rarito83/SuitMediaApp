package com.example.suitmediaapp.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.suitmediaapp.R
import com.example.suitmediaapp.databinding.ActivityMainViewBinding
import com.example.suitmediaapp.screen.home.HomeView
import com.example.suitmediaapp.shared.ext.getStringRes
import com.example.suitmediaapp.shared.ext.uriToFile
import com.example.suitmediaapp.shared.ui.InputView

class MainView : AppCompatActivity() {

    private lateinit var binding: ActivityMainViewBinding
    private var name = ""
    private var palind = ""

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        initView()
    }

    private fun initView() {
        with (binding) {
            imgUser.apply {
                setOnClickListener { getImageProfile() }
            }

            inputName.apply {
                setHint(getStringRes(R.string.type_name))
                setVisibleIcon(false)
                setClearTextEnable(false)
                setInputType(InputView.TYPE.TEXT)
                setListener(nameTextWatcher)
            }

            inputPalindrome.apply {
                setHint(getStringRes(R.string.type_palindrome))
                setVisibleIcon(false)
                setClearTextEnable(false)
                setInputType(InputView.TYPE.TEXT)
                setListener(palindromeTextWatcher)
            }

            btnCheck.setOnClickListener {
                if (palind.isNotEmpty()) {
                    if (checkPalindrome(palind)) {
                        Toast.makeText(this@MainView, "$palind is a Palindrome String", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainView, "$palind is not a Palindrome String", Toast.LENGTH_SHORT).show()
                    }
                } else { return@setOnClickListener }
                Log.d("MainView", "Data Check $palind")
            }

            btnNext.setOnClickListener {
                if (name.isEmpty() && palind.isEmpty()) {
                    return@setOnClickListener
                } else if (name.isNotEmpty() && checkPalindrome(palind)) {
                    val intent = Intent(HomeView.newIntent(this@MainView))
                    intent.putExtra("NAME", name)
                    startActivity(intent)
                    Log.d("MainView", "Data Name $name")
                } else if (name.isEmpty() && checkPalindrome(palind)) {
                    Toast.makeText(this@MainView, "Please insert your name", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainView, "Please check palindrome and insert your name", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val nameTextWatcher = object : InputView.InputListener {
        override fun afterTextChanged() {
            super.afterTextChanged()
            with(binding) {
                if (inputName.getText().isNotEmpty()) {
                    name = inputName.getText()
                }
            }
        }
    }

    private val palindromeTextWatcher = object : InputView.InputListener {
        override fun afterTextChanged() {
            super.afterTextChanged()
            with(binding) {
                if (inputPalindrome.getText().isNotEmpty()) {
                    palind = inputPalindrome.getText()
                }
            }
        }
    }

    private fun checkPalindrome(text: String): Boolean {
        val reverseString = text.reversed()
        return text.equals(reverseString, ignoreCase = true)
    }

    private fun getImageProfile() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            binding.imgUser.setImageURI(selectedImg)
        }
    }
}