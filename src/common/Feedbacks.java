package common;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Feedbacks implements Serializable {
    int attr_id;
    int issued_by;
    int attr_approvedby;
    String[] attr_status = {"Resolved", "In-Progress", "Pending"};
    String[] attr_upvote;
    String[] attr_downvote;
    String[] categories = {"Event Suggestion", "Missing Person", "Noise Complaint", "Infrastructure", "Other"};
    String title;
    String location;
    String content;
    Date issued_date;

    public Feedbacks(int attr_id, int issued_by, int attr_approvedby, String[] attr_status, String[] attr_upvote, String[] attr_downvote, String[] categories, String title, String location, String content, Date issued_date) {
        this.attr_id = attr_id;
        this.issued_by = issued_by;
        this.attr_approvedby = attr_approvedby;
        this.attr_status = attr_status;
        this.attr_upvote = attr_upvote;
        this.attr_downvote = attr_downvote;
        this.categories = categories;
        this.title = title;
        this.location = location;
        this.content = content;
        this.issued_date = issued_date;
    }

    public Feedbacks(int attr_id, int issued_by, int attr_approvedby, String[] attr_status, String[] categories, String title, String location, String content, Date issued_date) {
        this.attr_id = attr_id;
        this.issued_by = issued_by;
        this.attr_approvedby = attr_approvedby;
        this.attr_status = attr_status;
        this.attr_upvote = null;
        this.attr_downvote = null;
        this.categories = categories;
        this.title = title;
        this.location = location;
        this.content = content;
        this.issued_date = issued_date;
    }

    public int getAttr_id() {
        return attr_id;
    }

    public int getIssued_by() {
        return issued_by;
    }

    public int getAttr_approvedby() {
        return attr_approvedby;
    }

    public String[] getAttr_status() {
        return attr_status;
    }

    public String[] getAttr_upvote() {
        return attr_upvote;
    }

    public String[] getAttr_downvote() {
        return attr_downvote;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getContent() {
        return content;
    }

    public Date getIssued_date() {
        return issued_date;
    }

    public void setAttr_id(int attr_id) {
        this.attr_id = attr_id;
    }

    public void setIssued_by(int issued_by) {
        this.issued_by = issued_by;
    }

    public void setAttr_approvedby(int attr_approvedby) {
        this.attr_approvedby = attr_approvedby;
    }

    public void setAttr_status(String[] attr_status) {
        this.attr_status = attr_status;
    }

    public void setAttr_upvote(String[] attr_upvote) {
        this.attr_upvote = attr_upvote;
    }

    public void setAttr_downvote(String[] attr_downvote) {
        this.attr_downvote = attr_downvote;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIssued_date(Date issued_date) {
        this.issued_date = issued_date;
    }

    @Override
    public String toString() {
        return "Feedbacks{" +
                "attr_id=" + attr_id +
                ", issued_by=" + issued_by +
                ", attr_approvedby=" + attr_approvedby +
                ", attr_status=" + Arrays.toString(attr_status) +
                ", attr_upvote=" + Arrays.toString(attr_upvote) +
                ", attr_downvote=" + Arrays.toString(attr_downvote) +
                ", categories=" + Arrays.toString(categories) +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", content='" + content + '\'' +
                ", issued_date=" + issued_date +
                '}';
    }
}
