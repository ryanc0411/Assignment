package com.example.assignment.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.asksira.loopingviewpager.LoopingViewPager
import com.example.assignment.Adapter.MyBestDealsAdapter
import com.example.assignment.Adapter.MyPopularCategoriesAdapter
import com.example.assignment.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel


   var  layoutAnimationController: LayoutAnimationController?=null
    var recyclerView:RecyclerView?=null
    var viewPager: LoopingViewPager?=null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val fab: FloatingActionButton = root.findViewById(R.id.fab)
//        fab.setOnClickListener { view: View ->
//            view.findNavController().navigate(R.id.nav_cart)
//        }
        initView(root)
        //Bind date
        homeViewModel.popularList.observe(this, Observer {
            val listData =it
            val adapter = MyPopularCategoriesAdapter(context!!,listData)
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutAnimation = layoutAnimationController
        })
        homeViewModel.bestDealList.observe(this, Observer {
            val adapter = MyBestDealsAdapter(context!!,it,false)
            viewPager!!.adapter = adapter
        })
        return root
    }

    private fun initView(root:View) {
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
        viewPager = root.findViewById(R.id.viewpager) as LoopingViewPager
        recyclerView = root.findViewById(R.id.recycler_popular) as RecyclerView
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
    }



    }

