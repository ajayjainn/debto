package com.example.debto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.debto.databinding.FragmentNewExpenseBinding
import com.example.debto.viewmodels.DebtoViewModal

class NewExpenseFragment : Fragment() {

    private var friend: String? = null
    private lateinit var binding:FragmentNewExpenseBinding
    private val viewModel: DebtoViewModal by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            friend = it.getString("friend_name")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewExpenseBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(friend!=null){
            binding.autoCompleteTextView.setText(friend.toString())
        }

        binding.addExpense.setOnClickListener {
            addExpense()
        }

        if(viewModel.friends.value!!.size==0) viewModel.retrieveFriends()

        viewModel.friends.observe(viewLifecycleOwner){
            binding.autoCompleteTextView.setAdapter(
                ArrayAdapter(requireContext(),R.layout.dropdown_friend,viewModel.friends.value!!.toArray())
            )
        }

    }

    private fun addExpense() {
        if(binding.autoCompleteTextView.text.isNullOrEmpty() || binding.amount.text.isNullOrEmpty()){
            Toast.makeText(requireContext(),"Data in complete",Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.addExpense(
            binding.iPaid.isChecked,
            binding.amount.text.toString().toFloat(),
            binding.autoCompleteTextView.text.toString(),
            binding.comment.text.toString()
        )
        Toast.makeText(requireContext(),"Expense added successfully",Toast.LENGTH_SHORT).show()

        activity?.onBackPressedDispatcher?.onBackPressed()

    }



}