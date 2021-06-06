package com.br.apsmobile.activity.app

import android.os.Bundle
import android.view.View
import android.widget.*
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
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.util.*


class MainActivity : AppCompatActivity() {

    private var movimentList: MutableList<Moviment> = mutableListOf()
    private lateinit var adapterMoviment: AdapterMoviments

    private lateinit var db: FirebaseFirestore
    private lateinit var dialog: AlertDialog
    private lateinit var dialogEditRemove: AlertDialog

    private var movimentoSelecionado: Moviment? = null
    private var movimentoPosicao = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        configRv()

        configCliques()

        getMoviments()

        configDate()
    }

    private fun configCliques() {
        card_ganhos.setOnClickListener {
            movimentoSelecionado = null
            showDialogGanhos()
        }
        card_gastos.setOnClickListener {
            movimentoSelecionado = null
            showDialogGastos()
        }
    }

    private fun showDialogGanhos() {
        val view: View = layoutInflater.inflate(R.layout.layout_dialog_ganhos, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alertDialog.setView(view)

        val progress_bar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val btn_save = view.findViewById<Button>(R.id.btn_save)
        val btn_close = view.findViewById<Button>(R.id.btn_close)
        val edt_description = view.findViewById<EditText>(R.id.edt_description)
        val edt_value = view.findViewById<CurrencyEditText>(R.id.edt_value)
        edt_value.locale = Locale("PT", "br")

        if (movimentoSelecionado != null) {
            edt_description.setText(movimentoSelecionado!!.description)
            edt_value.setText(GetMask.getValue(movimentoSelecionado!!.value / 100))
        }

        btn_save.setOnClickListener {

            val description = edt_description.text.toString()
            val value = edt_value.rawValue.toDouble()

            if (description.isNotBlank()) {
                if (value > 0) {

                    progress_bar.visibility = View.VISIBLE
                    btn_save.visibility = View.GONE
                    btn_close.visibility = View.GONE

                    if (movimentoSelecionado != null) { // Edição

                        val movimentoRef = db
                            .collection("movimentos")
                            .document(GetFirebase.getIdFirebase())
                            .collection("movimentos")
                            .document(movimentoSelecionado!!.id)

                        movimentoRef
                            .update(
                                mapOf(
                                    "description" to description,
                                    "value" to value
                                )
                            )

                        movimentList.remove(movimentoSelecionado!!)

                        movimentoSelecionado!!.value = value
                        movimentoSelecionado!!.description = description

                        movimentList.add(movimentoSelecionado!!)

                        adapterMoviment.notifyDataSetChanged()
                        configBalance()

                        dialogEditRemove.dismiss()
                        dialog.dismiss()

                    } else { // Novo registro

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

                        movimentoRef.update(updates).addOnCompleteListener {
                            movimentoRef.addSnapshotListener { snapshot, e ->
                                if (snapshot != null && snapshot.exists()) {
                                    val moviment = snapshot.toObject(Moviment::class.java)
                                    if (moviment != null) {
                                        movimentList.add(moviment)
                                        adapterMoviment.notifyDataSetChanged()
                                        configBalance()
                                        dialog.dismiss()
                                    }
                                }
                            }
                        }

                    }

                } else {
                    edt_value.error = "Informe o valor."
                }
            } else {
                edt_description.error = "Informe uma descrição."
            }

        }

        btn_close.setOnClickListener { dialog.dismiss() }

        dialog = alertDialog.create()
        dialog.show()

    }

    private fun showDialogGastos() {
        val view: View = layoutInflater.inflate(R.layout.layout_dialog_gastos, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alertDialog.setView(view)

        val progress_bar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val btn_save = view.findViewById<Button>(R.id.btn_save)
        val btn_close = view.findViewById<Button>(R.id.btn_close)
        val edt_description = view.findViewById<EditText>(R.id.edt_description)
        val edt_value = view.findViewById<CurrencyEditText>(R.id.edt_value)
        edt_value.locale = Locale("PT", "br")

        btn_save.setOnClickListener {

            val description = edt_description.text.toString()
            val value = edt_value.rawValue / 100

            if (description.isNotBlank()) {
                if (value > 0) {

                    progress_bar.visibility = View.VISIBLE
                    btn_save.visibility = View.GONE
                    btn_close.visibility = View.GONE

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
                        movimentoRef.addSnapshotListener { snapshot, e ->
                            if (snapshot != null && snapshot.exists()) {
                                val moviment = snapshot.toObject(Moviment::class.java)
                                if (moviment != null) {
                                    movimentList.add(moviment)
                                    adapterMoviment.notifyDataSetChanged()
                                    configBalance()
                                    dialog.dismiss()
                                }
                            }
                        }
                    }

                } else {
                    edt_value.error = "Informe o valor."
                }
            } else {
                edt_description.error = "Informe uma descrição."
            }

        }

        btn_close.setOnClickListener { dialog.dismiss() }

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

    private fun showDialogEditRemove(position: Int, operation: Int) {
        val view: View = layoutInflater.inflate(R.layout.dialog_edt_remove, null)
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alertDialog.setView(view)

        val btn_yes = view.findViewById<Button>(R.id.btn_yes)
        val text_message = view.findViewById<TextView>(R.id.text_message)
        val img_type = view.findViewById<ImageView>(R.id.img_type)

        if (operation == MOVIMENT_REMOVE) {
            text_message.text = "Tem certeza que deseja remover este registro ?"
            img_type.setImageResource(R.drawable.ic_dialog_remove)

            btn_yes.setOnClickListener {
                removeMoviment(movimentList[position])
            }
        } else if (operation == MOVIMENT_EDIT) {
            text_message.text = "Deseja editar este registro ?"
            img_type.setImageResource(R.drawable.ic_dialog_edit)

            btn_yes.setOnClickListener {

                movimentoSelecionado = movimentList[position]
                movimentoPosicao = position

                if (movimentList[position].type == "ganhos") {
                    showDialogGanhos()
                } else if (movimentList[position].type == "gastos") {
                    showDialogGastos()
                }
            }
        }


        view.findViewById<Button>(R.id.btn_close).setOnClickListener {
            dialogEditRemove.dismiss()
            adapterMoviment.notifyDataSetChanged()
        }

        dialogEditRemove = alertDialog.create()
        dialogEditRemove.show()

    }

    private fun removeMoviment(moviment: Moviment) {
        db.collection("movimentos")
            .document(GetFirebase.getIdFirebase())
            .collection("movimentos")
            .document(moviment.id)
            .delete()

        movimentList.remove(moviment)
        adapterMoviment.notifyDataSetChanged()

        dialogEditRemove.dismiss()

        configBalance()
    }

    private fun configRv() {
        rv_moviments.layoutManager = LinearLayoutManager(this)
        rv_moviments.setHasFixedSize(true)
        adapterMoviment = AdapterMoviments(movimentList, this)
        rv_moviments.adapter = adapterMoviment

        rv_moviments.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedLeft(position: Int) {
                showDialogEditRemove(position, MOVIMENT_REMOVE)
            }

            override fun onSwipedRight(position: Int) {
                showDialogEditRemove(position, MOVIMENT_EDIT)
            }
        })
    }

    private fun configBalance() {

        text_balance.text = "R$ 0,00"

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

        val balance = (ganhos - gastos) / 100
        text_balance.text = getString(R.string.text_value, GetMask.getValue(balance))
    }

    private fun configDate() {
        val long = Timestamp(System.currentTimeMillis()).time
        text_date.text = GetMask.getDate(long, 1)
    }

    companion object {
        const val MOVIMENT_EDIT = 1
        const val MOVIMENT_REMOVE = 2
    }

}