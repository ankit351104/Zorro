package com.example.zorro

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<user>
    private lateinit var adapter: user_adapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var dp:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList =  ArrayList();
        adapter =  user_adapter(this, userList)
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

//notfication permission
//        val PERMISSION_REQUEST_CODE = 1
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
//                == PackageManager.PERMISSION_DENIED
//            ) {
//                Log.d("permission", "permission denied to SEND_SMS - requesting it")
//                val permissions = arrayOf<String>(Manifest.permission.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION)
//                requestPermissions(permissions, PERMISSION_REQUEST_CODE)
//            }
//        }


        mDbRef.child("users").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapShot in snapshot.children){

                    val currentUser = postSnapShot.getValue(user :: class.java)
                    if(mAuth.currentUser?.uid!=currentUser?.UID){
                        userList.add(currentUser!!)
                    }

//                    adapter.notifyDataSetChanged()
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.logout){
            mAuth.signOut()
            finish()
            val intent =  Intent(this@MainActivity, Login:: class.java)
            startActivity(intent)
            return true
        }
        return true
    }
}