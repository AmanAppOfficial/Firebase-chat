package e.aman.firebaseoodlesdemo.utils;

import android.content.Context;
import android.content.Intent;

import e.aman.firebaseoodlesdemo.admin.AdminMainActivity;
import e.aman.firebaseoodlesdemo.auth.ForgotPasswordActivity;
import e.aman.firebaseoodlesdemo.auth.LoginActivity;
import e.aman.firebaseoodlesdemo.chats.PersonalChatActivity;
import e.aman.firebaseoodlesdemo.profile.PeopleSearchActivity;
import e.aman.firebaseoodlesdemo.profile.SettingsActivity;
import e.aman.firebaseoodlesdemo.users.FriendRequestActivity;
import e.aman.firebaseoodlesdemo.users.MainActivity;
import e.aman.firebaseoodlesdemo.auth.SignupActivity;
import e.aman.firebaseoodlesdemo.profile.UserRegisterActivity;

public class Actions
{

    public static void sendToSignupActivity(Context context)
    {
        Intent signupIntent = new Intent(context , SignupActivity.class);
        context.startActivity(signupIntent);
    }

    public static void sendToMainActivity(Context context)
    {
        Intent mainIntent = new Intent(context , MainActivity.class);
        context.startActivity(mainIntent);
    }

    public static void sendToAdminMainActivity(Context context)
    {
        Intent adminIntent = new Intent(context , AdminMainActivity.class);
        context.startActivity(adminIntent);
    }

    public static void forgotPassword(Context context)
    {
        Intent forgotPasswordIntent = new Intent(context , ForgotPasswordActivity.class);
        context.startActivity(forgotPasswordIntent);
    }

    public static void sendToRegisterActivity(Context context)
    {
        Intent registerIntent = new Intent(context , UserRegisterActivity.class);
        context.startActivity(registerIntent);
    }

    public static void sendToSearchUserActivity(Context context)
    {
        Intent searchIntent = new Intent(context , PeopleSearchActivity.class);
        context.startActivity(searchIntent);
    }

    public static void sendUserToFriendRequestActivity(Context context)
    {
        Intent friendRequestIntent = new Intent(context , FriendRequestActivity.class);
        context.startActivity(friendRequestIntent);
    }
    public static void sendToLoginActivity(Context context)
    {
        Intent loginIntent = new Intent(context , LoginActivity.class);
        context.startActivity(loginIntent);
    }

    public static void sendToSettingsActivity(Context context)
    {
        Intent SettingIntent = new Intent(context , SettingsActivity.class);
        context.startActivity(SettingIntent);
    }

    public static void sendUserToChatActivity(Context context , String friendName , String friendkey)
    {
        Intent chatIntent = new Intent(context , PersonalChatActivity.class);
        chatIntent.putExtra(Constants.FRIEND_NAME , friendName);
        chatIntent.putExtra(Constants.FRIEND_KEY , friendkey);
        context.startActivity(chatIntent);
    }


}
