package com.br.apsmobile.activity.app

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blackcat.currencyedittext.CurrencyEditText
import com.br.apsmobile.R
import com.br.apsmobile.adapter.AdapterMoviments
import com.br.apsmobile.helper.GetFirebase
import com.br.apsmobile.helper.GetMask
import com.br.apsmobile.model.Moviment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var movimentList: MutableList<Moviment> = mutableListOf()
    private lateinit var adapterMoviment: AdapterMoviments

    private lateinit var db: FirebaseFirestore
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        configRv()

        configCliques()

        getMoviments()
    }

    private fun configCliques() {
        card_ganhos.setOnClickListener { showDialogGanhos() }
        card_gastos.setOnClickListener { showDialogGastos() }
    }

    private fun showDialogGanhos() {
        val view: View = layoutInflater.inflate(R.layout.layout_dialog_ganhos, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alertDialog.setView(view)

        val edt_description = view.findViewById<EditText>(R.id.edt_description)
        val edt_value = view.findViewById<CurrencyEditText>(R.id.edt_value)
        edt_value.locale = Locale("PT", "br")

        view.findViewById<Button>(R.id.btn_save).setOnClickListener {

            val description = edt_description.text.toString()
            val value = edt_value.rawValue / 100

            if (description.isNotBlank()) {
                if (value > 0) {

                    val movimentoRef = db
                        .collection("movimentos")
                        .document(GetFirebase.getIdFirebase())
                        .collection("movimentos")
                        .document()

                    val movimento = Moviment(
                        id = movimentoRef.id,
                        description = description,
                        value = value.toDouble(),
                        type = "ganhos"
                    )

                    movimentoRef.set(movimento)

                    val updates = hashMapOf<String, Any>(
                        "date" to FieldValue.serverTimestamp()
                    )

                    movimentoRef.update(updates).addOnCompleteListener { getMoviments() }

                    dialog.dismiss()

                } else {
                    edt_value.error = "Informe o valor."
                }
            } else {
                edt_description.error = "Informe uma descrição."
            }

        }

        view.findViewById<Button>(R.id.btn_close).setOnClickListener { dialog.dismiss() }

        dialog = alertDialog.create()
        dialog.show()

    }

    private fun showDialogGastos() {
        val view: View = layoutInflater.inflate(R.layout.layout_dialog_gastos, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alertDialog.setView(view)

        val edt_description = view.findViewById<EditText>(R.id.edt_description)
        val edt_value = view.findViewById<CurrencyEditText>(R.id.edt_value)
        edt_value.locale = Locale("PT", "br")

        view.findViewById<Button>(R.id.btn_save).setOnClickListener {

            val description = edt_description.text.toString()
            val value = edt_value.rawValue / 100

            if (description.isNotBlank()) {
                if (value > 0) {

                    val movimentoRef = db
                        .collection("movimentos")
                        .document(GetFirebase.getIdFirebase())
                        .collection("movimentos")
                        .document()

                    val movimento = Moviment(
                        id = movimentoRef.id,
                        description = description,
                        value = value.toDouble(),
                        type = "gastos"
                    )

                    movimentoRef.set(movimento)

                    val updates = hashMapOf<String, Any>(
                        "date" to FieldValue.serverTimestamp()
                    )

                    movimentoRef.update(updates).addOnCompleteListener {
                        getMoviments()
                    }

                    dialog.dismiss()

                } else {
                    edt_value.error = "Informe o valor."
                }
            } else {
                edt_description.error = "Informe uma descrição."
            }

        }

        view.findViewById<Button>(R.id.btn_close).setOnClickListener { dialog.dismiss() }

        dialog = alertDialog.create()
        dialog.show()

    }

    private fun getMoviments() {
        db.collection("movimentos")
            .document(GetFirebase.getIdFirebase())
            .collection("movimentos")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                movimentList.clear()
                for (moviment in documentSnapshot) {
                    val mov = moviment.toObject(Moviment::class.java)
                    movimentList.add(mov)
                }

                progress_bar.visibility = View.GONE

                movimentList.reverse()
                adapterMoviment.notifyDataSetChanged()

                configBalance()
            }
            .addOnFailureListener { exception ->

            }
    }

    private fun configRv() {
        rv_moviments.layoutManager = LinearLayoutManager(this)
        rv_moviments.setHasFixedSize(true)
        adapterMoviment = AdapterMoviments(movimentList, this)
        rv_moviments.adapter = adapterMoviment
    }

    private fun configBalance() {

        text_balance.text = ""

        var gastos = 0.0
        var ganhos = 0.0

        for (movimento in movimentList) {
            if (movimento.type == "gastos") {
                gastos += movimento.value
            } else {
                ganhos += movimento.value
            }
        }

        progress_bar_balance.visibility = View.GONE

        val balance = ganhos - gastos
        text_balance.text = getString(R.string.text_value, GetMask.getValue(balance))
    }

}