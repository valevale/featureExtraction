package model;

import java.util.List;

public class InformationsMatching {

	private List<RelevantInformation> informations;
	
	public InformationsMatching(List<RelevantInformation> informations) {
		this.informations=informations;
	}
	
	public List<RelevantInformation> getInformations() {
		return this.informations;
	}
}
