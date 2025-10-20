package voluntrack.model;

public class Registration {
    private final String id;
    private final int userId;
    private final int projectId;
    private final String title;
    private final String location;
    private final String day;
    private final int hourlyValue;
    private final int slots;
    private final int hoursPerSlot;
    private final int totalValue;
    private final String confirmedAt;

    public Registration(String id, int userId, int projectId, String title, String location, String day,
                        int hourlyValue, int slots, int hoursPerSlot, int totalValue, String confirmedAt) {
        this.id = id;
        this.userId = userId;
        this.projectId = projectId;
        this.title = title;
        this.location = location;
        this.day = day;
        this.hourlyValue = hourlyValue;
        this.slots = slots;
        this.hoursPerSlot = hoursPerSlot;
        this.totalValue = totalValue;
        this.confirmedAt = confirmedAt;
    }

    public String getId() { return id; }
    public int getUserId() { return userId; }
    public int getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDay() { return day; }
    public int getHourlyValue() { return hourlyValue; }
    public int getSlots() { return slots; }
    public int getHoursPerSlot() { return hoursPerSlot; }
    public int getTotalValue() { return totalValue; }
    public String getConfirmedAt() { return confirmedAt; }
}
