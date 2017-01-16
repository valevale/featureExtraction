package model;

import java.util.ArrayList;
import java.util.List;

public class PairMatching {

	private Xpath xpath1;
	private Xpath xpath2;
	private List<Integer> dominiRaggiungibili1;
	private List<Integer> dominiRaggiungibili2;
	
	public PairMatching(Xpath xpath1, Xpath xpath2) {
		this.xpath1 = xpath1;
		this.xpath2 = xpath2;
		this.dominiRaggiungibili1 = new ArrayList<>();
		this.dominiRaggiungibili2 = new ArrayList<>();
		
	}
	
	public Xpath getXpath1() {
		return this.xpath1;
	}
	
	public Xpath getXpath2() {
		return this.xpath2;
	}
	
	public List<Integer> getDominiRaggiungibiliDaXpath1() {
		return this.dominiRaggiungibili1;
	}
	
	public List<Integer> getDominiRaggiungibiliDaXpath2() {
		return this.dominiRaggiungibili2;
	}
	
	public void setDominiRaggiungibili(Xpath xpath, List<Integer> dominiRaggiungibili) {
		if (xpath.equals(this.xpath1))
			this.dominiRaggiungibili1 = dominiRaggiungibili;
		else if (xpath.equals(this.xpath2))
			this.dominiRaggiungibili2 = dominiRaggiungibili;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((xpath1 == null) ? 0 : xpath1.hashCode());
		result = prime * result + ((xpath2 == null) ? 0 : xpath2.hashCode());
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
		PairMatching other = (PairMatching) obj;
		if (xpath1 == null) {
			if (other.xpath1 != null)
				return false;
		} else if (!xpath1.equals(other.xpath1))
			return false;
		if (xpath2 == null) {
			if (other.xpath2 != null)
				return false;
		} else if (!xpath2.equals(other.xpath2))
			return false;
		return true;
	}

//	public boolean equals(PairMatching p) {
//		return (this.xpath1.getXpath().equals(p.getXpath1().getXpath())
//				&& this.xpath2.getXpath().equals(p.getXpath2().getXpath()));
//	}
}
