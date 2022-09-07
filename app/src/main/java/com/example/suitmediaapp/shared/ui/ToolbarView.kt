package com.example.suitmediaapp.shared.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.example.suitmediaapp.R
import com.example.suitmediaapp.databinding.ItemToolbarBinding

class ToolbarView: ConstraintLayout {

    private val TAG = ToolbarView::class.java.simpleName

    /**
     * Variables
     */
    //State
    private lateinit var binding: ItemToolbarBinding

    private var listener: ToolbarListener? = null

    /**
     * Constructors
     */
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    /**
     * Initialization
     */
    private fun init(context: Context, attributeSet: AttributeSet?) {
        //Inflate view
        binding = ItemToolbarBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.item_toolbar, this, true)
        )

//        binding.connectionStatus.setListener(object : ConnectionStatusView.ConnectionListener {
//            override fun onConnectionChange(isConnected: Boolean) {
//                listener?.onConnectionChanged(isConnected)
//            }
//        })
//
//        binding.connectionStatus.post {
//            val width = binding.connectionStatus.width.plus(convertDpToPx(24))
//            binding.lblTitle.setMarginLR(width)
//        }
    }

    private fun TextView.setMarginLR(margin: Int) {
        val params: LayoutParams = this.layoutParams as LayoutParams
        params.setMargins(margin, params.topMargin, margin, params.bottomMargin)
        this.layoutParams = params
    }

    fun setConnectionStatus(owner: LifecycleOwner) {
//        binding.connectionStatus.setConnectionStatus(context, owner)
    }

    fun toolbar(): Toolbar {
        return binding.toolbar
    }

    fun setTitle(title: String) {
        binding.lblTitle.text = title
    }

//    fun setRightIcon(icon: Drawable?, rightClicked: (view: View) -> Unit) {
//        with(binding) {
//            iconNotification.visibility = View.VISIBLE
//            iconNotification.setOnClickListener {
//                rightClicked(iconNotification)
//            }
//            iconNotification.setImageDrawable(icon)
//        }
//
//    }
//
//    fun setRightIconVisibility(isVisible: Boolean) {
//        with(binding) {
//            if (isVisible) {
//                iconNotification.visibility = View.VISIBLE
//            } else {
//                iconNotification.visibility = View.GONE
//            }
//        }
//    }

    fun setListener(listener: ToolbarListener) {
        this.listener = listener
    }

    fun hideNavigationIcon() {
        binding.toolbar.navigationIcon = null
    }

    interface ToolbarListener {
        fun onConnectionChanged(isConnected: Boolean)
    }
}