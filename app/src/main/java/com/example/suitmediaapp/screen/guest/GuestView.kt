package com.example.suitmediaapp.screen.guest

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.suitmediaapp.R
import com.example.suitmediaapp.adapter.GuestAdapter
import com.example.suitmediaapp.data.model.User
import com.example.suitmediaapp.databinding.ActivityGuestViewBinding
import com.example.suitmediaapp.shared.ext.ViewModelFactory
import com.example.suitmediaapp.shared.ext.getStringRes

class GuestView : AppCompatActivity(), GuestAdapter.onItemClickCallback {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, GuestView::class.java)
        }
    }

    private lateinit var binding: ActivityGuestViewBinding
    private lateinit var guestAdapter: GuestAdapter
    private lateinit var factory : ViewModelFactory
    private lateinit var viewModel : GuestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuestViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getViewModel()
        initView()
    }

    private fun initView() {

        with(binding) {
            customToolbar.apply {
                setTitle(getStringRes(R.string.guests_title))
                setSupportActionBar(toolbar())
                supportActionBar?.title = title
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(false)
                toolbar().navigationIcon = resources.getDrawable(R.drawable.ic_arrow_left)
                toolbar().setNavigationOnClickListener { onBackPressed() }
            }

            rvGuests.apply {
                layoutManager = GridLayoutManager(this@GuestView, 2)
                setHasFixedSize(true)
                adapter = guestAdapter
            }

//            swipe.apply { getViewModel() }

        }

    }

    private fun getViewModel() {
        factory = ViewModelFactory.getInstance()
        viewModel = ViewModelProvider(this, factory)[GuestViewModel::class.java]
        guestAdapter = GuestAdapter()
        viewModel.getUsers().observe(this@GuestView) { user ->
            Log.d("GuestView", "Data User -> $user")
            guestAdapter.setData(user)
            guestAdapter.setOnItemClickCallback(this@GuestView)

        }
    }

    override fun onItemClicked(dataUser: User) {
        TODO("Not yet implemented")
    }

}