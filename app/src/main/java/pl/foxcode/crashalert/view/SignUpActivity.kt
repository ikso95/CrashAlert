package pl.foxcode.crashalert.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import pl.foxcode.crashalert.InputChecker
import pl.foxcode.crashalert.R

class SignUpActivity : AppCompatActivity() {


    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance()

        button_signUp.setOnClickListener {
            signUpNewUser(editText_email_signUp.text?.trim().toString()
                ,editText_password_signUp.text.toString()
                ,editText_password_repeat_signUp.text.toString())
        }
    }

    fun signUpNewUser(email : String, password :String, passwordRepeated : String){
        if(InputChecker.isEmailCorrect(email)
            && InputChecker.isPasswordCorrect(password)
            && InputChecker.areStringsTheSame(password, passwordRepeated))
        {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, object :
                OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if(task.isSuccessful){
                        val goToSignInIntent = Intent(applicationContext,SignInActivity::class.java)
                        startActivity(goToSignInIntent)
                    }
                    else
                    {
                        Toast.makeText(applicationContext,getString(R.string.signUpError),Toast.LENGTH_LONG).show()
                    }
                }

            })
        }
        if(!InputChecker.isEmailCorrect(email)) editText_email_signUp.error = getString(R.string.email_error)
        if(!InputChecker.isPasswordCorrect(password)) editText_password_signUp.error = getString(R.string.password_error)
        if(!InputChecker.areStringsTheSame(password,passwordRepeated)) editText_password_repeat_signUp.error = getString(R.string.password_repeated_error)
    }
}