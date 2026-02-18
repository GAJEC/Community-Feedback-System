package common;

import java.io.Serializable;
import java.util.Date;

public class Announcement implements Serializable {
    int attr_id;
    int posted_by;
    String posted_by_name;
    String title;
    String message;
    Date posted_at;

    public Announcement(int attr_id, int posted_by, String posted_by_name, String title, String message, Date posted_at) {
        this.attr_id = attr_id;
        this.posted_by = posted_by;
        this.posted_by_name = posted_by_name;
        this.title = title;
        this.message = message;
        this.posted_at = posted_at;
    }

    public int getAttr_id() {
        return attr_id;
    }

    public void setAttr_id(int attr_id) {
        this.attr_id = attr_id;
    }

    public int getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(int posted_by) {
        this.posted_by = posted_by;
    }

    public String getPosted_by_name() {
        return posted_by_name;
    }

    public void setPosted_by_name(String posted_by_name) {
        this.posted_by_name = posted_by_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getPosted_at() {
        return posted_at;
    }

    public void setPosted_at(Date posted_at) {
        this.posted_at = posted_at;
    }
}
