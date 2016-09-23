package model;

import org.mongodb.morphia.annotations.Embedded;

@Embedded
public class Snapshot {
	private String parent_id;
 
    public String getParentId() {
        return parent_id;
    }
 
    public void setParentId(String parent_id) {
        this.parent_id = parent_id;
    }
}
