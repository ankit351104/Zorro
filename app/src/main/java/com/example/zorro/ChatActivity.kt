package com.example.zorro

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatActivity : AppCompatActivity() {

    private  lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton : ImageView
    private  lateinit var messageAdapter: MessageAdapter
    private  lateinit var messageList : ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    var receiverRoom : String? = null
    var senderRoom : String ?= null

    var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        FirebaseService.token = token
                    }
                }
            }
//        FirebaseService.sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE)
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
//            FirebaseService.token = it.token
//        }
        //val intent = Intent()
        // userId = intent.getStringExtra("userId")
        val name = intent.getStringExtra("name")
        val receiver_uid = intent.getStringExtra("uid")
        val sender_uid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()
        senderRoom =  receiver_uid + sender_uid
        receiverRoom =  sender_uid + receiver_uid

        supportActionBar?.title = name


        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox =  findViewById(R.id.messagebox)
        sendButton = findViewById(R.id.sendButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter


        //adding data to recyclerview
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                    // AUTOSCROLL WHEN NEW MSG COMES
                    chatRecyclerView.getAdapter()?.let {
                        chatRecyclerView.getLayoutManager()?.smoothScrollToPosition(
                            chatRecyclerView,
                            RecyclerView.State(),
                            it.getItemCount()
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        sendButton.setOnClickListener {

            val message = messageBox.text.toString()
            val messageObject = Message(message, sender_uid)
            if(message.isEmpty()){
                Toast.makeText(applicationContext,"you can't send empty message to "+name,Toast.LENGTH_SHORT).show();
                messageBox.setText("")
            }
            else{
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")

                topic = "/topics/$sender_uid"
                PushNotification(
                    NotificationData(name!!, message),
                    topic
                ).also {
                    sendNotification(it)
                }
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }
}