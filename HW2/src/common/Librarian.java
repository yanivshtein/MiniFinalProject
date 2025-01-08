package common;

import java.io.Serializable;

public class Librarian implements Serializable{

	private String librarian_id = "";
	private String librarian_name = "";
	private String librarian_email = "";
	private String librarian_password = "";
	
	
	public Librarian(String id, String name, String email, String password) {
		librarian_id = id;
		librarian_name = name;
		librarian_email = email;
		librarian_password = password;
	}
	
	public String getLibrarian_id() {
        return librarian_id;
    }

    public void setlibrarian_id(String librarian_id) {
        this.librarian_id = librarian_id;
    }
    
    public String getLibrarian_name() {
        return librarian_name;
    }

    public void setlibrarian_name(String librarian_name) {
        this.librarian_name = librarian_name;
    }
    
    public String getLibrarian_email() {
        return librarian_email;
    }

    public void setlibrarian_email(String librarian_email) {
        this.librarian_email = librarian_email;
    }
    
    public String getLibrarian_password() {
        return librarian_password;
    }

    public void setlibrarian_password(String librarian_password) {
        this.librarian_password = librarian_password;
    }
}
