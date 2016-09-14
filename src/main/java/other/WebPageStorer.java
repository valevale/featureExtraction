package other;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

import model.Page;

public class WebPageStorer {
	
	public static void main(String[] args) {
		final Morphia morphia = new Morphia();

    	// tell Morphia where to find your classes
    	// can be called multiple times with different packages or classes
    	morphia.mapPackage("model");

    	// create the Datastore connecting to the default port on the local host
    	final Datastore datastore = morphia.createDatastore(new MongoClient(), "storer_example");
    	//datastore.getDB().dropDatabase();
    	datastore.ensureIndexes();
    	
    	//prendi file
    	String fileName = "/home/valentina/Scrivania/raggi.html";

    	String url = "http://www.repubblica.it/politica/2016/09/02/news/raggi_a_lavoro_per_cercare_nuovi_nomi_sulle_chat_l_allarme_degli_attivisti_se_forza_con_marra_viene_giu_il_mondo_-147049578/?ref=HREA-1";
    	String content = "";
		//read file into stream, try-with-resources
    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

			String line;
			while ((line = br.readLine()) != null) {
				content = content + line;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	System.out.println(content);
    	
    	final Page raggi = new Page(url, content);
    	datastore.save(raggi);
	}
	
	
}

//
//
//@Entity("webpages")
//@Indexes(@Index(value = "url", fields = @Field("url")))
//class Page {
//    @Id
//    private ObjectId id;
//    private String url;
//    private String content;
//    public Page() {
//    }
//
//    public Page(final String url, final String content) {
//        this.url = url;
//        this.content = content;
//    }
//
//    public ObjectId getId() {
//        return id;
//    }
//
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(final String url) {
//        this.url = url;
//    }
//    
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(final String content) {
//        this.content = content;
//    }
//
//}
