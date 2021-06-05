package com.br.apsmobile.activity.auth

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.br.apsmobile.R
import com.br.apsmobile.helper.GetFirebase
import kotlinx.android.synthetic.main.activity_recuperar_conta.*

class RecuperarContaActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_conta)

        configToolbar()

        configCliques()

    }

    private fun configToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun configCliques() {
        btn_reset_password.setOnClickListener { dataValidate() }
    }

    private fun dataValidate() {
        val email = edt_email.text.toString()

        if (email.isNotBlank()) {

            // Oculta o teclado do dispositivo
            ocultaTeclado()

            progress_bar.visibility = View.VISIBLE

            resetPassoword(email)
        } else {
            edt_email.requestFocus()
            edt_email.error = getString(R.string.recover_account_error_email)
        }
    }

    private fun resetPassoword(email: String) {
        GetFirebase.getAuth().sendPasswordResetEmail(
            email
        ).addOnCompleteListener { task ->

            // Oculta a progressbar
            progress_bar.visibility = View.GONE

            if (task.isSuccessful) {
                Toast.makeText(this, "E-mail enviado com sucesso.", Toast.LENGTH_SHORT).show()
            } else {
                showDialogError(GetFirebase.getMsg(task.exception?.message.toString()))
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

    // Oculta o teclado do dispositivo
    private fun ocultaTeclado() {
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            this.currentFocus?.windowToken, 0
        )
    }

}