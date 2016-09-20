package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("queries")
@Indexes(@Index(value = "id", fields = @Field("id")))
public class QueryEntry {
	@Id
    private ObjectId id;
    private String query;
    
    public QueryEntry() {
    }

    public ObjectId getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
