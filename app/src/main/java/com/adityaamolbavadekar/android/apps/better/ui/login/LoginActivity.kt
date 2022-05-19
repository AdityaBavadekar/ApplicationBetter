package com.adityaamolbavadekar.android.apps.better.ui.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.LoginBinding
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hypertrack.hyperlog.HyperLog
import com.parse.LogInCallback
import com.parse.ParsePush
import com.parse.ParseUser
import com.parse.SignUpCallback
import org.json.JSONObject
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {


    private lateinit var binding: LoginBinding

    // [START initialize_auth]
    // Initialize Firebase Auth
    private var auth = Firebase.auth

    private lateinit var launcher: ActivityResultLauncher<Intent>

    // [END initialize_auth]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HyperLog.i(this.javaClass.name.toString(), "onCreate")
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        contactPermissionCheck()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            binding.phoneNumber.setAutofillHints(View.AUTOFILL_HINT_PHONE)
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (it.data != null) {
                    val creds = it.data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                    if (creds != null) {
                        val phone = creds.id.substring(3)
                        showPhoneDialog(phone)
                    }
                }
            }
        }
        initClickListeners()
        getPhoneNumber()
        getPhone2()
    }


    private fun getPhone2() {
        val i = Credentials.getClient(this).getHintPickerIntent(
            HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        )
        try {
            startIntentSenderForResult(i.intentSender, 21, null, 0, 0, 0, Bundle())
        } catch (e: Exception) {
            HyperLog.e(this.javaClass.name.toString(), e.toString())
        }
    }

    private fun getPhoneNumber() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm.simState == TelephonyManager.SIM_STATE_READY) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_NUMBERS
                    ) ==
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val pn = tm.getLine1Number()
                    if (pn != null && !TextUtils.isEmpty(pn) && !TextUtils.isDigitsOnly(pn)) {
                        showPhoneDialog(pn)
                    }
                    return
                }
            } catch (e: Exception) {
                HyperLog.e(this.javaClass.name.toString(), e.toString())
            }
        }
    }

    private fun showPhoneDialog(pn: String) {
        AlertDialog.Builder(this)
            .setTitle("Phone Number")
            .setMessage(
                getString(
                    R.string.phone_number_auto_dialog_text_formatted,
                    pn
                )
            )
            .setPositiveButton("OK") { d, _ ->
                binding.phoneNumber.setText(pn)
                d.dismiss()
            }
            .setNeutralButton("CANCEL") { d, _ ->
                d.cancel()
            }
            .create()
            .show()
    }

    private fun initClickListeners() {


        binding.button.setOnClickListener {
            toggleProgressbarOn()
            toggleError()
            val t = binding.phoneNumber.text.toString()
            val c = binding.countryCode.text.toString()
            val plus = "+"
            if (!TextUtils.isEmpty(t) && TextUtils.isDigitsOnly(t) && TextUtils.isDigitsOnly(c) && !TextUtils.isEmpty(
                    c
                ) && t.length == 10
            ) {
                val cred = plus + c + t//+[code][number]
                onPhoneNumberEntered(cred)
            } else if (t.length != 10) {
                toggleProgressbar()
                binding.error.text = getString(R.string.invalid_phone_error)
                binding.error.visibility = View.VISIBLE
            } else {
                toggleProgressbar()
                binding.error.text = getString(R.string.pls_verify_all_details)
                binding.error.visibility = View.VISIBLE
            }
        }

        binding.resend.setOnClickListener {
            this.recreate()
        }

        binding.buttonVerify.setOnClickListener {

            toggleError()
            val c = binding.otp.text.toString()
            if (!TextUtils.isDigitsOnly(c) && !TextUtils.isEmpty(c)) {
                verifyPhoneNumberWithCode(storedVerificationId, c)
            } else {
                binding.error.text = getString(R.string.invalid_otp)
                binding.error.visibility = View.VISIBLE
            }
        }
    }

    private fun toggleError() {
        if (binding.error.visibility == View.VISIBLE) {
            binding.error.visibility = View.INVISIBLE
        }
    }

    private fun toggleOtpLayout() {
        if (binding.layout2.visibility == View.VISIBLE) {
            binding.layout2.visibility = View.INVISIBLE
        }
    }

    private fun toggleResend() {
        if (binding.resend.visibility == View.VISIBLE) {
            binding.resend.visibility = View.INVISIBLE
        }
    }

    private fun toggleTimer() {
        if (binding.timer.visibility == View.VISIBLE) {
            binding.timer.visibility = View.INVISIBLE
        }
    }

    private fun toggleVerify() {
        if (binding.buttonVerify.visibility == View.VISIBLE) {
            binding.buttonVerify.visibility = View.INVISIBLE
        }
    }

    private fun toggleProgressbar() {
        if (binding.progress.visibility == View.VISIBLE) {
            binding.progress.visibility = View.INVISIBLE
        }
    }

    private fun toggleSendButtonOn() {
        if (binding.button.visibility == View.INVISIBLE) {
            binding.button.visibility = View.VISIBLE
        }
        if (binding.button.isEnabled == false) {
            binding.button.isEnabled = true
        }
    }

    private fun toggleProgressbarOn() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun contactPermissionCheck() {
        if (!EasyPermissions.hasPermissions(this, android.Manifest.permission.READ_CONTACTS)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.better_contacts_permission_statement),
                100,
                android.Manifest.permission.READ_CONTACTS
            )
        }
    }

    private fun onPhoneNumberEntered(cred: String) {
        startPhoneNumberVerification(cred)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        // [END start_phone_auth]
    }


    // [START resend_verification]
    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }
    // [END resend_verification]


    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
        // [END verify_with_code]
    }


    private fun i(s: String) {
        HyperLog.i(TAG, s)
    }

    private fun ec(s: String, e: Exception) {
        HyperLog.e(TAG, s)
        HyperLog.exception(TAG, e)
    }

    private fun e(s: String) {
        HyperLog.e(TAG, s)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            i("onVerificationCompleted:$credential")
            toggleProgressbar()
            toggleProgressbarOn()
            binding.otp.setText(credential.smsCode!!)
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            toggleProgressbar()
            ec("onVerificationFailed", e)
            Toast.makeText(
                this@LoginActivity,
                "Something went wrong...\n${e.message}",
                Toast.LENGTH_LONG
            ).show()
            resetScreen()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            super.onCodeSent(verificationId, token)
            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
            toggleProgressbar()
            Toast.makeText(this@LoginActivity, "Code sent", Toast.LENGTH_LONG).show()
            enableOtpBox()
            //Show message and start timer enable editText
        }

        override fun onCodeAutoRetrievalTimeOut(code: String) {
            super.onCodeAutoRetrievalTimeOut(code)
            e("onCodeAutoRetrievalTimeOut")
            toggleProgressbar()
            Toast.makeText(
                this@LoginActivity,
                getString(R.string.time_out_pls_try_again),
                Toast.LENGTH_LONG
            ).show()
            resetScreen()
            //Update OtpEditText showToast
        }
    }

    private var time = 0

    private fun enableOtpBox() {
        binding.button.isVisible = false
        binding.buttonVerify.visibility = View.VISIBLE
        binding.timer.visibility = View.VISIBLE
        binding.resend.visibility = View.VISIBLE


        binding.timer.text = getString(R.string.sixty)
        val d = object : CountDownTimer(60 * 1000, 1 * 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = "$millisUntilFinished"
            }

            override fun onFinish() {
                binding.timer.text = getString(R.string.time_out)
            }
        }
        d.start()
        binding.layout2.visibility = View.VISIBLE
        //Show message and start timer enable editText
    }

    private fun resetScreen() {
        toggleProgressbar()
        toggleError()
        toggleOtpLayout()
        toggleResend()
        toggleTimer()
        toggleVerify()
        toggleSendButtonOn()
        this.recreate()
        //Tme Out Update OtpEditText showToast
    }

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    HyperLog.i(
                        this.javaClass.toString(),
                        "signInWithPhoneAuthCredential : ${credential.smsCode} ${credential.provider} ${credential.signInMethod} : true ::USER:${task.result?.user}"
                    )
                    // Sign in success, update UI with the signed-in user's information
                    toggleProgressbarOn()
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    updateUITrue(user!!)

                } else {
                    // Sign in failed, display a message and update the UI
                    toggleProgressbar()
                    ec("signInWithCredential:failure", task.exception!!)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        binding.error.isVisible = true
                        binding.error.text =
                            getString(R.string.invalid_otp)

                        AlertDialog.Builder(this)
                            .setTitle("Invalid OTP")
                            .setMessage("OTP entered was invalid\nPlease try again")
                            .setPositiveButton("OK") { d, _ ->
                                d.cancel()
                                resetScreen()
                            }
                    } else {
                        binding.error.isVisible = true
                        binding.error.text = "${task.exception!!.message}"
                        Toast.makeText(
                            this,
                            getString(com.adityaamolbavadekar.android.apps.better.R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    resetScreen()
                    // Update UI
                }
            }
    }

    private fun updateUITrue(user: FirebaseUser) {
        try {
            val obj = JSONObject()
            obj.put("alert","Contact - ${user.phoneNumber}")
            obj.put("title","New user created an account!")
            val push = ParsePush()
            push.setChannel("new_users")
            push.setData(obj)
            push.send()
            HyperLog.i(this.javaClass.name,"Push sent...")
        } catch (e: Exception) {
            HyperLog.i(this.javaClass.name,"Push was not sent - ${e.toString()}")
        }

        val pser = ParseUser()
        pser.username = (user.phoneNumber)
        pser.setPassword(user.phoneNumber)
        pser.signUpInBackground {
            if (it != null) {
                HyperLog.e(this.javaClass.name, "Parse User SignUp ERROR - ${it.toString()}")
                ParseUser.logOut()
                ParseUser.logInInBackground(
                    user.phoneNumber!!,
                    user.phoneNumber!!,
                    { parseUser, e ->
                        if (parseUser != null) {
                            HyperLog.i(this.javaClass.name, "Parse User Login Success")
                        } else {
                            HyperLog.e(
                                this.javaClass.name,
                                "Parse User Login ERROR - ${e.toString()}"
                            )
                        }
                    })
            } else {
                HyperLog.i(this.javaClass.name, "Parse User SignUp Success")
            }
        }

        Constants().setUserLoginData(this, user.phoneNumber.toString())
        val phoneNumber = user.phoneNumber!!
        val userID = user.uid
        val timestamp =
            SimpleDateFormat(Constants.TIMESTAMP_FORMAT_TIME, Locale.ENGLISH).format(Date())
        val sysTime = System.currentTimeMillis()
        toggleProgressbar()
        Constants().setUserLoginTimeData(this, timestamp, sysTime, userID, phoneNumber)
        val i = Intent(this, LoginActivity2::class.java)
        startActivity(i)
        finish()
        //END THIS ACTIVITY
    }

    // [END sign_in_with_phone]


    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken


    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        toggleProgressbarOn()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(u: FirebaseUser?) {
        if (u != null) {
            toggleProgressbar()
            updateUITrue(u!!)
        } else {
            toggleProgressbar()
        }
    }
    // [END on_start_check_user]

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //StartUploading Contacts and get Users name for suggestions and store in roomdb
    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 21 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val creds = data!!.getParcelableExtra<Credential>(Credential.EXTRA_KEY)
                if (creds != null) {
                    val phone = creds.id.substring(3)
                    showPhoneDialog(phone)
                }
            }
        }

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        //Don't do anything
    }

    companion object {
        val TAG = this.toString()
    }
}
