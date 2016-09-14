package nlp;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
//import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Created by valerio on 7/22/16.
 */
public class LanguageDetectionFacade {

    private static LanguageDetectionFacade instace;
    private static TextObjectFactory textObjectFactory;
    private static LanguageDetector languageDetector;
    final static Logger log = Logger.getLogger(nlp.LanguageDetectionFacade.class);

    private LanguageDetectionFacade() {
        try {
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
            languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(languageProfiles)
                    .build();
            textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        } catch (IOException e) {
            log.error("Loading LanguageDetectionFacade failed", e);
        }
    }

    public static LanguageDetectionFacade getInstance() {
        if (instace == null) {
            instace = new LanguageDetectionFacade();
        }
        return instace;
    }

    public String detectLanguage(String text) {
        TextObject textObject = textObjectFactory.forText(text);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        return (lang.isPresent()) ? lang.get().getLanguage() : null;
    }
}
