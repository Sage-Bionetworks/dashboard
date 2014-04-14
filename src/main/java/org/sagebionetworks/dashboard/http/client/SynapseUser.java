package org.sagebionetworks.dashboard.http.client;

public class SynapseUser {

    public SynapseUser(String userId, String userName, String email,
            String firstName, String lastName) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserId() {
        return userId;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "SynapseUser [userId=" + userId + ", userName=" + userName
                + ", email=" + email + ", firstName=" + firstName
                + ", lastName=" + lastName + "]";
    }

    private final String userId;
    private final String userName;
    private final String email;
    private final String firstName;
    private final String lastName;
}
