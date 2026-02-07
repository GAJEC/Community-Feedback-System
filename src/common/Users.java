package common;

import java.util.Arrays;

public class Users {
    int attr_id;

    // Types are (0 - admin, 1 - Officials, 2 - Residents)
    int attr_type;
    String name;
    String address;
    String[] contact;

    public Users(int id, String name, String address) {
        this.attr_id = id;
        this.name = name;
        this.address = address;
    }

    public Users(String name, int id, String address, String[] contact) {
        this.name = name;
        this.attr_id = id;
        this.attr_type = 2;
        this.address = address;
        this.contact = contact;
    }

    public Users(int id, int type, String name, String address, String[] contact) {
        this.attr_id = id;
        this.attr_type = type;
        this.name = name;
        this.address = address;
        this.contact = contact;
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
