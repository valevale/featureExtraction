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
    
}
