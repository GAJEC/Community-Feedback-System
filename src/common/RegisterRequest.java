package common;

import java.io.Serializable;

public class RegisterRequest implements Serializable {
    private final String fullName;
    private final String location;
    private final String phone;
    private final String username;
    private final String password;
    private final String idFileName;
    private final String idFileBase64;

    public RegisterRequest(String fullName, String location, String phone, String username, String password, String idFileName, String idFileBase64) {
        this.fullName = fullName;
        this.location = location;
        this.phone = phone;
        this.username = username;
        this.password = password;
        this.idFileName = idFileName;
        this.idFileBase64 = idFileBase64;
    }

    public String getFullName() {
        return fullName;
    }

    public String getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIdFileName() {
        return idFileName;
    }

    public String getIdFileBase64() {
        return idFileBase64;
    }
}
