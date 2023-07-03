package com.example.debto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.debto.data.User
import com.example.debto.databinding.FragmentUsernameBinding
import com.example.debto.viewmodels.DebtoViewModal
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class UsernameFragment : Fragment() {
    private val viewModel: DebtoViewModal by activityViewModels()
    private lateinit var binding: FragmentUsernameBinding
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsernameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.welcomeTv.text =
            "Welcome " + Firebase.auth.currentUser?.displayName.toString().split(" ")[0]

        binding.createAccountButton.setOnClickListener {
            if (!binding.usernameEdittext.text.isNullOrEmpty() && !binding.phoneInput.text.isNullOrEmpty()) {
                createAccount()
            } else {
                Toast.makeText(requireActivity(), "Username cannot be blank", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun createAccount() {
        val username = binding.usernameEdittext.text.toString().replace(" ", "")

        val regexPattern = Regex("^(\\+91|0)?[6789]\\d{9}\$")

        if (viewModel.usernames.values.contains(username)) {
            Toast.makeText(requireContext(), "Duplicate username", Toast.LENGTH_SHORT).show()
            return
        }

        if (!binding.phoneInput.text.toString().matches(regexPattern)) {
            Toast.makeText(requireContext(), "Enter valid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        val matchResult = regexPattern.find(binding.phoneInput.text.toString())
        var phoneNumber = ""
        if (matchResult != null) {
            phoneNumber = matchResult.value.replace("^\\+?(91|0)".toRegex(), "")
        }else {
            return
        }
        Toast.makeText(requireContext(),phoneNumber,Toast.LENGTH_SHORT).show()

        val user = User(
            username,
            Firebase.auth.currentUser?.uid.toString(),
            Firebase.auth.currentUser?.displayName.toString(),
            null,
            phoneNumber
        )
        databaseReference.child(user.uid).setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(
                    requireActivity(),
                    "Account created successfully",
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().navigate(R.id.action_usernameFragment_to_summaryFragment)
            } else {
                Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}