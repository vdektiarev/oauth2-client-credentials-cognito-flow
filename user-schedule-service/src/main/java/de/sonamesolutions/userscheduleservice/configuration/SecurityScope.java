package de.sonamesolutions.userscheduleservice.configuration;

public enum SecurityScope {

    SCHEDULE_READ("schedule.read"), SCHEDULE_UPDATE("schedule.update");

    private final String id;

    SecurityScope(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
