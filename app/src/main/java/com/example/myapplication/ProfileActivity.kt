package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        val emailText = findViewById<TextView>(R.id.emailText)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)

        // Read existing data
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                emailText.text = snapshot.child("email").getValue(String::class.java)
                    ?: auth.currentUser?.email
                nameInput.setText(snapshot.child("name").getValue(String::class.java) ?: "")
                phoneInput.setText(snapshot.child("phone").getValue(String::class.java) ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })

        // Save edits
        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val updates = mapOf(
                "name" to nameInput.text.toString().trim(),
                "phone" to phoneInput.text.toString().trim()
            )
            ref.updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }

        // Logout
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}