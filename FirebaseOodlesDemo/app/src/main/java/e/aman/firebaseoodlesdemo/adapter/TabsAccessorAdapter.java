package e.aman.firebaseoodlesdemo.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import e.aman.firebaseoodlesdemo.chats.fragments.ChatsFragment;
import e.aman.firebaseoodlesdemo.chats.fragments.FriendsFragment;
import e.aman.firebaseoodlesdemo.chats.fragments.GroupsFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter
{


    public TabsAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0 :
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 1 :
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2 :
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)
        {
            case 0 :
                return "Chats";
            case 1 :
                return "Groups";
            case 2 :
                return "Friends";
            default:
                return null;
        }

    }
}
