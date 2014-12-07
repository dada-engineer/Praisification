package de.dada.praisification.hostlistitem;

public class HostListItem {

    public String hostName;

    public HostListItem(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return hostName;
    }
}
