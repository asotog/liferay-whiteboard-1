package com.rivetlogic.whiteboard.realtime;

public class UserData {
	private String userName;
    private String userImagePath;

    public UserData(String userName, String userImagePath) {

        super();
        this.userName = userName;
        this.userImagePath = userImagePath;
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserImagePath() {
		return userImagePath;
	}

	public void setUserImagePath(String userImagePath) {
		this.userImagePath = userImagePath;
	}
    
}
