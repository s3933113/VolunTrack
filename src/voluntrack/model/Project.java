package voluntrack.model;

import voluntrack.util.TimeUtil;

public class Project {
    private final int id;
    private final String title;
    private final String location;
    private final String day;
    private final int hourlyValue;
    private final int totalSlots;
    private final int registeredSlots;
    private final boolean enabled;
    private final String createdAt;

    // Full 9 parameters (service/repo side)
    public Project(int id,
                   String title,
                   String location,
                   String day,
                   int hourlyValue,
                   int totalSlots,
                   int registeredSlots,
                   boolean enabled,
                   String createdAt) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.day = day;
        this.hourlyValue = hourlyValue;
        this.totalSlots = totalSlots;
        this.registeredSlots = registeredSlots;
        this.enabled = enabled;
        this.createdAt = createdAt != null ? createdAt : TimeUtil.nowIso();
    }


    public Project(String title,
                   String location,
                   String day,
                   int hourlyValue,
                   int totalSlots) {
        this(0, title, location, day, hourlyValue, totalSlots, 0, true, TimeUtil.nowIso());
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDay() { return day; }
    public int getHourlyValue() { return hourlyValue; }
    public int getTotalSlots() { return totalSlots; }
    public int getRegisteredSlots() { return registeredSlots; }
    public boolean isEnabled() { return enabled; }
    public String getCreatedAt() { return createdAt; }
}
