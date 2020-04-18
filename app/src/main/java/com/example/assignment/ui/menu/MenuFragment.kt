//package com.example.assignment.ui.menu
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.view.animation.AnimationUtils
//import android.view.animation.LayoutAnimationController
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.assignment.Common.Common
//import com.example.assignment.Common.SpacesItemDecoration
//import com.example.assignment.R
//import dmax.dialog.SpotsDialog
//import kotlinx.android.synthetic.main.fragment_redbrick_shop.*
//
//
//class MenuFragment : Fragment() {
//    private lateinit var menuViewModel: MenuViewModel
//    private lateinit var dialog:AlertDialog
//    private lateinit var layoutAnimationController: LayoutAnimationController
//    private var adapter:RedbricksAdapter?=null
//
//    private var recycler_menu : RecyclerView?=null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        menuViewModel =
//            ViewModelProviders.of(this).get(MenuViewModel::class.java)
//        val root = inflater.inflate(R.layout.fragment_redbrick_shop, container, false)
//       initViews(root);
//
//        menuViewModel.getMessageError().observe(viewLifecycleOwner, Observer {
//        Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
//        })
//
//        menuViewModel.getRedbricksList().observe(viewLifecycleOwner, Observer {
//            adapter = RedbricksAdapter(context!!,it)
//            recycler_menu!!.adapter = adapter
//        })
//        return root
//    }
//
//    private fun initViews(root:View) {
//       dialog = SpotsDialog.Builder().setContext(context).setCancelable(false).build()
//        dialog.show()
//        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
//
//      recycler_menu = root.findViewById(R.id.recycler_menu) as RecyclerView
//       recycler_menu!!.setHasFixedSize(true)
//        val layoutManager = GridLayoutManager(context,2)
//        layoutManager.orientation = RecyclerView.VERTICAL
//        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup(){
//            override fun getSpanSize(position: Int): Int {
//                return if(adapter != null)
//                {
//                    when(adapter!!.getItemViewType(position)) {
//                        Common.DEFAULT_COLUMN_COUNT -> 1
//                        Common.FULL_WIDTH_COLUMN -> 2
//                        else -> -1
//                    }
//                }else
//                    -1
//                }
//            }
//        recycler_menu!!.layoutManager = layoutManager
//        recycler_menu!!.addItemDecoration(SpacesItemDecoration(0))
//
//        }
//
//    }
