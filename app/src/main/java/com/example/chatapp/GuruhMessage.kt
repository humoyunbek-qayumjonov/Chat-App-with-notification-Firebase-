package com.example.chatapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.Notifications.*
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.models.GroupMessage
import com.example.chatapp.models.Message
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_guruh_message.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuruhMessage.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuruhMessage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var messageAdapter: MessageAdapter
    lateinit var fireBaseAuth: FirebaseAuth
    lateinit var fireBAseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var referenceUser:DatabaseReference
    var list=ArrayList<Users>()
    lateinit var root:View
    lateinit var apiService:ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_guruh_message, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        fireBaseAuth = FirebaseAuth.getInstance()
        fireBAseDatabase = FirebaseDatabase.getInstance()
        reference = fireBAseDatabase.getReference("groups")
        referenceUser = fireBAseDatabase.getReference("users")

        val group = arguments?.getSerializable("key_group") as GroupMessage
        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(ApiService::class.java)
        root.txt_group_name_message.text = group.name
        root.back_button.setOnClickListener {
            activity?.onBackPressed()
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }


        root.send_group_btn.setOnClickListener {
            val text = root.message_group_edit.text.toString().trim()
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
            val format = simpleDateFormat.format(Date())
            if (text == ""){
                Toast.makeText(context, "Ma`lumot bo`sh", Toast.LENGTH_SHORT).show()
                root.message_group_edit.text.clear()
            }else{
                val message = Message(text,format,fireBaseAuth.currentUser?.uid,group.name)
                val key = reference.push().key
                reference.child("${group.name}/messages/$key").setValue(message)


                referenceUser.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        list.clear()
                        val children = snapshot.children
                        for (child in children) {
                            val value = child.getValue(Users::class.java)
                            if (value != null && fireBaseAuth.currentUser!!.uid != value.uid) {
                                list.add(value)
                            }
                        }


                        Log.d(TAG, "lists: ${list.size}")

                        for (i in 0 until list.size){
                            apiService.sendNotification(
                                Sender(
                                    Data(
                                        fireBaseAuth.currentUser!!.uid,
                                        R.drawable.ic_launcher_background,
                                        text,
                                        "${group.name}",
                                        group.name.toString()
                                    ),
                                    list[i].token.toString()
                                )
                            ).enqueue(
                                object : Callback<MyResponse> {
                                    override fun onResponse(
                                        call: Call<MyResponse>,
                                        response: Response<MyResponse>
                                    ) {
                                        if (response.isSuccessful){
                                            Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                                        TODO("Not yet implemented")
                                    }

                                }
                            )
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })





                root.message_group_edit.text.clear()
            }

        }

        reference.child("${group.name}/messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Message>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        if (value != null) {
                            list.add(value)
                        }
                    }
                    messageAdapter = MessageAdapter(list, fireBaseAuth.currentUser!!.uid,"guruh")
                    root.message_group_rv.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GuruhMessage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GuruhMessage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}