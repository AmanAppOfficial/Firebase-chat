package e.aman.firebaseoodlesdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.models.Chats;
import e.aman.firebaseoodlesdemo.utils.Actions;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>
{

    private List<Chats> chatList;
    private Context context;
    private String currentUsername;

    public ChatAdapter(List<Chats> chatList, Context context , String currentUsername) {
        this.chatList = chatList;
        this.context = context;
        this.currentUsername = currentUsername;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_layout , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        final Chats chats = chatList.get(position);
        if (chats.getName().equals(currentUsername))
        {
            holder.left_view.setVisibility(View.GONE);
            holder.sender_message_textview.setText(chats.getMessage());
            holder.sender_time_textview.setText(chats.getTime());
        }
        else
        {
            holder.right_view.setVisibility(View.GONE);
            holder.receiver_message_textview.setText(chats.getMessage());
            holder.receiver_time_textview.setText(chats.getTime());
            holder.receiver_name_textview.setText(chats.getName());
        }




    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView receiver_message_textview , receiver_time_textview , receiver_name_textview;
        private TextView sender_message_textview , sender_time_textview;
        private LinearLayout left_view , right_view;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            left_view = itemView.findViewById(R.id.left_view);
            right_view = itemView.findViewById(R.id.right_view);

            receiver_message_textview = itemView.findViewById(R.id.receiver_message_textview);
            receiver_time_textview = itemView.findViewById(R.id.receiver_time_textview);
            receiver_name_textview = itemView.findViewById(R.id.receiver_name_textview);

            sender_message_textview = itemView.findViewById(R.id.sender_message_textview);
            sender_time_textview = itemView.findViewById(R.id.sender_time_textview);

        }
    }
}
