package e.aman.firebaseoodlesdemo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.CompletionService;

import de.hdodenhof.circleimageview.CircleImageView;
import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.chats.PersonalChatActivity;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.profile.UserProfileActivity;
import e.aman.firebaseoodlesdemo.utils.Actions;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder>
{

    private Context context;
    private String currentUserId;
    private List<Users> friendsList;
    private DatabaseReference reference;
    private  String friendKey;

    public FriendsAdapter(Context context, String currentUserId, List<Users> friendsList) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.friendsList = friendsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_layout , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.friendName.setText(friendsList.get(position).getName());
        holder.friendPhone.setText(friendsList.get(position).getPhone());
        Picasso.get().load(friendsList.get(position).getProfileimage()).placeholder(R.drawable.profile_icon).into(holder.friendProfile);

        holder.optionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(position);
            }
        });

    }

    private void showOptionDialog(final int pos)
    {
        String[] options = {"View Profile", "Unfriend", "Message"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]

                if (which == 0)
                {
                    String names = friendsList.get(pos).getName();
                    Intent i = new Intent(context , UserProfileActivity.class);
                    i.putExtra(Constants.INTENT_NAME, names);
                    context.startActivity(i);
                }

                else if (which == 1)
                {
                    unfriend(pos);
                }

                else if (which == 2)
                {
                    message(friendsList.get(pos).getName());
                }

            }
        });
        builder.show();
    }

    private void unfriend(int pos)
    {

        final String name = friendsList.get(pos).getName();
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_NODE);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    if ((snapshot.child(Constants.DATABASE_NAME).getValue().toString()).equals(name))
                    {
                       friendKey = snapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUserId).exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.child(currentUserId).getChildren())
                    {
                        if (snapshot.getKey().equals(friendKey))
                        {
                            snapshot.getRef().removeValue();
                        }
                    }
                }
                if (dataSnapshot.child(friendKey).exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.child(friendKey).getChildren())
                    {
                        if (snapshot.getKey().equals(currentUserId))
                        {
                            snapshot.getRef().removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
                    friendKey = snapshot.getKey();
                    Actions.sendUserToChatActivity(context , friendName , friendKey);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });



    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView friendName , friendPhone;
        private ImageView optionIcon;
        private CircleImageView friendProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            friendName = itemView.findViewById(R.id.friend_users_name);
            friendPhone = itemView.findViewById(R.id.friend_users_number);
            friendProfile = itemView.findViewById(R.id.friend_users_profile_image);
            optionIcon = itemView.findViewById(R.id.friend_option_icon);


        }
    }
}
