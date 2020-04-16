package e.aman.firebaseoodlesdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class FriendRequestAdapter  extends RecyclerView.Adapter<FriendRequestAdapter.MyViewHolder>
{
    private List<Users> list ;
    private Context ctx;

    public FriendRequestAdapter(List<Users> list)
    {
        this.list = list;

    }

    @NonNull
    @Override
    public FriendRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_display , parent , false);
        return new FriendRequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MyViewHolder holder, int position) {
        Users users = list.get(position);
        holder.name.setText(users.getName());
        holder.age.setText(users.getAge());
        Picasso.get().load( users.getProfileimage()).placeholder(R.drawable.profile_icon).into(holder.profile);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_REQUEST_REF);
        private TextView name , age ;
        private CircleImageView profile;
        private Button acceptButton , declineButton;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            ctx = itemView.getContext();
            name = (TextView)itemView.findViewById(R.id.friend_request_users_name);
            age = (TextView)itemView.findViewById(R.id.friend_request_users_age);
            profile = (CircleImageView)itemView.findViewById(R.id.friend_request_profile_image);
            acceptButton = (Button)itemView.findViewById(R.id.friend_request_accept_button);
            declineButton = (Button)itemView.findViewById(R.id.friend_request_decline_button);


            //decline start

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phoneNumber = list.get(getAdapterPosition()).getPhone();
                    findUserId(phoneNumber);
                }

                private void findUserId(final String number)
                {
                    final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    if(number.equals(snapshot.child(Constants.DATABASE_PHONE_NUMBER).getValue()))
                                    {
                                        acceptRequest(currentUserId , snapshot.getKey());
                                    }
                                }
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                private void acceptRequest(final String currentUserId, final String senderId)
                {
                    friendsRef.child(currentUserId).child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                            {
                                dataSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }



            });

            //decline over





           acceptButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String phoneNumber = list.get(getAdapterPosition()).getPhone();
                   findUserId(phoneNumber);
               }

               private void findUserId(final String number)
               {
                   final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                   DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_ROOT_NODE);
                   reference.addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                       {
                           if (dataSnapshot.exists())
                           {
                               for(DataSnapshot snapshot : dataSnapshot.getChildren())
                               {
                                   if(number.equals(snapshot.child(Constants.DATABASE_PHONE_NUMBER).getValue()))
                                   {
                                       acceptRequest(currentUserId , snapshot.getKey());
                                   }
                               }
                           }
                       }


                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }


               private void acceptRequest(final String currentUserId, final String senderId)
               {
                   friendsRef.child(currentUserId).child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists())
                           {
                                dataSnapshot.getRef().removeValue();
                                addToFriend(currentUserId  , senderId);
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }



               private void addToFriend(String currentUserId, String senderId)
               {
                   DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_FRIEND_NODE);
                   HashMap friendsMap = new HashMap();
                   friendsMap.put(Constants.TIME , getTime());
                   friendsMap.put(Constants.DATE , getDate());

                   friendsRef.child(currentUserId).child(senderId).updateChildren(friendsMap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task)
                       {
                           Toast.makeText(ctx , "Added to friend list!" , Toast.LENGTH_SHORT).show();
                       }
                   });

               }


               private String getTime()
               {
                   SimpleDateFormat time_formatter = new SimpleDateFormat("HH:mm:ss");
                   String current_time_str = time_formatter.format(System.currentTimeMillis());
                   return current_time_str;
               }

               private String getDate()
               {
                   String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                   return currentDate;
               }

           });



        }
    }



}

