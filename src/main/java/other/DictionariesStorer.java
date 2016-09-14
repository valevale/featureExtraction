package other;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

import model.Page;

public class DictionariesStorer {
	public static void main(String[] args) {
		final Morphia morphia = new Morphia();

    	// tell Morphia where to find your classes
    	// can be called multiple times with different packages or classes
    	morphia.mapPackage("model");

    	// create the Datastore connecting to the default port on the local host
    	final Datastore datastore = morphia.createDatastore(new MongoClient(), "dictionaries");
    	//datastore.getDB().dropDatabase();
    	datastore.ensureIndexes();
    	
    	//prendi file
    	String fileName = "/home/valentina/Scrivania/";

    	String content = "";
		//read file into stream, try-with-resources
    	try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

//			String line;
//			while ((line = br.readLine()) != null) {
//				Di
//				content = content + line;
//			}

		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	System.out.println(content);
    	
//    	final Page raggi = new Page(url, content);
//    	datastore.save(raggi);
	}
}
