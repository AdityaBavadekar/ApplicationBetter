package com.adityaamolbavadekar.android.apps.better.ui.accountinfo

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.adityaamolbavadekar.android.apps.better.databinding.AccountInfoFragmentBinding
import com.adityaamolbavadekar.android.apps.better.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.parse.ParseUser


class AccountInfoFragment : Fragment() {

    private lateinit var binding: AccountInfoFragmentBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = AccountInfoFragmentBinding.inflate(layoutInflater)
        auth = Firebase.auth
        val c = auth.currentUser!!
        var name = ""
        var about = ""
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        pref.apply {
            name = getString("ACCOUNT_USER_PUBLIC_NAME", "Not set yet")!!
            about = getString("ACCOUNT_USER_PUBLIC_ABOUT", "Not set yet")!!
        }

        val displayName = auth.currentUser!!.displayName
        if (displayName != null) binding.name.setText(displayName)
        else binding.name.setText(name)
        binding.about.setText(about)
        binding.phoneNumber.setText(c.phoneNumber!!.toString())
        binding.editButton.setOnClickListener {

        }
        binding.frameLayout.setOnClickListener {

        }

        binding.about.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                if (s != null) {
                    val t = s.toString()
                    if (!TextUtils.isEmpty(t)) {


                        binding.editButton.isVisible = true
                        binding.editButton.setOnClickListener {
                            pref.edit {
                                putString(Constants.ACCOUNT_ABOUT, t)
                            }
                            Toast.makeText(requireContext(), "About saved", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        binding.editButton.isVisible = false
                    }
                }

            }
        })

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            try {
                ParseUser.logOut()
            } catch (e: Exception) {
            }
            val am = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.clearApplicationUserData()
            requireActivity().finishAndRemoveTask()
        }



        return binding.root
    }
}