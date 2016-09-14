package model;

import java.util.ArrayList;
import java.util.HashSet;

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
    
    private HashSet<BlacklistElement> blacklistedTxt;
    private HashSet<BlacklistElement> blacklistedImg;
    private boolean monitored;
    
    @Reference
    private ArrayList<Page> discoveredPages;
    
    public Source() {
    }

    public Source(ArrayList<Page> discoveredPages) {
        this.discoveredPages = discoveredPages;
    }

    public ObjectId getId() {
        return id;
    }

    public ArrayList<Page> getDiscoveredPages() {
        return discoveredPages;
    }

    public void setDiscoveredPages(ArrayList<Page> discoveredPages) {
        this.discoveredPages = discoveredPages;
    }
    
    public HashSet<BlacklistElement> getBlacklistedTxt() {
        return blacklistedTxt;
    }

    public void setBlacklistedTxt(HashSet<BlacklistElement> blacklistedTxt) {
        this.blacklistedTxt = blacklistedTxt;
    }
    
    public HashSet<BlacklistElement> getBlacklistedImg() {
        return blacklistedImg;
    }

    public void setBlacklistedImg(HashSet<BlacklistElement> blacklistedImg) {
        this.blacklistedImg = blacklistedImg;
    }
    
    public boolean getMonitored() {
        return monitored;
    }

    public void setMonitored(boolean monitored) {
        this.monitored = monitored;
    }
   
    
    public String toPrettyString() {
    	String pretty ="";
//    	for (int i=0;i<this.discoveredPages.size();i++) {
//    		pretty = pretty + (i+1) +") " + this.discoveredPages.get(i).toPrettyString();
//    	}
//    	pretty = pretty + this.discoveredPages.get(0).toPrettyString();
    	if (this.blacklistedTxt == null) {pretty=pretty+"null";} else {
    	pretty = pretty + this.blacklistedTxt.toString();}
    	pretty=pretty+"\n";
    	if (this.blacklistedImg == null) {pretty=pretty+"null";} else {
        	pretty = pretty + this.blacklistedImg.toString();}
    	
//    	for (int i=0;i<this.blacklistedTxt.size();i++) {
//    		pretty = pretty + "BLACKLISTTXT " + this.blacklistedTxt.iterator().next().getValue() + "\n++\n";
//    	}
    	return pretty;
    }
    
    

}
