package com.example.zorro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var mail : EditText
    private lateinit var password : EditText
    private lateinit var login_button : Button
    private lateinit var signup_button : Button
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null)
        {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        login_button = findViewById(R.id.login_button);
        signup_button = findViewById(R.id.signup_button);

        signup_button.setOnClickListener{
            val intent = Intent(this, Signup::class.java)
            startActivity(intent);
        }
        login_button.setOnClickListener{
            val email = mail.text.toString()
            val password = password.text.toString()

            login(email,password);
        }

    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
//                    updateUI(user)
                    val intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                    Toast.makeText(this@Login, "User doesn't exists, create a new account", Toast.LENGTH_SHORT).show()
                }
            }
    }
}