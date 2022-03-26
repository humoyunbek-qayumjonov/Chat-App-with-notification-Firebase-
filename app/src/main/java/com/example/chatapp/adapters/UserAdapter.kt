package com.example.chatapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemUserBinding
import com.example.chatapp.models.Message
import com.example.chatapp.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
lateinit var fireBaseAuth:FirebaseAuth
lateinit var fireBaseDatabase: FirebaseDatabase
lateinit var reference: DatabaseReference
class UserAdapter(var list:List<Users>,var onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    var theLastMessage: String? = null
    inner class MyViewHolder(var itemUserBinding: ItemUserBinding) :
        RecyclerView.ViewHolder(itemUserBinding.root) {
            @SuppressLint("SetTextI18n")
            fun onBind(users: Users){
                theLastMessage = "default"
                fireBaseAuth = FirebaseAuth.getInstance()
                fireBaseDatabase = FirebaseDatabase.getInstance()
                reference = fireBaseDatabase.getReference("users")
                reference.child("${fireBaseAuth.currentUser?.uid}/messages/${users.uid.toString()}")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val list2 = ArrayList<Message>()
                            val children = snapshot.children
                            for (child in children) {
                                val value = child.getValue(Message::class.java)
                                if (value != null) {
                                    list2.add(value)
                                }
                            }
                            val lastIndex = list2.lastIndex
                            if (lastIndex>=0){
                                theLastMessage = list2[lastIndex].messageText
                                itemUserBinding.emileTv.text = theLastMessage
                                itemUserBinding.recyclerDateTv.text = list2[lastIndex].date
                            } else{
                                    itemUserBinding.emileTv.text = "No Message"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                if (users.userOnOf){
                    itemUserBinding.imageOnline.visibility = View.VISIBLE
                }else{
                    itemUserBinding.imageOnline.visibility = View.INVISIBLE
                }


                 itemUserBinding.nameTv.text = users.displayName
                Picasso.get().load(users.photoUrl).into(itemUserBinding.imageTv)

                itemView.setOnClickListener {
                    onItemClickListener.onItemClick(users)
                }


            }
    }

    private fun lastMessage(userId:String) {



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnItemClickListener{
        fun onItemClick(users: Users)
    }

}