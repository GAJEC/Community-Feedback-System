package common;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    int attr_id;

    // Types are (0 - admin, 1 - Officials, 2 - Residents)
    int attr_type;
    String name;
    String address;
    String[] contact;

    String username;
    String password;

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, String name, String address) {
        this.attr_id = id;
        this.name = name;
        this.address = address;
    }

    public User(int id, String username, String password, String name, String address, String[] contact) {
        this.name = name;
        this.attr_id = id;
        this.attr_type = 2;
        this.address = address;
        this.contact = contact;
    }

    public User(int id, int type, String username, String password, String name, String address, String[] contact) {
        this.attr_id = id;
        this.attr_type = type;
        this.name = name;
        this.address = address;
        this.contact = contact;
    }

    public int getAttr_id() {
        return attr_id;
    }

    public void setAttr_id(int attr_id) {
        this.attr_id = attr_id;
    }

    public int getAttr_type() {
        return attr_type;
    }

    public void setAttr_type(int attr_type) {
        this.attr_type = attr_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String[] getContact() {
        return contact;
    }

    public void setContact(String[] contact) {
        this.contact = contact;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Users{" +
                "attr_id=" + attr_id +
                ", attr_type=" + attr_type +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", contact=" + Arrays.toString(contact) +
                '}';
    }
}
