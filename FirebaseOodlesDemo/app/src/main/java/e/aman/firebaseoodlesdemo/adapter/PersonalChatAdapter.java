package e.aman.firebaseoodlesdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Actions;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class PersonalChatAdapter extends RecyclerView.Adapter<PersonalChatAdapter.MyViewHolder>
{

    private String currentUserId;
    private List<Users> personalChatListUsers;
    private Context context;

    public PersonalChatAdapter() {
    }

    public PersonalChatAdapter(String currentUserId, List<Users> personalChatListUsers, Context context) {
        this.currentUserId = currentUserId;
        this.personalChatListUsers = personalChatListUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_chat_display_layout , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.name.setText(personalChatListUsers.get(position).getName());
        Picasso.get().load(personalChatListUsers.get(position).getProfileimage()).placeholder(R.drawable.profile_icon).into(holder.circleImageView);





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               message(personalChatListUsers.get(position).getName());
            }
        });


    }

    @Override
    public int getItemCount() {
        return personalChatListUsers.size();
    }

    private void message(final String friendName)
    {
        DatabaseReference rRef  = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
        rRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if ((snapshot.child(Constants.DATABASE_NAME).getValue().toString()).equals(friendName))
                    {
                        String friendKey = snapshot.getKey();
                        Actions.sendUserToChatActivity(context , friendName , friendKey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name;
        private CircleImageView circleImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.personal_chat_name);
            circleImageView = itemView.findViewById(R.id.personal_chat_profile);

        }
    }
}
