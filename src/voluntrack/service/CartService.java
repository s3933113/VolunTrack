package voluntrack.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

/**
 * In-memory cart per session. Not persisted.
 * Rules
 *  - hours per slot: 1..3
 *  - max slots per project per user: 3
 */
public class CartService {

    public static final int MIN_HOURS = 1;
    public static final int MAX_HOURS = 3;
    public static final int MAX_SLOTS_PER_PROJECT = 3;

    public static final class CartItem {
        public final int projectId;
        public final String title;
        public final int hourlyValue; // AUD per hour
        private int slots;            // 1..3
        private int hoursPerSlot;     // 1..3

        public CartItem(int projectId, String title, int hourlyValue, int slots, int hoursPerSlot) {
            this.projectId = projectId;
            this.title = title;
            this.hourlyValue = hourlyValue;
            this.slots = slots;
            this.hoursPerSlot = hoursPerSlot;
        }

        public int getProjectId() { return projectId; }
        public String getTitle() { return title; }
        public int getHourlyValue() { return hourlyValue; }
        public int getSlots() { return slots; }
        public int getHoursPerSlot() { return hoursPerSlot; }

        private void setSlots(int slots) { this.slots = slots; }
        private void setHoursPerSlot(int h) { this.hoursPerSlot = h; }

        public int itemContribution() { return hourlyValue * hoursPerSlot * slots; }
    }

    private final ObservableList<CartItem> items = FXCollections.observableArrayList();
    private final Map<Integer, CartItem> indexByProject = new HashMap<>();

    public ObservableList<CartItem> getItems() {
        return items;
    }

    // Add or update one project in the cart
    public String addOrUpdate(int projectId, String title, int hourlyValue, int slots, int hoursPerSlot) {
        if (hoursPerSlot < MIN_HOURS || hoursPerSlot > MAX_HOURS) {
            return "Hours per slot must be between 1 and 3.";
        }
        if (slots < 1 || slots > MAX_SLOTS_PER_PROJECT) {
            return "Slots per project must be between 1 and 3.";
        }
        CartItem existing = indexByProject.get(projectId);
        if (existing == null) {
            CartItem item = new CartItem(projectId, title, hourlyValue, slots, hoursPerSlot);
            indexByProject.put(projectId, item);
            items.add(item);
        } else {
            existing.setHoursPerSlot(hoursPerSlot);
            existing.setSlots(slots);
            // items list already reflects the object, no need to replace
        }
        return "SUCCESS";
    }

    public boolean remove(int projectId) {
        CartItem it = indexByProject.remove(projectId);
        if (it == null) return false;
        return items.remove(it);
    }

    public void clear() {
        indexByProject.clear();
        items.clear();
    }

    public int totalContribution() {
        return items.stream().mapToInt(CartItem::itemContribution).sum();
    }

    public boolean isEmpty() { return items.isEmpty(); }
}
