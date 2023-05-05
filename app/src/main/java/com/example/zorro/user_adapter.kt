package com.example.zorro

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class user_adapter(val context: Context, val userList: ArrayList<user>) : RecyclerView.Adapter<user_adapter.userViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)

        return userViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val current_user = userList[position]
        Picasso.get().load(current_user.imageURL).into(holder.imageView)
       //Picasso.get().load(current_user.imageURL).into()


        holder.txt_name.text = current_user.name

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("name",current_user.name)
            intent.putExtra("uid",current_user.UID)
            context.startActivity(intent)
        }
    }


    class  userViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val txt_name = itemView.findViewById<TextView>(R.id.text_name)
        val imageView = itemView.findViewById<ImageView>(R.id.item_view_dp)
    }
}