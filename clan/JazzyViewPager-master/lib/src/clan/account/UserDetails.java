package clan.account;

public class UserDetails {

	private String fName;
	private String lName;
	private String emailId;

	private String sessionId;
	
	public void UserDetails(String fName,String lName,String emailId, String sessionId){
		this.fName = fName;
		this.lName = lName;
		this.emailId = emailId;
		this.sessionId = sessionId;
	}

	public String getFirstName() {
		return fName;
	}

	public String getLastName() {
		return lName;
	}

	public String getFullName() {
		return fName + " " + lName;
	}

	public String getEmailAddress() {
		return emailId;
	}

	public String getSessionId() {
		return sessionId;
	}
	
	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}

}
