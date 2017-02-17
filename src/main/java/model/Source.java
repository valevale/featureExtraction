package model;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;

@Entity("sources")
@Indexes(@Index(value = "id", fields = @Field("id")))
public class Source {
	@Id
    private ObjectId id;
	@Reference
	private List<WebPage> discoveredPages;
	private String host;
    
    public Source() {
    }
    
    public ObjectId getId() {
    	return id;
    }
    
    public List<WebPage> getPages() {
    	return this.discoveredPages;
    }
    
    public String getHost() {
    	return host;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Source other = (Source) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
    
}
