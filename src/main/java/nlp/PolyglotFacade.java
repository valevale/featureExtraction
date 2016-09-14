package nlp;


//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.neovisionaries.i18n.LanguageAlpha3Code;
//import lib.utils.JsonMapper;
import lib.utils.MapUtils;
//import lombok.extern.log4j.Log4j;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Entities;
//import scala.Tuple2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.*;
//import java.util.concurrent.TimeUnit;

/**
 * Created by lorenzoluce on 21/03/16.
 */
public class PolyglotFacade {

    public static HashMap<String, List<String>> extractNamedEntities(String langCode, String text){
        if (langCode != null) {
            try {
//                text = text.replaceAll("\"", "'").replaceAll("\\p{C}", "").replaceAll("\t|\r|\n", " ");

//                String input = "{\"lang\":\"" + langCode + "\",\"text\":\"" + text.replaceAll("\"", "'").replaceAll("\n", " ") + "\"}";


                String input = langCode.length()+langCode+text;

                URL url = new URL("http://localhost:5000/todo/api/v1.0/tasks");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "text/plain");
                
                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                     throw new RuntimeException("Failed - HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                JsonNode jsonNode = new ObjectMapper().readTree(br);
                JsonNode entities = jsonNode.get("entities");

                HashMap<String, List<String>> map = new HashMap<>();

                entities.forEach(jsonNode1 -> {
                    String key = jsonNode1.get(0).textValue();
                    JsonNode s3 = jsonNode1.get(1);

                    StringBuilder sb = new StringBuilder();
                    int i = 0;

                    for (int len = s3.size(); i < len; ++i) {
                        if (i > 0) {
                            sb.append(' ');
                        }
                        sb.append(s3.get(i).textValue());
                    }

                    MapUtils.mergeLists(map, key, sb.toString());
                });

                conn.disconnect();
                return map;

            } catch (Exception e) {
            	System.out.println("Polyglot TOKENIZE Exception on : " + e);
            }
        }
        return new HashMap<>();
    }


//    public HashMap<String, List<String>> extractNamedEntities(String langCode, String text, String cardId){
//        try {
//
//
//            Tuple2<File, String> tokens = tokenize(text);
//            String lang = tokens._2();
//            if (lang == null)
//                lang = langCode;
//            if (lang != null) {
//                try {
//                    return ner(tokens._1(), tokens._2());
//                } catch (Exception e) {
//                    log.warn("Polyglot NER Exception on "+cardId+" : "+e);
//                }
//            }
//        } catch (Exception e) {
//            log.warn("Polyglot TOKENIZE Exception on "+cardId+" : "+e);
//        }
//        return new HashMap<>();
//    }

//    public Tuple2<File, String> tokenize(String text) throws IOException, InterruptedException {
//        File temp = writeToTempFile(text);
//        String[] args1 = {"polyglot", "tokenize", "--input", temp.getAbsolutePath()};
//        File output = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
//        String language;
//        Process p = Runtime.getRuntime().exec(args1);
//        if (!p.waitFor(10, TimeUnit.SECONDS))
//            log.warn("Tokenize Timeout");
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String line;
//        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
//        while ((line = reader.readLine()) != null) {
//            bw.write(line + "\n");
//        }
//        bw.close();
//        reader.close();
//
//        BufferedReader reader2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//        language = null;
//        while ((line = reader2.readLine()) != null) {
//            if (line.contains("detected"))
//                language = LanguageAlpha3Code.findByName(line.substring(line.indexOf("Language ") + 9, line.indexOf(" is detected"))).get(0).getAlpha2().toString();
//        }
//        reader2.close();
//
//        p.destroy();
//
//        return new Tuple2<>(output, language);
//    }
//
//    public HashMap<String, List<String>> ner(File temp, String languageCode) throws IOException, InterruptedException {
//        BufferedReader reader = nlp("ner", temp, languageCode);
//
//        HashMap<String, List<String>> entities = new HashMap<>();
//        String type = "";
//        String entity = "";
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            if (line.endsWith("I-ORG") || line.endsWith("I-LOC") || line.endsWith("I-PER")) {
//                entity = entity+line.replaceFirst("\\s*(I-ORG|I-LOC|I-PER)$","")+" ";
//                type = line.substring(line.length()-3);
//            } else {
//                if (!entity.isEmpty() && entity.trim().length()>1)
//                    MapUtils.mergeLists(entities, type, entity.substring(0, entity.length()-1));
//                entity = "";
//            }
//        }
//        reader.close();
//
//        return entities;
//    }
//
//    public BufferedReader nlp(String function, File temp, String languageCode) throws IOException, InterruptedException {
//        String[] args;
//        if (languageCode != null) {
//            args = new String[]{"polyglot", "--lang", languageCode, function, "--input", temp.getAbsolutePath()};
//        } else {
//            args = new String[]{"polyglot", function, "--input", temp.getAbsolutePath()};
//        }
//        BufferedReader reader = null;
//        int maxTries = 0;
//        while (maxTries<2) {
//            Process p = Runtime.getRuntime().exec(args);
//
//            if (!p.waitFor(10+(maxTries*10), TimeUnit.SECONDS))
//                log.warn("NLP timeout "+10+(maxTries*10)+". Destroy it");
//
//            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            if (reader.ready())
//                break;
//            BufferedReader tempReader = new BufferedReader(new FileReader(temp.getAbsolutePath()));
//            if (tempReader.readLine() == null) // il file temp in input è vuoto
//                break;
//
//            log.warn("nlp output not found with " + function + " in " + languageCode);
//
//            maxTries++;
//        }
//        return reader;
//    }
//
//    private void downloadPackage(String function, String languageCode) {
//        try {
//            if (languageCode != null) {
//                String[] args2 = new String[]{"polyglot", "download", function + "2." + languageCode};
//                Process p2 = Runtime.getRuntime().exec(args2);
//                if (!p2.waitFor(120, TimeUnit.SECONDS))
//                    log.warn("Timeout In DownloadPackage "+function+"."+languageCode);
//                p2.destroy();
//            }
//        } catch (IOException | InterruptedException e) {
//            log.error("Error downloading polyglot packages");
//        }
//    }
//
//    public File writeToTempFile(String text) throws IOException {
//        text = text.replaceAll("[^\\x20-\\x7e]", "");
//        File temp = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
//        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
//        bw.write(text);
//        bw.close();
//        return temp;
//    }


}
