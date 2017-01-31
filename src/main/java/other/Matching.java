package other;

import java.util.HashMap;
import java.util.Map;

import model.DomainSource;
import model.Xpath;

public class Matching {

	private Map<DomainSource,Xpath> domain2xpath;
	
	public Matching(DomainSource domain1, Xpath xpath1, DomainSource domain2, Xpath xpath2) {
		this.domain2xpath = new HashMap<>();
		this.domain2xpath.put(domain1, xpath1);
		this.domain2xpath.put(domain2, xpath2);
	}
	
	public Matching(Matching m) {
		this.domain2xpath = new HashMap<>(m.getMatching());
	}
	
	public Map<DomainSource,Xpath> getMatching() {
		return this.domain2xpath;
	}
	
	public boolean has(DomainSource d, Xpath x) {
		Xpath xpathOfDomain = this.domain2xpath.get(d);
//		System.out.println("xpath del dominio "+xpathOfDomain.getXpath());
//		System.out.println("xpath nuovo "+x.getXpath());
		if (xpathOfDomain == null) return false;
		boolean has = xpathOfDomain.getXpath().equals(x.getXpath());
//		System.out.println(has);
		return has;
	}
	
	public boolean add(DomainSource d, Xpath x) {
		if (this.domain2xpath.containsKey(d))
			return false;
		else {
			this.domain2xpath.put(d, x);
			return true;
		}
	}
	
	public void addOverriding(DomainSource d, Xpath x) {
			this.domain2xpath.put(d, x);
	}
}
