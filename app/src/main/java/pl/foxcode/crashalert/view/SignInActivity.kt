package pl.foxcode.crashalert.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import pl.foxcode.crashalert.InputChecker
import pl.foxcode.crashalert.R

class SignInActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()


        button_sign_in.setOnClickListener {
            signInEmailAndPassword(
                editText_email_sign_in.text.toString(),
                editText_password_sign_in.text.toString()
            )
        }

        button_create_new_account.setOnClickListener {
            val goToSignUpActivityIntent = Intent(this, SignUpActivity::class.java)
            startActivity(goToSignUpActivityIntent)
        }

        button_forgot_password.setOnClickListener {
            if(InputChecker.isEmailCorrect(editText_email_sign_in.text.toString()))
            {
                mAuth.sendPasswordResetEmail(editText_email_sign_in.text.toString())

                Toast.makeText(applicationContext,getString(R.string.reset_password),Toast.LENGTH_LONG).show()
            }

        }

    }


    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser!=null)
        {
            val goToNextActivityIntent = Intent(applicationContext, TestActivity::class.java)
            startActivity(goToNextActivityIntent)
        }

    }

    private fun signInEmailAndPassword(email: String, password: String) {
        if (InputChecker.isEmailCorrect(email) && InputChecker.isPasswordCorrect(password))
         {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful) {
                            val goToNextActivityIntent = Intent(applicationContext, TestActivity::class.java)
                            startActivity(goToNextActivityIntent)
                        }
                        else {
                            textView_sign_in_error_message.visibility = View.VISIBLE
                        }
                    }
                })
        }
        if(!InputChecker.isEmailCorrect(email)) textInputLayout_email_sign_in.error = getString(R.string.email_error)
        if(!InputChecker.isPasswordCorrect(password)) textInputLayout_password_sign_in.error = getString(R.string.password_error)
    }


}