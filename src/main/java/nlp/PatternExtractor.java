package nlp;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lib.utils.MapUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lorenzoluce on 04/08/16.
 */
public class PatternExtractor {

    private static PatternExtractor patternExtractor;

    private static Pattern email;
    private static Pattern mailto;
    private static Pattern date;
    private static Pattern simplePhone;

    private PatternExtractor() {
        email = Pattern.compile("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
        mailto = Pattern.compile("mailto\\:\\S+");
        date = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d");
        simplePhone = Pattern.compile("\\s+\\d{3}(\\s+|\\s*-\\s*)?\\d{3}(\\s+|\\s*-\\s*)?\\d{4,5}\\s+");
    }

    public static PatternExtractor getInstance() {
        if (patternExtractor == null) {
            patternExtractor = new PatternExtractor();
        }
        return patternExtractor;
    }

    public HashMap<String, List<String>> extractFeatures(String text, String pageBody) {
        HashMap<String, List<String>> entities = new HashMap<>();
        Iterator<PhoneNumberMatch> phones = PhoneNumberUtil.getInstance().findNumbers(text, null).iterator();
        while (phones.hasNext()){
            Phonenumber.PhoneNumber number = phones.next().number();
            MapUtils.mergeLists(entities, "PHONE", "+"+number.getCountryCode()+number.getNationalNumber());
        }
        Matcher m = email.matcher(text);
        while (m.find()) {
            MapUtils.mergeLists(entities, "EMAIL", m.group());
        }
        if (pageBody!=null) {
            Matcher m2 = mailto.matcher(pageBody);
            while (m2.find()) {
                Matcher m22 = email.matcher(m2.group());
                while (m22.find()) {
                    MapUtils.mergeLists(entities, "EMAIL", m22.group());
                }
            }
        }
        Matcher m1 = date.matcher(text);
        while (m1.find()) {
            MapUtils.mergeLists(entities, "DATE", m1.group());
        }
        Matcher m3 = simplePhone.matcher(text);
        while (m3.find()) {
            MapUtils.mergeLists(entities, "PHONE", m3.group());
        }

        return entities;
    }

}
