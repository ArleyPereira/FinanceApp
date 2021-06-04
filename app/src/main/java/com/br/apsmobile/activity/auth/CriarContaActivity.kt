package com.br.apsmobile.activity.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.br.apsmobile.activity.app.MainActivity
import com.br.apsmobile.R
import com.br.apsmobile.helper.GetFirebase
import com.br.apsmobile.model.User
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.activity_criar_conta.*

class CriarContaActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_conta)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        configCliques()

    }

    private fun configCliques() {
        btn_register.setOnClickListener { registerValidate() }
    }

    private fun registerValidate() {
        val name = edt_user_name.text.toString()
        val email = edt_email.text.toString()
        val password = edt_password.text.toString()

        if (name.isNotBlank()) {
            if (email.isNotBlank()) {
                if (password.isNotBlank()) {

                    hideKeyboard()

                    progress_bar.visibility = View.VISIBLE

                    registerAuth(name, email, password)

                } else {
                    edt_password.requestFocus()
                    edt_password.error = getString(R.string.authentication_register_error_password)
                }
            } else {
                edt_email.requestFocus()
                edt_email.error = getString(R.string.authentication_register_error_email)
            }
        } else {
            edt_user_name.requestFocus()
            edt_user_name.error = getString(R.string.authentication_register_error_name)
        }

    }

    private fun registerAuth(name: String, email: String, password: String) {
        GetFirebase.getAuth().createUserWithEmailAndPassword(
            email, password
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                val idUser = GetFirebase.getAuth().currentUser!!.uid

                saveUserFirestore(idUser, name, email, password)

            } else {
                progress_bar.visibility = View.GONE
                showDialogError(GetFirebase.getMsg(task.exception.toString()))
            }

        }
    }

    private fun saveUserFirestore(idUser: String, name: String, email: String, password: String) {

        val userRef = GetFirebase.getDatabase().collection("users").document(idUser)
        val user = User(
            id = idUser,
            name = name,
            email = email,
            password = password,
            dateRegister = 0
        )
        userRef.set(user)

        val updates = hashMapOf<String, Any>(
            "dateRegister" to FieldValue.serverTimestamp()
        )

        userRef.update(updates).addOnCompleteListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun showDialogError(message: String) {
        val view: View = layoutInflater.inflate(R.layout.dialog_error, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alertDialog.setView(view)

        val text_message = view.findViewById<TextView>(R.id.text_message)
        text_message.text = message

        view.findViewById<Button>(R.id.btn_ok).setOnClickListener { dialog.dismiss() }

        dialog = alertDialog.create()
        dialog.show()

    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            currentFocus?.windowToken, 0
        )
    }

}