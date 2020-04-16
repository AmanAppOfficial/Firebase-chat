package e.aman.firebaseoodlesdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import e.aman.firebaseoodlesdemo.R;
import e.aman.firebaseoodlesdemo.models.Users;
import e.aman.firebaseoodlesdemo.profile.UserProfileActivity;
import e.aman.firebaseoodlesdemo.utils.Constants;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> implements Filterable
{

    private List<Users> list ;
    private List<Users> fullList;
    private Context ctx;


    public UserAdapter(List<Users> list)
    {
        this.list = list;
        fullList = new ArrayList<>(list);
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_details_view , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Users users = list.get(position);
        holder.name.setText(users.getName());
        holder.phone.setText(users.getPhone());
        Picasso.get().load( users.getProfileimage()).placeholder(R.drawable.profile_icon).into(holder.profile);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String names = list.get(position).getName();
                Intent i = new Intent(ctx , UserProfileActivity.class);
                i.putExtra(Constants.INTENT_NAME, names);
                ctx.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Users> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Users item : fullList) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getPhone().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView name , phone ;
        private CircleImageView profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ctx = itemView.getContext();
            name = (TextView)itemView.findViewById(R.id.all_users_name);
            phone = (TextView)itemView.findViewById(R.id.all_users_number);
            profile = (CircleImageView)itemView.findViewById(R.id.all_users_profile_image);



        }
    }



}
