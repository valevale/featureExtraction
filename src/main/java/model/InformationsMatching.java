package model;

import java.util.List;

public class InformationsMatching {

	private List<RelevantInformation> informations;
	//il codice identificativo del cammino: S_C
	//S=numero del sottografo
	//C=numero del cammino
	private String idPath;
	
	public InformationsMatching(List<RelevantInformation> informations, String idPath) {
		this.informations=informations;
		setMatchingReference(this.informations);
		this.idPath=idPath;
	}
	
	public List<RelevantInformation> getInformations() {
		return this.informations;
	}
	
	public String getIdPath() {
		return this.idPath;
	}
	
	/* dato un dominio restituisce l'informazione del matching con quel dominio.
	 * se non è presente restituisce null*/
	public RelevantInformation getInformationOfDomain(int idDomain) {
		for (int i=0; i<this.informations.size(); i++) {
			if (this.informations.get(i).getDomain() == idDomain)
				return this.informations.get(i);
		}
		return null;
	}
	
	private void setMatchingReference(List<RelevantInformation> informations) {
		for (int i=0; i<informations.size(); i++) {
			RelevantInformation info = informations.get(i);
			info.setMatching(this);
		}
	}
}
