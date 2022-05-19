package com.adityaamolbavadekar.android.apps.better.ui.chatview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.ItemMessageCommunityBinding
import com.adityaamolbavadekar.android.apps.better.ui.main.MainRecyclerViewAdapter
import com.bumptech.glide.Glide
import com.hypertrack.hyperlog.HyperLog
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val context: Context, private val list: MutableList<MessagesEntity>) :
    RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: ItemMessageCommunityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemMessageCommunityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val msg = list[position]
        holder.apply {

            when (msg.isFromUser) {
                true -> visualiseMe(binding, msg)
                false -> visualiseOther(binding, msg)
            }
        }

    }

    private fun visualiseMe(binding: ItemMessageCommunityBinding, msg: MessagesEntity) {
        binding.mainLinearLayoutMe.isVisible = true
        binding.mainLinearLayout.isVisible = false
        binding.senderMe.text = msg.from

        setDate(binding,msg)

        binding.messageMe.text = msg.message
        val size = binding.messageMe.urls.size
        if (size != 0) {
            val uri = binding.messageMe.urls[0].url
            val v =
                LayoutInflater.from(context).inflate(R.layout.richlickpreview_layout, null, false)
            getLinkInfo(uri, v, binding.mainLinearLayoutMe)
        }
        binding.linearLayout.gravity = GravityCompat.END

        binding.mainLinearLayoutMe.setOnClickListener {
            BottomSheetDatabase().showMessageMenuBottomSheet(context, it, msg.message)
        }

        binding.root.setOnLongClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("text", msg.message.toString()))
            true
        }
    }

    private fun setDate(binding: ItemMessageCommunityBinding, msg: MessagesEntity) {

        val t = msg.timestamp.toLong()
        val yesterday = Calendar.getInstance().add(Calendar.DAY_OF_YEAR, -1)
        val objectDate = SimpleDateFormat("dd", Locale.ENGLISH).format(Date(t)).toLong()
        val objectYear = SimpleDateFormat("MMM", Locale.ENGLISH).format(Date(t))
        val objectMonth = SimpleDateFormat("dd", Locale.ENGLISH).format(Date(t))
        val yesterdaysDate = SimpleDateFormat("dd", Locale.ENGLISH).format(Date()).toLong() - 1
        val yesterdaysYear = SimpleDateFormat("MMM", Locale.ENGLISH).format(Date())
        val yesterdaysMonth = SimpleDateFormat("dd", Locale.ENGLISH).format(Date())

        val w1 = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(t))
        val w2 = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date())

        if (w1 == w2) {
            val time = SimpleDateFormat(
                "h:mm a",
                Locale.ENGLISH
            ).format(Date(t))//RETURNS 12 FORMAT TIME
            binding.stampMe.text =
                context.getString(R.string.timestamp_formatted_today_string, time)
        } else if (objectDate == yesterdaysDate && objectMonth == yesterdaysMonth && objectYear == yesterdaysYear) {
            val time = SimpleDateFormat("h:mm a", Locale.ENGLISH).format(Date(t))
            binding.stampMe.text = context.getString(R.string.yesterday_at_formatted, time)
        } else {
            val timestamp = SimpleDateFormat(
                "dd MMM yyyy h:mm a",
                Locale.ENGLISH
            ).format(Date(t))//RETURNS 12 FORMAT TIME
            binding.stampMe.text = timestamp
        }
    }


    fun getLinkInfo(url: String, v: View, binding: LinearLayout) {

        MainRecyclerViewAdapter.LinkPreview(object : MainRecyclerViewAdapter.ResponseListener {
            override fun onSuccess(metaData: MainRecyclerViewAdapter.MetaData) {
                HyperLog.i(this.toString(), "Link[$url] : $metaData")
                val textView = v.findViewById<TextView>(R.id.textView)
                val textView2 = v.findViewById<TextView>(R.id.textViewUrl)
                val textView3 = v.findViewById<TextView>(R.id.textViewDescription)
                val imageView = v.findViewById<ImageView>(R.id.imageView)

                textView.text = metaData.title
                textView3.text = metaData.description
                textView2.text = metaData.website


                if (metaData.imageUrl != "") {
                    HyperLog.i(this.toString(), "Link[$url] : Image not empty")
                    Glide.with(context.applicationContext)
                        .load(metaData.imageUrl)
                        .placeholder(R.drawable.ic_baseline_language_24)
                        .into(imageView)
                        .clearOnDetach()
                }
                v.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(metaData.website)
                    context.startActivity(i)
                }
                binding.addView(v)
            }

            override fun onError(exception: Exception) {
                HyperLog.i(this.toString(), "Link[$url] : onError")
            }

        }).get(url)

    }

    private fun visualiseOther(binding: ItemMessageCommunityBinding, msg: MessagesEntity) {
        binding.mainLinearLayoutMe.isVisible = false
        binding.mainLinearLayout.isVisible = true
        binding.sender.text = msg.from
        val timestamp = SimpleDateFormat(
            "dd MMM yyyy h:mm a",
            Locale.ENGLISH
        ).format(Date(msg.timestamp))//RETURNS 12 FORMAT TIME
        binding.stamp.text = timestamp
        binding.message.text = msg.message
        binding.linearLayout.gravity = GravityCompat.START


        binding.root.setOnClickListener {
            BottomSheetDatabase().showMessageMenuBottomSheet(context, it, msg.message)
        }

        binding.root.setOnLongClickListener {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Message", msg.message))
            true
        }

    }


    override fun getItemCount(): Int = list.size
}
