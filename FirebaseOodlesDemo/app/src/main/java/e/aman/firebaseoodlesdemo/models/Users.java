package e.aman.firebaseoodlesdemo.models;

public class Users
{
    public String name , phone , profileimage , age;

    public Users() {
    }

    public Users(String name, String phone, String profileimage , String age) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.profileimage = profileimage;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
