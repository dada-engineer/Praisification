package de.dada.praisification.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dada.praisification.hostlistitem.HostListItem;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ProtocolContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<HostListItem> ITEMS = new ArrayList<HostListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, HostListItem> ITEM_MAP = new HashMap<String, HostListItem>();

    static {
        // Add 3 sample items.
        addItem(new HostListItem("1", "Hostname 1"));
        addItem(new HostListItem("2", "Hostname 2"));
        addItem(new HostListItem("3", "Hostname 3"));
    }

    private static void addItem(HostListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
}
