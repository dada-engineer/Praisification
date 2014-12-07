package de.dada.praisification.hostlistitem;

public class HostListItem {
	
	public long id;
    public String hostName;

    public HostListItem(long l, String hostName) {
        this.id = l;
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return hostName;
    }
}
