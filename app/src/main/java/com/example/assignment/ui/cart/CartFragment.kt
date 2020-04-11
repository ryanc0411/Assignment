package com.example.assignment.ui.cart

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.assignment.R
import com.example.assignment.ui.slideshow.SlideshowViewModel


class CartFragment : Fragment() {

    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartViewModel =
            ViewModelProviders.of(this).get(CartViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        val textView: TextView = root.findViewById(R.id.text_cart)
        cartViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
