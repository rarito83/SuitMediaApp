package com.example.suitmediaapp.screen.event

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suitmediaapp.R
import com.example.suitmediaapp.adapter.EventAdapter
import com.example.suitmediaapp.data.model.User
import com.example.suitmediaapp.databinding.ActivityEventViewBinding
import com.example.suitmediaapp.screen.map.MapView
import com.example.suitmediaapp.screen.guest.GuestViewModel
import com.example.suitmediaapp.shared.ext.ViewModelFactory
import com.example.suitmediaapp.shared.ext.getStringRes

class EventView : AppCompatActivity(), EventAdapter.onItemClickCallback {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, EventView::class.java)
        }
    }

    private lateinit var binding: ActivityEventViewBinding
    private lateinit var eventsAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        eventsAdapter = EventAdapter()
        val factory = ViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, factory)[GuestViewModel::class.java]

        viewModel.getUsers().observe(this) { user ->
            Log.d("EventView", "Data User List $user")
            eventsAdapter.setData(user)
            eventsAdapter.setOnItemClickCallback(this)
        }

        with(binding) {
            customToolbar.apply {
                setTitle(getStringRes(R.string.events_title))
                setSupportActionBar(toolbar())
                supportActionBar?.title = title
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(false)
                toolbar().navigationIcon = resources.getDrawable(R.drawable.ic_arrow_left)
                toolbar().setNavigationOnClickListener { onBackPressed() }
            }

            rvEvents.apply {
                layoutManager = LinearLayoutManager(this@EventView)
                setHasFixedSize(true)
                adapter = eventsAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu_three, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                Log.d("EventView", "Search Menu Clicked")
                true
            }
            R.id.menu_map -> {
                startActivity(MapView.newIntent(this))
                true
            }
            else -> false
        }
    }

    override fun onItemClicked(data: User) {
        TODO("Not yet implemented")
    }
}