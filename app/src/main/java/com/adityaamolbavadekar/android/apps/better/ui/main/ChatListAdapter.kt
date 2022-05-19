package com.adityaamolbavadekar.android.apps.better.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.adityaamolbavadekar.android.apps.better.R
import com.adityaamolbavadekar.android.apps.better.databinding.BetterChatListItemBinding
import com.adityaamolbavadekar.android.apps.better.room.chats_list.ChatEntity
import com.adityaamolbavadekar.android.apps.better.ui.chatview.ChatViewerActivity
import com.adityaamolbavadekar.android.apps.better.ui.chatview.CommunityChatViewerActivity

class ChatListAdapter(private val context: Context, private val list: MutableList<ChatEntity>) :
    RecyclerView.Adapter<ChatListAdapter.ItemViewHolder>() {

    class ItemViewHolder(val binding: BetterChatListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            BetterChatListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val item = list[position]

        holder.apply {

            when (item.chatTypes.toChatType()) {
                ChatItem.ChatTypes.ONLY_SELF -> initPersonal(binding, item)
                ChatItem.ChatTypes.TWO_PEOPLE -> initTwoPeople(binding, item)
                ChatItem.ChatTypes.COMMUNITY_GROUP -> initCommunity(binding, item)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
    private fun initCommunity(binding: BetterChatListItemBinding, item: ChatEntity) {
        basicLayoutSetupMain(binding, item)
    }
    private fun initTwoPeople(binding: BetterChatListItemBinding, item: ChatEntity) {
        basicLayoutSetupMain(binding, item)
    }
    private fun initPersonal(binding: BetterChatListItemBinding, item: ChatEntity) {

        basicLayoutSetup(binding, item)
    }

    private fun basicLayoutSetupMain(binding: BetterChatListItemBinding, item: ChatEntity) {
        if (item.name == "") {
            binding.title.text = item.phoneNumber
        } else {
            binding.title.text = item.name
        }
        binding.massageTextView.text = item.lastMessage
        binding.timestamp.text = item.lastMessageTimestamp

        binding.root.setOnClickListener {
            openChat(item, false)
        }

        binding.root.setOnLongClickListener {
            true
        }

    }

    private fun basicLayoutSetup(binding: BetterChatListItemBinding, item: ChatEntity) {

        if (item.name == "") {
            binding.title.text = item.phoneNumber
        } else {
            binding.title.text = item.name
        }
        binding.timestamp.isVisible = false
        binding.massageTextView.text = item.lastMessage
        binding.imageView.setImageResource(R.drawable.ic_round_people_alt_24)

        binding.root.setOnClickListener {
            openChat(item, true)
        }

        binding.root.setOnLongClickListener {
            true
        }

    }

    private fun openChat(item: ChatEntity, isUserChannel: Boolean) {
        if (!isUserChannel){
            val i = Intent(context, CommunityChatViewerActivity::class.java)
            i.putExtra("item_id", item.id)
            i.putExtra("item_title", item.name)
            i.putExtra("item_chat_type", item.chatTypes)
            i.putExtra("item_uid", item.chatWithUserId)
            i.putExtra("item_myUid", item.myUserId)
            i.putExtra("item_is_user_channel", isUserChannel)
            context.startActivity(i)
        }else {
            val i = Intent(context, ChatViewerActivity::class.java)
            i.putExtra("item_id", item.id)
            i.putExtra("item_title", item.name)
            i.putExtra("item_chat_type", item.chatTypes)
            i.putExtra("item_uid", item.chatWithUserId)
            i.putExtra("item_myUid", item.myUserId)
            i.putExtra("item_is_user_channel", isUserChannel)
            context.startActivity(i)
        }
    }

}
