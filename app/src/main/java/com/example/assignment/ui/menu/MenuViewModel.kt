//package com.example.assignment.ui.menu
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.assignment.Callback.IRedbricksLoadCallback
//import com.example.assignment.Common.Common
//import com.example.assignment.Model.RedbricksModel
//import com.example.assignment.ui.home.IRecyclerItemClickListener
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//
//class MenuViewModel : ViewModel(), IRedbricksLoadCallback {
//
//private var redricksListMutable : MutableLiveData<List<RedbricksModel>>?=null
//    private var messageError:MutableLiveData<String> = MutableLiveData()
//    private val redbricksCallBackListener : IRedbricksLoadCallback
//
//    init {
//         redbricksCallBackListener = this
//    }
//
//    override fun onRedbricksLoadSuccess(RedbricksModelList: List<RedbricksModel>) {
//        redricksListMutable!!.value = RedbricksModelList
//    }
//
//    override fun onRedbricksLoadFailed(message: String) {
//        messageError.value = message
//    }
//
//    fun getRedbricksList():MutableLiveData<List<RedbricksModel>>{
//        if(redricksListMutable == null)
//        {
//            redricksListMutable = MutableLiveData()
//            loadRedbricks()
//        }
//        return redricksListMutable!!
//    }
//
//    fun getMessageError():MutableLiveData<String>{
//        return messageError
//    }
//
//
//    private fun loadRedbricks() {
//        val tempList = ArrayList<RedbricksModel>()
//        val redbricksRef = FirebaseDatabase.getInstance().getReference(Common.REDBRICKS_REF)
//        redbricksRef.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//                redbricksCallBackListener.onRedbricksLoadFailed(p0.message!!)
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                for (itemSnapShot in p0!!.children)
//                {
//                    val model = itemSnapShot.getValue<RedbricksModel>(RedbricksModel::class.java)
//                    model!!.shop_id = itemSnapShot.key
//                    tempList.add(model!!)
//                }
//                redbricksCallBackListener.onRedbricksLoadSuccess(tempList)
//            }
//
//        })
//    }
//
//
//}
