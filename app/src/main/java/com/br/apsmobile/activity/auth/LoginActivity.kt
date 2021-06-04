package com.br.apsmobile.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.br.apsmobile.activity.app.MainActivity
import com.br.apsmobile.R
import com.br.apsmobile.helper.GetFirebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.progress_bar

class LoginActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        configCliques()

    }

    private fun configCliques() {
        btn_login.setOnClickListener { loginValidate() }

        text_criar_conta.setOnClickListener {
            startActivity(Intent(this, CriarContaActivity::class.java))
        }

        text_recuperar_conta.setOnClickListener {
            startActivity(Intent(this, RecuperarContaActivity::class.java))
        }
    }

    private fun loginValidate() {
        val email = edt_email.text.toString()
        val password = edt_password.text.toString()

        if (email.isNotBlank()) {
            if (password.isNotBlank()) {

                hideKeyboard()

                progress_bar.visibility = View.VISIBLE

                loginAuth(email, password)

            } else {
                edt_password.requestFocus()
                edt_password.error = getString(R.string.authentication_register_error_password)
            }
        } else {
            edt_email.requestFocus()
            edt_email.error = getString(R.string.authentication_register_error_email)
        }

    }

    private fun loginAuth(email: String, password: String) {
        GetFirebase.getAuth().signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                progress_bar.visibility = View.GONE
                showDialogError(GetFirebase.getMsg(task.exception.toString()))
            }
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