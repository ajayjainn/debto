package com.example.debto

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.debto.databinding.FragmentFriendListBinding
import com.example.debto.viewmodels.DebtoViewModal

/**
 * A fragment representing a list of Items.
 */
class FriendFragment : Fragment() {


    private val viewModel: DebtoViewModal by activityViewModels()
    private lateinit var binding:FragmentFriendListBinding
    private lateinit var adapter: MyFriendRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding = FragmentFriendListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.netTransactions()
        super.onViewCreated(view, savedInstanceState)
        if(viewModel.friends.value!!.size==0) viewModel.retrieveFriends()

        adapter = MyFriendRecyclerViewAdapter(viewModel.friends.value!!.toList())
        binding.list.layoutManager = LinearLayoutManager(this.context)
        binding.list.adapter = adapter
        binding.addFriend.setOnClickListener {
            addNewFriend()
        }

        viewModel.friends.observe(this.viewLifecycleOwner){
                items->items.let{
                    binding.list.adapter = MyFriendRecyclerViewAdapter(it.toList())
                }
        }
    }

    private fun addNewFriend() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter your friend's username: ")

        val viewInflated = layoutInflater.inflate(R.layout.dialog_input_name, view as ViewGroup, false)
        val input = viewInflated.findViewById<EditText>(R.id.inputName)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(viewInflated)
        builder.setPositiveButton("OK") { dialog, _ ->
            val name = input.text.toString()
            if (!viewModel.addFriend(name)){
                Toast.makeText(requireContext(),"Enter valid username",Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            Toast.makeText(requireContext(), "Friend added successfully!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()

    }
}