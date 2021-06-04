package com.br.apsmobile.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GetFirebase {

    companion object {

        // Retorna a instancia do Firebase Database
        fun getDatabase() = FirebaseFirestore.getInstance()

        // Retorna a instancia do Firebase Auth
        fun getAuth() = FirebaseAuth.getInstance()

        // Retorna se o acesso está autenticado ( true / false )
        fun getAutenticado() = getAuth().currentUser != null

        // Retorna o id do acesso autenticado
        fun getIdFirebase() = getAuth().uid.toString()

        // Valida erros de cadastro e login
        fun getMsg(erro: String): String {
            return when {
                erro.contains("The email address is already in use by another account.") -> {
                    "O endereço de e-mail já está sendo usado por outra conta."
                }
                erro.contains("The email address is badly formatted.") -> {
                    "O endereço de e-mail está formatado incorretamente."
                }
                erro.contains("Password should be at least 6 characters") -> {
                    "A senha fornecida é inválida. A senha deve ter pelo menos 6 caracteres."
                }
                erro.contains("There is no user record corresponding to this identifier. The user may have been deleted.") -> {
                    "Nenhuma conta encontrada com este endereço de e-mail."
                }
                erro.contains("The password is invalid or the user does not have a password.") -> {
                    "Senha inválida, tente novamente."
                }
                else -> {
                    "Não foi possível realizar a operação, por favor tente novamente mais tarde."
                }
            }
        }
    }

}