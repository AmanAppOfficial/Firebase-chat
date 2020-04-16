package e.aman.firebaseoodlesdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.models.Messages;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class PersonalMessagesAdapter extends RecyclerView.Adapter<PersonalMessagesAdapter.MyViewHolder>
{
    private List<Messages> messagesList;
    private Context context;
    private String currentUserID;

    private DatabaseReference usersRef;

    public PersonalMessagesAdapter() {
    }

    public PersonalMessagesAdapter(List<Messages> messagesList, Context context, String currentUserID) {
        this.messagesList = messagesList;
        this.context = context;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_messages_layout , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        Messages message = messagesList.get(position);
        String fromUserId = message.getFrom();
        String messageType = message.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE).child(fromUserId);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Constants.DATABASE_PROFILE_IMAGE))
                {
                    String image = dataSnapshot.child(Constants.DATABASE_PROFILE_IMAGE).getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (messageType.equals(Constants.TEXT))
        {

            if (fromUserId.equals(currentUserID))
            {
                holder.leftLayout.setVisibility(View.GONE);
                holder.senderText.setText(message.getMessage());
            }
            else
            {
                holder.rightLayout.setVisibility(View.GONE);
                holder.receiverText.setText(message.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView circleImageView;
        private TextView senderText , receiverText;
        private LinearLayout leftLayout , rightLayout;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.personal_friend_profile);
            senderText = itemView.findViewById(R.id.personal_sender_message_textview);
            receiverText = itemView.findViewById(R.id.personal_receiver_message_textview);
            leftLayout = itemView.findViewById(R.id.personal_left_view);
            rightLayout = itemView.findViewById(R.id.personal_right_view);
        }
    }
}
