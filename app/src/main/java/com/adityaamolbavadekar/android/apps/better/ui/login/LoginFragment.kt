package com.adityaamolbavadekar.android.apps.better.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.ProfileViewLoginFragmentBinding
import com.adityaamolbavadekar.android.apps.better.ui.main.MainActivity
import com.adityaamolbavadekar.android.apps.better.ui.main.TextDrawable
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.hypertrack.hyperlog.HyperLog
import java.io.File


class LoginFragment : Fragment() {

    private lateinit var binding: ProfileViewLoginFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var app: FirebaseApp = Firebase.app

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ProfileViewLoginFragmentBinding.inflate(layoutInflater)
        auth = Firebase.auth
        db = Firebase.firestore
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {

                val data = it.data

                try {
                    if (data != null) {
                        val uri = data.data!!.path
                        val path = File(uri)
                        HyperLog.e(this.javaClass.name,"${path.path} \n${path.absolutePath}")
                        Toast.makeText(
                            context,
                            "File - ${path.nameWithoutExtension}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Glide.with(requireActivity())
                            .load(path.path)
                            .error(R.drawable.ic_baseline_broken_image_24)
                            .into(binding.imageView)

                    }
                } catch (e: Exception) {
                    HyperLog.e(this.javaClass.name,e.toString())
                }

            }
        }
        var name = ""
        var about = ""
        val displayName = auth.currentUser!!.displayName
        if (displayName != null) {
            name = displayName
            binding.name.setText(name)
            db.collection(Constants.USERS_COLLECTION)
                .document(displayName)
                .get()
                .addOnSuccessListener {
                    about = it.getString("About") ?: ""
                    if (about != "") {
                        binding.name.setText(name)
                    }
                }

        }

        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    val t = s.toString()

                    if (t != "") {
                        try {
                            val text = t[0].toString()
                            val b = TextDrawable.builder()
                                .buildRoundRect(text, R.color.colorAppBar, 10)
                            binding.imageView.setImageDrawable(b)
                        } catch (e: IndexOutOfBoundsException) {

                        }
                    }
                }
            }
        })

        binding.imageView.setOnClickListener {
            launcher.launch(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),"Choose from photos"))
        }

        binding.button.setOnClickListener {
            name = binding.name.text.toString()
            about = binding.about.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(name) && name.trim() != "" && about.trim() != "") {
                Constants().setUserFirebaseName(requireContext(), auth, name)
                val pf = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val userId = pf.getString(Constants.ACCOUNT_USERID, "")!!
                val timestamp = pf.getString(Constants.ACCOUNT_CREATION_TIMESTAMP, "")!!
                val sysTime = pf.getLong(Constants.ACCOUNT_CREATION_SYS_TIMESTAMP, 0).toString()

                val hashMap = hashMapOf(
                    "CreationDate" to timestamp,
                    "UserId" to userId,
                    "SystemTime" to sysTime,
                    "Phone" to auth.currentUser!!.phoneNumber!!.toString(),
                    "Name" to name,
                    "About" to about
                )

                PreferenceManager.getDefaultSharedPreferences(requireContext()).edit {
                    putString("ACCOUNT_USER_PUBLIC_NAME", name)
                    putString("ACCOUNT_USER_PUBLIC_ABOUT", about)
                }

                db.collection(Constants.USERS_COLLECTION)
                    .document(auth.currentUser!!.phoneNumber!!.toString())
                    .set(hashMap)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.account_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                        requireContext().startActivity(
                            Intent(
                                requireContext(),
                                MainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()

                        requireContext().startActivity(
                            Intent(
                                requireContext(),
                                MainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    }
            } else {
                Snackbar.make(
                    binding.root,
                    "Please enter your name and about",
                    Snackbar.LENGTH_SHORT
                )
                    .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                    .setAction("OK") {
                    }
                    .show()
            }

        }


        return binding.root
    }


}