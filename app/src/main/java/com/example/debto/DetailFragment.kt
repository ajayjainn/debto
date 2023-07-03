package com.example.debto

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.debto.databinding.FragmentDetailBinding
import com.example.debto.viewmodels.DebtoViewModal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DetailFragment : Fragment() {
    private var friend: String? = null
    private var net_amount :String?=null

    private lateinit var binding:FragmentDetailBinding
    private val viewModel: DebtoViewModal by activityViewModels()

    private var database: DatabaseReference = Firebase.database.reference
    private var expRef:DatabaseReference? = null
    private var listener:ValueEventListener? = null

    private val allTrans: MutableLiveData<ArrayList<Triple<String,Float,Long>>> = MutableLiveData(ArrayList())

    private fun initializeFirebaseDatabase() {
        database = Firebase.database.reference
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val username = database.child("Users").child(currentUser.uid).child("username").get()
        username.addOnSuccessListener {
            expRef = database.child("Transactions").child(it.value.toString())
            listener = expRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val alltrans = arrayListOf<Triple<String,Float,Long>>()
                    for (trans in snapshot.children) {
                        val friend_name = trans.child("friend").getValue(String::class.java)
                        if(friend_name!=friend){
                            continue
                        }
                        var amount = trans.child("amount").getValue(Float::class.java)!!
                        var comment = trans.child("comment").getValue(String::class.java)!!
                        if(comment==""){
                            comment = "None"
                        }
                        val time = trans.child("time").getValue(Long::class.java)!!
                        val ipaid = trans.child("ipaid").getValue(Boolean::class.java)!!
                        if(!ipaid) amount*=-1
                        alltrans.add(Triple(comment,amount,time))

                    }
                    alltrans.reverse()
                    allTrans.value = alltrans
                }
                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        initializeFirebaseDatabase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            friend = it.getString("friend_name")
            net_amount = it.getString("net_amount")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DetailFragmentAdapter(allTrans.value!!)
        binding.list.adapter = adapter

        binding.name.text = friend
        binding.amountNet.text = net_amount

        val am = net_amount!!.split(" ")[1].toFloat()
        if(am>0){
            binding.name.setTextColor(Color.parseColor("#5fb49c"))
            binding.amountNet.setTextColor(Color.parseColor("#5fb49c"))
        }else if(am<0){
            binding.name.setTextColor(Color.parseColor("#CF6679"))
            binding.amountNet.setTextColor(Color.parseColor("#CF6679"))
        }
        binding.addExpense.setOnClickListener {
            addExpense()
        }
        allTrans.observe(viewLifecycleOwner){
            binding.list.adapter = DetailFragmentAdapter(allTrans.value!!)
        }

        viewModel.settlements.observe(viewLifecycleOwner) {
            binding.amountNet.text = "₹ " + it[friend].toString()
        }

    }

    private fun addExpense() {
        val action = DetailFragmentDirections.actionDetailFragmentToNewExpenseFragment(friend)
        findNavController().navigate(action)
    }

    override fun onPause() {
        if( listener!=null && expRef!=null){
            expRef!!.removeEventListener(listener!!)
        }
        super.onPause()
    }


}