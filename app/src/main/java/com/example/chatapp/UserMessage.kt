package com.example.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.Notifications.*
import com.example.chatapp.adapters.MessageAdapter
import com.example.chatapp.models.Message
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_message.view.*
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
 * Use the [UserMessage.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserMessage : Fragment() {
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
    lateinit var textlast:String
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var root: View
    lateinit var apiService:ApiService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root =  inflater.inflate(R.layout.fragment_user_message, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        fireBaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")

        val users = arguments?.getSerializable("key_user") as Users

        apiService =
            Client.getRetrofit("https://fcm.googleapis.com/").create(ApiService::class.java)

        Picasso.get().load(users.photoUrl).into(root.top_image_group_tv)
        root.top_group_name.text = users.displayName
        if (users.userOnOf){
            root.txt_tip.text = "online"
        }else{
            root.txt_tip.text = "ofline"
        }

        root.back_button.setOnClickListener {
            activity?.onBackPressed()
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }


        root.send_btn.setOnClickListener {
            val text = root.message_edit.text.toString().trim()
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val format = simpleDateFormat.format(Date())
            if (text == ""){
                Toast.makeText(context, "Ma`lumot bo`sh", Toast.LENGTH_SHORT).show()
                root.message_edit.text.clear()
            }else{
                val message = Message(text,format,fireBaseAuth.currentUser?.uid,users.uid)
                val key = reference.push().key
                reference.child("${fireBaseAuth.currentUser?.uid}/messages/${users.uid!!}/$key").setValue(message)
                reference.child("${users.uid}/messages/${fireBaseAuth.currentUser?.uid}/$key").setValue(message)

                apiService.sendNotification(
                    Sender(
                        Data(
                            fireBaseAuth.currentUser!!.uid,
                            R.drawable.ic_launcher_background,
                            text,
                            "New message",
                            users.uid?:""
                        ), users.token!!
                    )
                ).enqueue(object :Callback<MyResponse>{
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

                })
                root.message_edit.text.clear()
            }
        }
        reference.child("${fireBaseAuth.currentUser?.uid}/messages/${users.uid}")
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
                    messageAdapter = MessageAdapter(list, fireBaseAuth.currentUser!!.uid,"user")
                    root.message_rv.adapter = messageAdapter
                    val lastIndex = list.lastIndex
                    if (lastIndex>=0){
                        var lastMessage = list[lastIndex]
                        textlast = lastMessage.messageText!!
                    }
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
         * @return A new instance of fragment UserMessage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserMessage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}