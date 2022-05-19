package com.adityaamolbavadekar.android.apps.better.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.ActionChipBinding
import com.adityaamolbavadekar.android.apps.better.databinding.NoteItemMainBinding
import com.bumptech.glide.Glide
import com.hypertrack.hyperlog.HyperLog
import org.jsoup.Jsoup
import java.util.*
import kotlin.properties.Delegates


class MainRecyclerViewAdapter(private val context: Context, private val list: MutableList<Note>) :
        RecyclerView.Adapter<MainRecyclerViewAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: NoteItemMainBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
                NoteItemMainBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    private lateinit var _URL: String

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val note = list[position]
        holder.apply {
            binding.title.text = note.title
            binding.body.text = note.body
            binding.stamp.text = note.modificationDate

            if (note.hasImage) {
                binding.imageView.visibility = View.VISIBLE
                Glide.with(context)
                        .load(note.imagePath)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .error(R.drawable.ic_baseline_broken_image_24)
                        .into(binding.imageView)
            }
            if (note.hasLinksPreviews) {
                HyperLog.i("MainRecyclerViewAdapter","Note contains links")
                note.links.forEach { url ->
//                    addLinkPreview(binding, url)
                    _URL = url
                    HyperLog.i("MainRecyclerViewAdapter","Note link[$url]")
                    addLinkPreview(url,binding)
                }
            }

            if (note.hasLabels) {
                note.labels.forEach {
                    val v = ActionChipBinding.inflate(LayoutInflater.from(context)).chip
                    v.text = it
                    binding.chipGroup.addView(v)
                }
            }
        }
    }

    fun addLinkPreview(url: String,binding: NoteItemMainBinding) {
        val v = LayoutInflater.from(context).inflate(R.layout.richlickpreview_layout,null,false)
        getLinkInfo(url, v,binding)
    }


    class MetaData {
        var title: String = ""
        var website: String = ""
        var description: String = ""
        var imageUrl: String = ""
        var type: String = ""
    }


    class LinkPreview(private val responseListener: ResponseListener) {


        fun get(url: String) {
            HyperLog.i(this.toString(),"Link[$url] : Getting link meteData")
            GetData(url, responseListener).execute()

        }

        //START
        private class GetData(
                private val url: String,
                private val responseListener: ResponseListener
        ) : AsyncTask<Void, Void, Void>() {

            private var metaData: MetaData? = null
            private var completed by Delegates.notNull<Boolean>()
            private lateinit var exception: java.lang.Exception

            override fun doInBackground(vararg params: Void?): Void? {
                var doc: org.jsoup.nodes.Document? = null

                try {
                    var m = MetaData()
                    m.website = url


                    doc = Jsoup.connect(url)
                            .timeout(30 * 1000)
                            .get()

                    val elements = doc.getElementsByTag("meta")
                    var title = doc.select("meta[property=og:title]").attr("content")

                    if (title.isNullOrEmpty()) {
                        title = doc.title()
                    }
                    m.title = title

                    val mediaTypes = doc.select("meta[name=medium]")
                    var type = ""
                    type = if (mediaTypes.size > 0) {
                        val media = mediaTypes.attr("content")
                        if (media == "image") {
                            "photo"
                        } else {
                            media
                        }
                    } else {
                        doc.select("meta[property=og:type]").attr("content")
                    }

                    m.type = type

                    var des = doc.select("meta[name=description]").attr("content")
                    if (des.isNullOrEmpty()) {
                        des = doc.select("meta[name=Description]").attr("content")
                    }
                    if (des.isNullOrEmpty()) {
                        des = doc.select("meta[property=og:description]").attr("content")
                    }
                    if (des.isNullOrEmpty()) {
                        des = ""
                    }
                    m.description = des


                    val imageElements = doc.select("meta[property=og:image]")
                    if (imageElements.size > 0) {
                        val imagePath = imageElements.attr("content")
                        if (imagePath.isEmpty()) {
                            m.imageUrl = imagePath
                        }
                    }

                    if (m.imageUrl.isEmpty()) {
                        var src = doc.select("link[rel=image_src]").attr("href")
                        if (src.isNotEmpty()) {
                            m.imageUrl = src
                        } else {
                            src = doc.select("link[rel=apple-touch-icon]").attr("href")
                            if (src.isNotEmpty()) {
                                m.imageUrl = src
                            } else {
                                src = doc.select("link[rel=icon]").attr("href")
                                if (src.isNotEmpty()) {
                                    m.imageUrl = src
                                }
                            }
                        }
                    }

                    completed = true
                    metaData = m
                } catch (e: Exception) {
                    completed = false
                    metaData = null
                    exception = e
                }
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                if (completed) {
                    HyperLog.i(this.toString(),"Link[$url] : link retrieved")
                    val m = metaData!!
                    responseListener.onSuccess(m)
                } else if (!completed) {
                    HyperLog.i(this.toString(),"Link[$url] : error while retrieving data :$exception")
                    val e = exception
                    responseListener.onError(e)
                }
            }
        }
        //ENG


    }

    interface ResponseListener {
        fun onSuccess(metaData: MetaData)
        fun onError(exception: java.lang.Exception)
    }

    fun getLinkInfo(url: String, v: View, binding: NoteItemMainBinding) {

        LinkPreview(object : ResponseListener {
            override fun onSuccess(metaData: MetaData) {
                HyperLog.i(this.toString(),"Link[$url] : $metaData")
                val textView = v.findViewById<TextView>(R.id.textView)
                val textView2 = v.findViewById<TextView>(R.id.textViewUrl)
                val textView3 = v.findViewById<TextView>(R.id.textViewDescription)
                val imageView = v.findViewById<ImageView>(R.id.imageView)

                textView.text = metaData.title
                textView3.text = metaData.description
                textView2.text = metaData.website


                if (metaData.imageUrl != "") {
                    HyperLog.i(this.toString(),"Link[$url] : Image not empty")
                    Glide.with(context.applicationContext)
                            .load(metaData.imageUrl)
                            .placeholder(R.drawable.ic_baseline_language_24)
                            .into(imageView)
                        .clearOnDetach()
                }
                val c = binding.mainLinearLayout.childCount-1
                v.setOnClickListener {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(metaData.website)
                    context.startActivity(i)
                }
                binding.mainLinearLayout.addView(v)
            }
            override fun onError(exception: java.lang.Exception) {
                HyperLog.i(this.toString(),"Link[$url] : onError")
                v.visibility = View.GONE
            }

        }).get(url)

    }

    override fun getItemCount(): Int = list.size
}

