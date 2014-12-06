package de.dada.praisification.hostlistitem;

public class HostListItem {
	
	public String id;
    public String content;

    public HostListItem(String id, String content) {
        this.id = id;
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
