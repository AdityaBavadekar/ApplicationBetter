package com.adityaamolbavadekar.android.apps.better.ui.chatview

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.AddBottomsheetBinding
import java.io.File

class BottomSheetDatabase {

    fun showAddBottomSheet(context: AppCompatActivity):Dialog {

        //
        val b = Dialog(context)
        b.requestWindowFeature(Window.FEATURE_NO_TITLE)
        b.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        b.window?.setFlags(
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
            WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE
        )
        b.window?.statusBarColor = Color.TRANSPARENT
        b.window?.navigationBarColor = Color.TRANSPARENT
        val binding = AddBottomsheetBinding.inflate(b.layoutInflater)
        b.setContentView(binding.root)
        val dialogWindow = b.window
        dialogWindow?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        val cameraLauncher =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK) {
                    val data = it.data

                    if (data != null) {
                        val bitmap = data.extras!!.get("data")!! as Bitmap
                        Toast.makeText(context, "${bitmap.height}", Toast.LENGTH_SHORT).show()
                        b.dismiss()
                    }

                }

            }


        binding.actionCamera.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            i.type = "*/*"
            val intent = Intent.createChooser(i, "Choose from Photos")
            cameraLauncher.launch(intent)
        }


        val docsLauncher =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK) {

                    val data = it.data

                    if (data != null) {
                        val uri = data.data!!.path
                        val path = File(uri)
                        Toast.makeText(
                            context,
                            "File - ${path.nameWithoutExtension}",
                            Toast.LENGTH_SHORT
                        ).show()
                        b.dismiss()
                    }

                }

            }

        binding.actionDocuments.setOnClickListener {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "*/*"
            val intent = Intent.createChooser(i, "Choose from Photos")
            docsLauncher.launch(intent)
        }


        val photosLauncher =
            context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

                if (it.resultCode == Activity.RESULT_OK) {

                    val data = it.data

                    if (data != null) {
                        val uri = data.data!!.path
                        val path = File(uri)
                        Toast.makeText(
                            context,
                            "File - ${path.nameWithoutExtension}",
                            Toast.LENGTH_SHORT
                        ).show()
                        b.dismiss()
                    }

                }

            }

        binding.actionPhotos.setOnClickListener {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "image/*"
            val intent = Intent.createChooser(i, "Choose from Photos")
            photosLauncher.launch(intent)
        }

        dialogWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogWindow?.attributes?.windowAnimations = R.style.AppTheme_BottomSheet
        dialogWindow?.setGravity(Gravity.BOTTOM)
        //

        return b
    }

    fun showMessageMenuBottomSheet(context: Context, view: View, msg: String) {
        val popupMenu = PopupMenu(context, view,GravityCompat.START)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Copy")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Share")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Reply")
        popupMenu.menu.add(Menu.NONE, 3, 3, "Add Emoji")
        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                0 -> {
                    val c = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val data = ClipData.newPlainText("message", msg)!!
                    c!!.setPrimaryClip(data!!)!!
                    Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    val i = Intent(Intent.ACTION_SEND)
                    i.setDataAndType(Uri.parse(msg),"text/*")
                }
                2 -> {

                }
                3 -> {

                }
            }

            true
        }
        popupMenu.show()
    }


}
