package de.sonamesolutions.articlesservice.model;

public class Article {

    private String body;
    private String scheduleId;
    private String userId;

    public Article() {
    }

    public Article(String body, String scheduleId, String userId) {
        this.body = body;
        this.scheduleId = scheduleId;
        this.userId = userId;
    }

    public String getBody() {
        return body;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
