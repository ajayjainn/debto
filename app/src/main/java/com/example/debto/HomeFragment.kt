package com.example.debto

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.debto.databinding.FragmentHomeBinding
import com.example.debto.viewmodels.DebtoViewModal
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking


class HomeFragment : Fragment() {
    private val viewModel: DebtoViewModal by activityViewModels()
    private lateinit var auth:FirebaseAuth
    private lateinit var binding: FragmentHomeBinding

    private lateinit var googleSignInClient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(FirebaseAuth.getInstance().currentUser!=null ){
            findNavController().navigate(R.id.action_homeFragment_to_summaryFragment)
        }

        FirebaseApp.initializeApp(requireContext())
        super.onViewCreated(view, savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)
        auth = Firebase.auth
        runBlocking{
            viewModel.retrieveUsernames()
        }

        binding.signInButton.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }


    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode==Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account:GoogleSignInAccount?=task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(requireContext(),"successful",Toast.LENGTH_SHORT)

                        if (viewModel.usernames.keys.contains(auth.currentUser!!.uid)){
                            findNavController().navigate(R.id.action_homeFragment_to_summaryFragment)
                        }else{
                            findNavController().navigate(R.id.action_homeFragment_to_usernameFragment)
                        }
                    }else{
                        Toast.makeText(requireContext(),"failed",Toast.LENGTH_SHORT)
                    }
                }
            }else{
                Toast.makeText(requireContext(),"mkc",Toast.LENGTH_SHORT)
            }
        }else{
            Toast.makeText(requireContext(),"failed",Toast.LENGTH_SHORT)
        }
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_settings)
        if (item != null) item.isVisible = false
    }


}