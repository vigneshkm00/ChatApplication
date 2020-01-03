package com.vignesh.chatapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vignesh.chatapplication.ChatActivity;
import com.vignesh.chatapplication.R;
import com.vignesh.chatapplication.models.Modeluser;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<Modeluser> userlist;

    public AdapterUsers(Context context, List<Modeluser> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisuid = userlist.get(position).getUid();
        String username = userlist.get(position).getName();
        final String useremail = userlist.get(position).getEmail();
        String userimage = userlist.get(position).getImage();


        holder.mname.setText(username);
        holder.memail.setText(useremail);
        try {
            Picasso.get().load(userimage).placeholder(R.drawable.ic_photo_black).into(holder.mavatar);
        }
        catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,""+useremail,Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, ChatActivity.class);
                i.putExtra("hisUid",hisuid);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mavatar;
        TextView mname,memail;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mavatar = itemView.findViewById(R.id.imageViewrow);
            mname = itemView.findViewById(R.id.nametv);
            memail = itemView.findViewById(R.id.emailtv);

        }
    }
}
