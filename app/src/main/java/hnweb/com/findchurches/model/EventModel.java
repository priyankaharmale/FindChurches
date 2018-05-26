package hnweb.com.findchurches.model;

/**
 * Created by PC-21 on 10-May-18.
 */

public class EventModel {

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_img() {
        return event_img;
    }

    public void setEvent_img(String event_img) {
        this.event_img = event_img;
    }

    public String getEvent_desc() {
        return event_desc;
    }

    public void setEvent_desc(String event_desc) {
        this.event_desc = event_desc;
    }

    public String getCreated_dt() {
        return created_dt;
    }

    public void setCreated_dt(String created_dt) {
        this.created_dt = created_dt;
    }

    public String getUpdated_dt() {
        return updated_dt;
    }

    public void setUpdated_dt(String updated_dt) {
        this.updated_dt = updated_dt;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCimg() {
        return cimg;
    }

    public void setCimg(String cimg) {
        this.cimg = cimg;
    }

    String eid ,event_dt, cid , event_name, event_time, event_img, event_desc, created_dt, updated_dt, deleted, cname, cimg;
    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getEvent_dt() {
        return event_dt;
    }

    public void setEvent_dt(String event_dt) {
        this.event_dt = event_dt;
    }
}
