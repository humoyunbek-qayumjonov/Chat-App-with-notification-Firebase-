package com.example.chatapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.ItemGroupBinding
import com.example.chatapp.models.GroupMessage

class GroupAdapter(var list:ArrayList<GroupMessage>,var onMyItemCliccked: OnMyItemCliccked) : RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {
    inner class MyViewHolder(var itemGroupBinding: ItemGroupBinding) :
        RecyclerView.ViewHolder(itemGroupBinding.root) {
        fun onBind(groupMessage: GroupMessage){
            //itemGroupBinding.infoGroupTv.text = groupMessage.haqida
            itemGroupBinding.nameGroupTv.text = groupMessage.name
            itemView.setOnClickListener {
                onMyItemCliccked.onMyItemClick(groupMessage)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemGroupBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnMyItemCliccked{
        fun onMyItemClick(groupMessage: GroupMessage)
    }
}