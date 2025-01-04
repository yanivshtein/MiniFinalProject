package common;
import java.io.Serializable;

public class Subscriber1 implements Serializable{
    private int subscriber_id;
    private String subscriber_name;
    private String subscriber_phone_number;
    private String subscriber_email;
    private String sub_status; // New field
    private String Password;

    private static int i = 0;

    public Subscriber1() {
        this.subscriber_id = i++;
        this.subscriber_name = "Doroty";
        this.subscriber_phone_number = "1234";
        this.subscriber_email = "doroty.dorin@gmail.com";
        this.sub_status = "Active"; // Default value for sub_status
        this.Password = "1234";
    }
    
    public Subscriber1(int sub_id, String name, String phone, String email, String status , String Password) {
        subscriber_id = sub_id;
        subscriber_name = name;
        subscriber_phone_number = phone;
        subscriber_email = email;
        sub_status = status; // Initialize sub_status
        this.Password = Password;
    }

    // Getters and setters
    public int getSubscriber_id() {
        return subscriber_id;
    }

    public void setSubscriber_id(int subscriber_id) {
        this.subscriber_id = subscriber_id;
    }

    public String getSubscriber_name() {
        return subscriber_name;
    }

    public void setSubscriber_name(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    public String getSubscriber_phone_number() {
        return subscriber_phone_number;
    }

    public void setSubscriber_phone_number(String subscriber_phone_number) {
        this.subscriber_phone_number = subscriber_phone_number;
    }

    public String getSubscriber_email() {
        return subscriber_email;
    }

    public void setSubscriber_email(String subscriber_email) {
        this.subscriber_email = subscriber_email;
    }

    public String getSub_status() {
        return sub_status;
    }

    public void setSub_status(String sub_status) {
        this.sub_status = sub_status;
    }

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}
}
