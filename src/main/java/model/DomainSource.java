package model;

import java.util.HashSet;
import java.util.Set;

/*classe provvisoria che sostituisce 'sorce' del database*/
//TODO nella classe source dovrai comunque allegare gli xpath generici, senza referenza o altro
public class DomainSource {

	private Set<Xpath> genericXpaths;
//	private int parameter;
	private String parameter;
	
//	public DomainSource(String parameter) {
//		this.parameter=parameter;
//		this.genericXpaths = new HashSet<>();
//	}
	
	//TODO devi incluedere 'source' nel costruttore
	public DomainSource(String parameter) {
		this.parameter=parameter;
		this.genericXpaths = new HashSet<>();
	}
	
	public String getParameter() {
		return this.parameter;
	}
	
	public Set<Xpath> getGenericXpaths() {
		return this.genericXpaths;
	}
	
	public void setGenericXpaths(Set<Xpath> generalXpaths) {
		this.genericXpaths=generalXpaths;
	}
	
	public void addGenericXpaths(Set<Xpath> generalXpaths) {
		this.genericXpaths.addAll(generalXpaths);
	}
	
	public void addGenericXpath(Xpath generalXpaths) {
		this.genericXpaths.add(generalXpaths);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DomainSource other = (DomainSource) obj;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		return true;
	}

	
	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + parameter;
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		DomainSource other = (DomainSource) obj;
//		if (parameter != other.parameter)
//			return false;
//		return true;
//	}
//	public void addGenericXpaths(Set<Xpath> generalXpaths) {
//		Iterator<Xpath> it = this.genericXpaths.iterator();
//		while (it.hasNext()) {
//			Xpath xpath = it.next();
//			//se nel set c'è un xpath uguale, non aggiungerlo
//			if (xpath.getXpath().equals()){
//				return;
//			}
//		}
//		this.genericXpaths.addAll(generalXpaths);
//	}
}
