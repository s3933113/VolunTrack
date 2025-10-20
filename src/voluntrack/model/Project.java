package voluntrack.model;

public class Project {
    private final int id;
    private final String title;
    private final String location;
    private final String day;              // Mon..Sun
    private final int hourlyValue;         // AUD per hour
    private final int totalSlots;          // total available
    private final int registeredSlots;     // already taken
    private final boolean enabled;         // visible to users
    private final String createdAt;        // ISO string

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
        this.createdAt = createdAt;
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

    // Derived helpers
    public int getAvailableSlots() { return Math.max(0, totalSlots - registeredSlots); }

    // Backward compatibility for old UI that expected description
    public String getDescription() {
        return String.format("%s • %s • $%d/h • %d/%d slots",
                location, day, hourlyValue, getAvailableSlots(), totalSlots);
    }
}
