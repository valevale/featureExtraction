package segmentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jsoup.nodes.Node;
import org.xml.sax.SAXException;

import lib.utils.NodeUtils;
import lib.utils.XpathApplier;
import lib.utils.XpathMaker;
import model.Segment;
import model.WebPageDocument;

public class SegmentExtractor {

	XpathApplier xpapplier;
	XpathMaker xpmaker;
	WebPageSegmentator segmentator;

	private static SegmentExtractor instance = null;

	public static SegmentExtractor getInstance() {
		if (instance == null)
			instance = new SegmentExtractor();
		return instance;
	}

	private SegmentExtractor() {
		xpapplier = XpathApplier.getInstance();
		xpmaker = XpathMaker.getInstance();
		segmentator = WebPageSegmentator.getInstance();
	}


	public Set<Segment> extractSegments(WebPageDocument doc, double parameterTextFusion) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerException {

		//segmentazione
		List<Node> nodes_segments = segmentator.segment(doc.getDocument_jsoup(), parameterTextFusion);

		Set<Segment> segments = new HashSet<>();
		for (int i=0;i<nodes_segments.size();i++) {
			try {
				Segment toAdd = new Segment(nodes_segments.get(i), doc);
				String content = NodeUtils.getNodesContent(toAdd.getW3cNodes());
				String cleaned = content.replaceAll("[^a-zA-Z0-9]+", "");
				if (cleaned.length() > 1) {
					segments.add(toAdd);
				}
			} catch (Exception e) {
				System.out.println("Errore durante la generazione di un segmento "+e);
			}
		}

		return segments;
	}
}
