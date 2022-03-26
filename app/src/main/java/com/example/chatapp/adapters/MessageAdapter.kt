package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.FromItemBinding
import com.example.chatapp.databinding.ToItemBinding
import com.example.chatapp.models.Message
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

lateinit var fireauth:FirebaseAuth
lateinit var fireDate: FirebaseDatabase
lateinit var firereference: DatabaseReference
class MessageAdapter(var list: List<Message>, var uid: String, var tipActivity:String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class FromVh(var itemFromBinding: FromItemBinding) :
        RecyclerView.ViewHolder(itemFromBinding.root) {
            fun onBind(message: Message){
                itemFromBinding.messageTv.text = message.messageText
                itemFromBinding.messageDateTv.text = message.date
                //itemFromBinding.dateTv.text = message.date
            }

    }

    inner class ToVh(var itemToBinding: ToItemBinding) : RecyclerView.ViewHolder(itemToBinding.root) {
        fun onBind(message: Message){
            itemToBinding.messageTv.text = message.messageText
            itemToBinding.messageDateTv.text = message.date
            if (tipActivity == "user"){
                itemToBinding.imgMessageGroup.visibility = View.GONE
            }else{
                var list=ArrayList<Users>()
                fireauth = FirebaseAuth.getInstance()
                fireDate = FirebaseDatabase.getInstance()
                firereference = fireDate.getReference("users")
                firereference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        list.clear()
                        val children = snapshot.children
                        for (child in children) {
                            val value = child.getValue(Users::class.java)
                            if (value!=null && value.uid == message.fromUid){
                                Picasso.get().load(value.photoUrl).into(itemToBinding.forGroupImage)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
           // itemToBinding.dateTv.text = message.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(
                FromItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{
            return ToVh(
                ToItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==1){
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        }else{
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].fromUid == uid) {
            return 1
        } else {
            return 2
        }
    }
}