package com.example.chatapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.chatapp.adapters.TabAdapter
import com.example.chatapp.databinding.FragmentPagerBinding
import com.example.chatapp.models.PagerModel
import com.example.chatapp.models.Users
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.tab_item_category.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PagerFragment : Fragment() {
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
    lateinit var binding:FragmentPagerBinding
    lateinit var categoryList:ArrayList<PagerModel>
    lateinit var tabAdapter: TabAdapter
    var list=ArrayList<Users>()
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var fireBaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPagerBinding.inflate(LayoutInflater.from(context))


        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        firebaseAuth = FirebaseAuth.getInstance()
        fireBaseDatabase = FirebaseDatabase.getInstance()
        reference = fireBaseDatabase.getReference("users")

        categoryList = ArrayList()
        categoryList.add(PagerModel("Chats"))
        categoryList.add(PagerModel("Groups"))
        tabAdapter = TabAdapter(categoryList, context as FragmentActivity)
        binding.viewPager.adapter = tabAdapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager, object : TabLayoutMediator.TabConfigurationStrategy {
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                tab.text = categoryList[position].titleName
            }

        }).attach()
        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val customview =tab?.customView
                    customview?.txt_tab_item?.background =
                        resources.getDrawable(R.drawable.selected)
                    customview?.txt_tab_item?.setTextColor(resources.getColor(R.color.white))
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val customView = tab?.customView
                    customView?.txt_tab_item?.background =
                        resources.getDrawable(R.drawable.unselected)
                    customView?.txt_tab_item?.setTextColor(resources.getColor(R.color.un_selected))
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            }
        )


        val tabcount = binding.tabLayout.tabCount
        for (i in 0 until tabcount) {
            val tabView = LayoutInflater.from(context).inflate(R.layout.tab_item_category, null, false)
            val tab = binding.tabLayout.getTabAt(i)
            tab?.customView = tabView
            tabView.txt_tab_item.text = categoryList[i].titleName
            if (i == 0){
                tabView.txt_tab_item?.background =
                    resources.getDrawable(R.drawable.selected)
                tabView?.txt_tab_item?.setTextColor(resources.getColor(R.color.white))
            }else{

                tabView?.txt_tab_item?.background =
                    resources.getDrawable(R.drawable.unselected)
                tabView?.txt_tab_item?.setTextColor(resources.getColor(R.color.un_selected))
            }
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}