package common;
import java.util.Date;

public class Feedbacks {
    int attr_id;
    int issued_by;
    int attr_approvedby;
    String[] attr_status = {"Resolved", "In-Progress", "Pending"};
    String location;
    String content;
    Date date;

    public Feedbacks(int attr_id, int issued_by, int attr_approvedby, String[] attr_status, String location, String content, Date date) {
        this.attr_id = attr_id;
        this.attr_status = attr_status;
        this.attr_approvedby = attr_approvedby;
        this.issued_by = issued_by;
        this.location = location;
        this.content = content;
        this.date = date;
    }
}
