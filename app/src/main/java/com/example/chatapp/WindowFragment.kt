package com.example.chatapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.chatapp.adapters.GroupAdapter
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.databinding.DialogAddGuruhBinding
import com.example.chatapp.models.GroupMessage
import com.example.chatapp.models.Message
import com.example.chatapp.models.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_window.*
import kotlinx.android.synthetic.main.fragment_window.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WindowFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WindowFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }
    lateinit var userAdapter: UserAdapter
    lateinit var groupAdapter:GroupAdapter
    var list=ArrayList<Users>()
    var list2 = ArrayList<String>()
    //lateinit var textlast:String
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var fireBaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var reference2: DatabaseReference
    lateinit var sharedpreferences:SharedPreferences
    lateinit var root:View
    @SuppressLint("SimpleDateFormat", "UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_window, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        fireBaseDatabase = FirebaseDatabase.getInstance()
        reference = fireBaseDatabase.getReference("users")

        if (param1 == "Chats"){
            root.add_group.visibility = View.INVISIBLE
            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            sharedpreferences = this.activity!!.getSharedPreferences("humoyun", AppCompatActivity.MODE_PRIVATE)
            fireBaseDatabase = FirebaseDatabase.getInstance()
            reference = fireBaseDatabase.getReference("users")


            var str = sharedpreferences.getString("key1","kelmadi")
            Log.d(TAG, "Default: $str")



            val email = currentUser?.email
            val displayName = currentUser?.displayName
            val photoUrl = currentUser?.photoUrl
            val uid = currentUser?.uid
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val format = simpleDateFormat.format(Date())


            val users = Users(email.toString(), displayName.toString(),photoUrl.toString(),uid.toString(),true,format,null,
                str.toString()
            )


            reference.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    val filterList = arrayListOf<Users>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Users::class.java)
                        if (value != null && uid != value.uid) {
                            list.add(value)
                        }
                        if (value!=null && value.uid == uid){
                            filterList.add(value)
                        }
                    }
                    if (filterList.isEmpty()){
                        reference.child(uid.toString()).setValue(users)
                    }
                    userAdapter = UserAdapter(list, object : UserAdapter.OnItemClickListener {
                        override fun onItemClick(users: Users) {
                            findNavController().navigate(R.id.userMessage, bundleOf("key_user" to users))
                        }

                    })
                    root.chat_rv.adapter = userAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            FirebaseMessaging.getInstance().token.addOnCompleteListener{
                if (it.isComplete){
                    var token = it.result.toString()
                    updateValue(token)
                }
            }
            Toast.makeText(context, currentUser?.email, Toast.LENGTH_SHORT).show()

        }
        if (param1 == "Groups"){
            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            fireBaseDatabase = FirebaseDatabase.getInstance()
            reference2 = fireBaseDatabase.getReference("groups")

            root.add_group.visibility = View.VISIBLE

            root.add_group.setOnClickListener {
                val alertDialog = AlertDialog.Builder(context)
                val dialog = alertDialog.create()
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val dialogView = DialogAddGuruhBinding.inflate(LayoutInflater.from(context))
                dialog.setView(dialogView.root)
                dialogView.txtYopish.setOnClickListener { dialog.hide() }
                dialogView.txtQoshish.setOnClickListener {
                    val guruhNomi = dialogView.edtAddKurs.text.toString().trim()
                    val guruhHaqida = dialogView.edtKursHaqida.text.toString().trim()
                    val groupMessage = GroupMessage(guruhNomi,guruhHaqida)

                    reference2.child(guruhNomi).setValue(groupMessage)
                    dialog.hide()
                }
                dialog.show()
            }

           reference2.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val groupNameList=ArrayList<GroupMessage>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(GroupMessage::class.java)
                        if (value != null) {
                            groupNameList.add(value)
                        }

                    }
                    groupAdapter = GroupAdapter(groupNameList, object : GroupAdapter.OnMyItemCliccked {
                        override fun onMyItemClick(groupMessage: GroupMessage) {

                            findNavController().navigate(R.id.guruhMessage, bundleOf("key_group" to groupMessage))

                        }

                    })
                    root.chat_rv.adapter = groupAdapter
                }
                override fun onCancelled(error: DatabaseError) {

                }

            })


        }
        return root
    }




    fun updateValue(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val reference= fireBaseDatabase.getReference("users").child(currentUser!!.uid)
        val hashMap= HashMap<String,Any>()
        hashMap["token"] = token

        reference.updateChildren(hashMap)

    }

    @SuppressLint("SimpleDateFormat")
    override fun onStart() {
        super.onStart()
        reference.child(firebaseAuth.currentUser?.uid!!).get()
            .addOnSuccessListener {
                if (it.exists()){
                    reference.child("${firebaseAuth.currentUser?.uid}/userOnOf").setValue(true)
                }
            }
    }
    @SuppressLint("SimpleDateFormat")
  override fun onResume() {
        super.onResume()

        reference.child(firebaseAuth.currentUser?.uid!!).get()
            .addOnSuccessListener {
                if (it.exists()){
                    reference.child("${firebaseAuth.currentUser?.uid}/userOnOf").setValue(true)
                }
            }
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WindowFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            WindowFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)

                }
            }
    }
  fun insertToken(token:String):String{
      return token
  }
}