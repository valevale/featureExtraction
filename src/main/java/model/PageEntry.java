package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("pages")
@Indexes(@Index(value = "id", fields = @Field("id")))
public class PageEntry {
    @Id
    private ObjectId id;
    private String crawling_id;
    @Embedded
    private Page page;
    @Embedded
    private Snapshot snapshot;
    
    public PageEntry() {
    }

    public ObjectId getId() {
        return id;
    }

    public String getCrawlingId() {
        return crawling_id;
    }

    public void setCrawlingId(final String crawling_id) {
        this.crawling_id = crawling_id;
    }
    
    public Page getPage() {
        return page;
    }

    public void setPage(final Page page) {
        this.page = page;
    }
    
    public Snapshot getSnapshot() {
        return snapshot;
    }

    public void setPage(final Snapshot snapshot) {
        this.snapshot = snapshot;
    }
//    public String toPrettyString() {
//    	String pretty ="";
//    	pretty += "url: " + this.url + "\n";
//    	//pretty += "content: " + this.html;
//    	return pretty;
//    }

}