package model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

@Entity("pages")
@Indexes(@Index(value = "url", fields = @Field("url")))
public class Page {
    @Id
    private ObjectId id;
    private String url;
    private String html;
    
    public Page() {
    }

    public Page(final String url, final String content) {
        this.url = url;
        this.html = content;
    }

    public ObjectId getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }
    
    public String getHtml() {
        return html;
    }

    public void setHtml(final String content) {
        this.html = content;
    }
    
    public String toPrettyString() {
    	String pretty ="";
    	pretty += "url: " + this.url + "\n";
    	//pretty += "content: " + this.html;
    	return pretty;
    }

}