package common;

import java.io.Serializable;

/**
 * Represents a librarian with personal details such as ID, name, email, and password.
 * Implements Serializable to allow for object serialization.
 */
public class Librarian implements Serializable {

    private String librarian_id = "";
    private String librarian_name = "";
    private String librarian_email = "";
    private String librarian_password = "";

    /**
     * Constructs a Librarian object with the specified details.
     * 
     * @param id The librarian's ID.
     * @param name The librarian's name.
     * @param email The librarian's email address.
     * @param password The librarian's password.
     */
    public Librarian(String id, String name, String email, String password) {
        librarian_id = id;
        librarian_name = name;
        librarian_email = email;
        librarian_password = password;
    }

    /**
     * Gets the librarian's ID.
     * 
     * @return The librarian ID.
     */
    public String getLibrarian_id() {
        return librarian_id;
    }

    /**
     * Sets the librarian's ID.
     * 
     * @param librarian_id The new librarian ID.
     */
    public void setlibrarian_id(String librarian_id) {
        this.librarian_id = librarian_id;
    }

    /**
     * Gets the librarian's name.
     * 
     * @return The librarian name.
     */
    public String getLibrarian_name() {
        return librarian_name;
    }

    /**
     * Sets the librarian's name.
     * 
     * @param librarian_name The new librarian name.
     */
    public void setlibrarian_name(String librarian_name) {
        this.librarian_name = librarian_name;
    }

    /**
     * Gets the librarian's email address.
     * 
     * @return The librarian email address.
     */
    public String getLibrarian_email() {
        return librarian_email;
    }

    /**
     * Sets the librarian's email address.
     * 
     * @param librarian_email The new librarian email address.
     */
    public void setlibrarian_email(String librarian_email) {
        this.librarian_email = librarian_email;
    }

    /**
     * Gets the librarian's password.
     * 
     * @return The librarian password.
     */
    public String getLibrarian_password() {
        return librarian_password;
    }

    /**
     * Sets the librarian's password.
     * 
     * @param librarian_password The new librarian password.
     */
    public void setlibrarian_password(String librarian_password) {
        this.librarian_password = librarian_password;
    }
}