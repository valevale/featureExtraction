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
public class WebPage {
	@Id
    private ObjectId id;
    private String html;
    private String url;
    private QueryEntry query;
    
    public WebPage() {
    }

    public ObjectId getId() {
        return id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public QueryEntry getQuery() {
        return query;
    }

    public void setQuery(QueryEntry query) {
        this.query = query;
    }
    
}
