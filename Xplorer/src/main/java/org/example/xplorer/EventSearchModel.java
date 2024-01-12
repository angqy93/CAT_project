package org.example.xplorer;

public class EventSearchModel {
    Integer event_id;
    String event_name, event_desc, event_location, event_time;

    public EventSearchModel(String event_name, String event_desc, String event_location, String event_time) {
        this.event_name = event_name;
        this.event_desc = event_desc;
        this.event_location = event_location;
        this.event_time = event_time;
    }

    public Integer getEvent_id() {
        return event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_desc() {
        return event_desc;
    }

    public String getEvent_location() {
        return event_location;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setEvent_desc(String event_desc) {
        this.event_desc = event_desc;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }
}
