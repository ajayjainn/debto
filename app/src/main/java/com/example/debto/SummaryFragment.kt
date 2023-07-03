package com.example.debto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.debto.databinding.FragmentSummaryBinding
import com.example.debto.viewmodels.DebtoViewModal


class SummaryFragment : Fragment() {

    private lateinit var binding:FragmentSummaryBinding
    private val viewModel: DebtoViewModal by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSummaryBinding.inflate(layoutInflater,container,false)
        binding.addFriend.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_friendFragment)
        }
        binding.addExpense.setOnClickListener {
            findNavController().navigate(R.id.action_summaryFragment_to_newExpenseFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.netTransactions()

        val adapter = viewModel.settlements.value?.let {
            SettlementAdapter(
                it.toList()
            ) {name,amount_net->
                val action = SummaryFragmentDirections.actionSummaryFragmentToDetailFragment(name,amount_net)
                findNavController().navigate(action)
            }
        }
        binding.list.adapter = adapter

        viewModel.settlements.observe(viewLifecycleOwner) {
            binding.list.adapter = SettlementAdapter(it.toList()) {name,amount_net->
                val action = SummaryFragmentDirections.actionSummaryFragmentToDetailFragment(name,amount_net)
                findNavController().navigate(action)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}