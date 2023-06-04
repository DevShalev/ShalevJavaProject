package utill;

import android.app.Application;

public class TaskApi extends Application {

    private String username;
    private String userId;

    private static TaskApi instance;

    public static TaskApi getInstance(){
        if(instance == null)
            instance = new TaskApi();
        return instance;
    }

    public TaskApi(){

    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }




}
