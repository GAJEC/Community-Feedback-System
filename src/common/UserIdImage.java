package common;

import java.io.Serializable;

public class UserIdImage implements Serializable {
    int attr_id;
    int attr_user_id;
    String file_name;
    String base64_data;

    public UserIdImage(int attr_id, int attr_user_id, String file_name, String base64_data) {
        this.attr_id = attr_id;
        this.attr_user_id = attr_user_id;
        this.file_name = file_name;
        this.base64_data = base64_data;
    }

    public int getAttr_id() {
        return attr_id;
    }

    public void setAttr_id(int attr_id) {
        this.attr_id = attr_id;
    }

    public int getAttr_user_id() {
        return attr_user_id;
    }

    public void setAttr_user_id(int attr_user_id) {
        this.attr_user_id = attr_user_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getBase64_data() {
        return base64_data;
    }

    public void setBase64_data(String base64_data) {
        this.base64_data = base64_data;
    }
}
