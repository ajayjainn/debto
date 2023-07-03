package com.example.debto.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.debto.data.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DebtoViewModal : ViewModel() {
    private var database: DatabaseReference = Firebase.database.reference
    private val _friends: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList())
    private var _usernames: MutableMap<String, String> = mutableMapOf()
    private var _settlements: MutableLiveData<MutableMap<String, Float>> =
        MutableLiveData(mutableMapOf())

    val friends: MutableLiveData<ArrayList<String>>
        get() = _friends

    val usernames: MutableMap<String, String>
        get() = _usernames

    val settlements: MutableLiveData<MutableMap<String, Float>>
        get() = _settlements


    fun retrieveUsernames() {
        val usernamesRef = database.child("Users")
        usernamesRef.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (user in snapshot.children) {
                        val uname = user.child("username").value
                        val uid = user.child("uid").value
                        if (uname != null && uid != null) {
                            usernames[uid.toString()] = uname.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
        )
    }


    fun retrieveFriends() {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val friendsRef = database.child("Users").child(id).child("friends")

        friendsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendList = arrayListOf<String>()
                for (friendSnapshot in snapshot.children) {
                    val friendId = friendSnapshot.getValue(String::class.java)
                    friendId?.let {
                        friendList.add(it)
                    }
                }
                _friends.value = friendList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    fun addFriend(name: String): Boolean {

        if (name !in usernames.values || friends.value?.contains(name) == true || name == usernames[FirebaseAuth.getInstance().currentUser!!.uid]) {
            return false
        }
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val newRequestRef = database.child("Users").child(id).child("friends").push()
        newRequestRef.setValue(name)
        return true
    }


    fun addExpense(ipaid: Boolean, amount: Float, friend: String, comment: String) {
        Log.d("ifk", usernames.size.toString())
        val currusername = usernames[FirebaseAuth.getInstance().currentUser!!.uid]
        val currUserRef = database.child("Transactions").child(currusername!!).push()
        val friendRef = database.child("Transactions").child(friend).push()
        val currTime = System.currentTimeMillis()

        currUserRef.setValue(Expense(friend, amount, comment, currTime, ipaid))
        friendRef.setValue(Expense(currusername, amount, comment, currTime, !ipaid))
    }

    fun netTransactions() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return

        database.child("Users").child(currentUser.uid).child("username").get()
            .addOnSuccessListener {
                val currUserName = it.value.toString()
                val ref = database.child("Transactions").child(currUserName)
                ref.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        val netsettlements = mutableMapOf<String, Float>()

                        for (trans in snapshot.children) {
                            val amount = trans.child("amount").getValue(Float::class.java)!!
                            val friend = trans.child("friend").getValue(String::class.java)
                            val ipaid = trans.child("ipaid").getValue(Boolean::class.java)
                            val prev = netsettlements[friend.toString()] ?: 0.0

                            if (ipaid == true) {
                                netsettlements[friend.toString()] = prev.toFloat() + amount
                            } else {
                                netsettlements[friend.toString()] = prev.toFloat() - amount
                            }
                        }
                        _settlements.value = netsettlements
                        Log.d("ifk", _settlements.value!!.entries.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }

                })
            }


    }
}