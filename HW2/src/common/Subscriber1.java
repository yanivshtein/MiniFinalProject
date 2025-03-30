package common;

import java.io.Serializable;

/**
 * Represents a subscriber with personal details such as name, phone number, email, subscription status, and password.
 * Implements Serializable to allow for object serialization.
 */
public class Subscriber1 implements Serializable {
    
    private int subscriber_id;
    private String subscriber_name;
    private String subscriber_phone_number;
    private String subscriber_email;
    private String sub_status; // Subscription status (e.g., Active, Inactive)
    private String Password;

    private static int i = 0;

    /**
     * Default constructor that initializes a subscriber with default values.
     * The subscriber ID is automatically assigned a unique value using a static counter.
     */
    public Subscriber1() {
        this.subscriber_id = i++;
        this.subscriber_name = "Doroty";
        this.subscriber_phone_number = "1234";
        this.subscriber_email = "doroty.dorin@gmail.com";
        this.sub_status = "Active"; // Default value for subscription status
        this.Password = "1234";
    }

    /**
     * Constructor to initialize a subscriber with the provided details.
     * 
     * @param sub_id The subscriber ID.
     * @param name The subscriber's name.
     * @param phone The subscriber's phone number.
     * @param email The subscriber's email address.
     * @param status The subscription status (e.g., Active, Inactive).
     * @param Password The subscriber's password.
     */
    public Subscriber1(int sub_id, String name, String phone, String email, String status, String Password) {
        subscriber_id = sub_id;
        subscriber_name = name;
        subscriber_phone_number = phone;
        subscriber_email = email;
        sub_status = status; // Initialize subscription status
        this.Password = Password;
    }

    // Getters and setters for the fields

    /**
     * Gets the subscriber's ID.
     * 
     * @return The subscriber ID.
     */
    public int getSubscriber_id() {
        return subscriber_id;
    }

    /**
     * Sets the subscriber's ID.
     * 
     * @param subscriber_id The new subscriber ID.
     */
    public void setSubscriber_id(int subscriber_id) {
        this.subscriber_id = subscriber_id;
    }

    /**
     * Gets the subscriber's name.
     * 
     * @return The subscriber name.
     */
    public String getSubscriber_name() {
        return subscriber_name;
    }

    /**
     * Sets the subscriber's name.
     * 
     * @param subscriber_name The new subscriber name.
     */
    public void setSubscriber_name(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    /**
     * Gets the subscriber's phone number.
     * 
     * @return The subscriber's phone number.
     */
    public String getSubscriber_phone_number() {
        return subscriber_phone_number;
    }

    /**
     * Sets the subscriber's phone number.
     * 
     * @param subscriber_phone_number The new phone number.
     */
    public void setSubscriber_phone_number(String subscriber_phone_number) {
        this.subscriber_phone_number = subscriber_phone_number;
    }

    /**
     * Gets the subscriber's email address.
     * 
     * @return The subscriber's email address.
     */
    public String getSubscriber_email() {
        return subscriber_email;
    }

    /**
     * Sets the subscriber's email address.
     * 
     * @param subscriber_email The new email address.
     */
    public void setSubscriber_email(String subscriber_email) {
        this.subscriber_email = subscriber_email;
    }

    /**
     * Gets the subscriber's subscription status.
     * 
     * @return The subscription status (e.g., Active, Inactive).
     */
    public String getSub_status() {
        return sub_status;
    }

    /**
     * Sets the subscriber's subscription status.
     * 
     * @param sub_status The new subscription status.
     */
    public void setSub_status(String sub_status) {
        this.sub_status = sub_status;
    }

    /**
     * Gets the subscriber's password.
     * 
     * @return The subscriber's password.
     */
    public String getPassword() {
        return Password;
    }

    /**
     * Sets the subscriber's password.
     * 
     * @param password The new password.
     */
    public void setPassword(String password) {
        Password = password;
    }
}