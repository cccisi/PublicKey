package iie.dcs.test;


import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */

public class Contact implements Parcelable {
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        //Parcelable.Creator<Contact>()新建一个集合
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    private String name;
    private String pubkey;
    private String id;

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.pubkey = in.readString();
        this.id = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    //get类方法在DBHelper和List中都调用
    public String getPubKey() {
        return pubkey;
    }
    //set类方法只在DBHelper中调用
    public void setPubKey(String pubkey) {
        this.pubkey = pubkey;
    }

    //重写describeContents
    @Override
    public int describeContents() {
        return 0;
    }
    //重写writeToParcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.pubkey);
        dest.writeString(this.id);
    }
}
