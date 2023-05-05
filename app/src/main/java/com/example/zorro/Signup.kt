package com.example.zorro

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageRegistrar
import java.util.UUID

class Signup : AppCompatActivity() {
    private lateinit var name : EditText
    private lateinit var mail : EditText
    private lateinit var password : EditText
    private lateinit var signup_button : Button
    private lateinit var mAuth : FirebaseAuth
    //private lateinit var mDbRef: DatabaseReference
    private lateinit var mDbRef : StorageReference
    private lateinit var selectphoto: Button
    private lateinit var selectphotoimageviewregister: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

       mAuth = FirebaseAuth.getInstance()
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        signup_button = findViewById(R.id.signup_button);
        selectphoto = findViewById(R.id.selectphoto)
        selectphotoimageviewregister =  findViewById(R.id.selectphoto_imageview_register)
        mDbRef = FirebaseStorage.getInstance().getReference()
        selectphoto.setOnClickListener{
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivity(intent,)
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        signup_button.setOnClickListener{
            val user_name = name.text.toString()
            val  email = mail.text.toString()
            val password = password.text.toString()

            signup(user_name,email,password)
        }
    }

    //var selectedPhotoUri: Uri? = null

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            //Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphotoimageviewregister.setImageBitmap(bitmap)

            selectphoto.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun signup(user_name: String,email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "createUserWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
                    //addUserToDatabase(user_name,email, mAuth.currentUser?.uid!!)
                    uploadImageToFireBase()
                    val intent = Intent(this@Signup, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                    Toast.makeText(this@Signup,"mail/user already exists",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadImageToFireBase() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")


        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                //Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    //Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val  email = mail.text.toString()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

//        val user = user(name.text.toString(),email,uid,profileImageUrl)
        val user = user(uid, email,name.text.toString(), profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }

//    private fun addUserToDatabase(userName: String, email: String, uid: String) {
//        mDbRef = FirebaseDatabase.getInstance().getReference()
//
//        mDbRef.child("user").child(uid).setValue(user(userName,email,uid))
//    }
}