package com.example.chatapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.adapters.TabAdapter
import com.example.chatapp.adapters.UserAdapter
import com.example.chatapp.databinding.ActivityRealBinding
import com.example.chatapp.models.PagerModel
import com.example.chatapp.models.Users
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.tab_item_category.view.*

class RealActivity : AppCompatActivity() {
    lateinit var categoryList:ArrayList<PagerModel>
    lateinit var tabAdapter:TabAdapter

    lateinit var binding: ActivityRealBinding
    lateinit var userAdapter:UserAdapter
    var list=ArrayList<Users>()
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var fireBaseDatabase:FirebaseDatabase
    lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        fireBaseDatabase = FirebaseDatabase.getInstance()
        reference = fireBaseDatabase.getReference("users")

    }
    override fun onDestroy() {
        super.onDestroy()
        reference.child(firebaseAuth.currentUser?.uid!!).get()
            .addOnSuccessListener {
                if (it.exists()){
                    reference.child("${firebaseAuth.currentUser?.uid}/userOnOf").setValue(false)
                }
            }
    }


    override fun onStop() {
        super.onStop()
            reference.child(firebaseAuth.currentUser?.uid!!).get()
                .addOnSuccessListener {
                    if (it.exists()){
                        reference.child("${firebaseAuth.currentUser?.uid}/userOnOf").setValue(false)
                    }
                }
    }

}