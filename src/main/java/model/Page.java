package model;

import org.mongodb.morphia.annotations.Embedded;

/*per il database, rappresenta l'oggetto embedded di WebPage*/
@Embedded
public class Page {

    private String url;
    private String body;
 
    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        this.url = url;
    }
 
    public String getBody() {
        return body;
    }
 
    public void setBody(String body) {
        this.body = body;
    }
}
