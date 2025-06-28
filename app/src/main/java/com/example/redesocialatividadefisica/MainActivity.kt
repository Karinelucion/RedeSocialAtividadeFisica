package com.example.redesocialatividadefisica

import UsuarioDBHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.redesocialatividadefisica.helpers.UsuarioFirestoreHelper
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var usuarioDBHelper: UsuarioDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)
        usuarioDBHelper = UsuarioDBHelper(this)

        setContentView(R.layout.activity_main)
    }

    fun btLoginOnClick(view: View) {
        launchCredentialManager()
    }

    private fun launchCredentialManager(){
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val  request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                println(result)
                handleSignIn(result.credential)
            }catch (e: Exception){
                Log.e("Erro", "Não foi possível recuperar a credencial do usuário: ${e.localizedMessage}")
            }
        }
    }

    private fun handleSignIn(credential: Credential){
        if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        }else{
            Log.w("Erro", "Credencial não é do tipo Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this){ task ->
                if(task.isSuccessful){
                    val user = auth.currentUser
                    val email = user?.email ?: ""
                    val uid = user?.uid ?: ""
                    val displayName = user?.displayName ?: ""

                    if (user != null) {
                        UsuarioFirestoreHelper.salvarUsuario(
                            user,
                            onSuccess = { Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show() },
                            onFailure = { Toast.makeText(this, "Erro ao cadastrar usuário!", Toast.LENGTH_SHORT).show() },
                            onAlreadyExists = { Toast.makeText(this, "Autenticado com sucesso!", Toast.LENGTH_SHORT).show() }
                        )
                    }

//                    val cadastrado = usuarioDBHelper.inserirUsuario(uid, email, displayName)

//                    if (cadastrado) {
//                        Toast.makeText(this, "Usuário cadastrado localmente", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this, "Usuário já cadastrado localmente", Toast.LENGTH_SHORT).show()
//                    }

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w("Erro", "signInWithCredential:failure", task.exception)
                }
            }
    }
}