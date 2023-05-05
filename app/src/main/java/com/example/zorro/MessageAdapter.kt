package com.example.zorro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    val ITEM_RECIEVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.recieve,parent,false)

            return ReceiveViewHolder(view)
        }
        else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)

            return SentViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass== SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder

            holder.sentMesaage.text = currentMessage.message
        }
        else{
            val viewHolder =  holder as ReceiveViewHolder
            holder.recieveMesaage.text = currentMessage.message
        }

    }

    override fun getItemViewType(position: Int): Int {
        var currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }
        else{
            return ITEM_RECIEVE
        }
    }

    class SentViewHolder(itemView: View ) : RecyclerView.ViewHolder(itemView){
        val sentMesaage =  itemView.findViewById<TextView>(R.id.sentmessage)
    }
    class ReceiveViewHolder(itemView: View ) : RecyclerView.ViewHolder(itemView){
        val recieveMesaage =  itemView.findViewById<TextView>(R.id.recievemessage)

    }

}