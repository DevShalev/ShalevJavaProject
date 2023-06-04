package model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Task implements Parcelable {
    private String title , task_details , imageUrl , userId , userName ;
    private Timestamp timeAdded;


    public Task(){

    }

    /*public Task(String title, String task_details, String imageUrl, String userId, String userName, Timestamp timeAdded) {
        this.title = title;
        this.task_details = task_details;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.timeAdded = timeAdded;
    }*/

    protected Task(Parcel in) {
        title = in.readString();
        task_details = in.readString();
        imageUrl = in.readString();
        userId = in.readString();
        userName = in.readString();
        timeAdded = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask_details() {
        return task_details;
    }

    public void setTask_details(String task_details) {
        this.task_details = task_details;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(task_details);
        parcel.writeString(imageUrl);
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeParcelable(timeAdded, i);
    }
}