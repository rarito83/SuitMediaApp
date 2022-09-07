package com.example.suitmediaapp.screen.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.suitmediaapp.databinding.ActivityHomeViewBinding
import com.example.suitmediaapp.screen.event.EventView
import com.example.suitmediaapp.screen.guest.GuestView

class HomeView : AppCompatActivity() {

    companion object {
        private const val NAME = "NAME"

        fun newIntent(context: Context): Intent {
            return Intent(context, HomeView::class.java)
        }
    }

    private lateinit var binding: ActivityHomeViewBinding
    private var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initView()
    }

    private fun initData() {
        userName = intent.getStringExtra(NAME) ?: ""
    }

    private fun initView() {
        with(binding) {
            tvName.text = userName

            btnEvent.setOnClickListener {
                startActivity(EventView.newIntent(this@HomeView))
            }

            btnGuest.setOnClickListener {
                startActivity(GuestView.newIntent(this@HomeView))
            }
        }
    }
}