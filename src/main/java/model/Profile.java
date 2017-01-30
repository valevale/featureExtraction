package model;

import java.util.ArrayList;
import java.util.List;

public class Profile {

	private int idDomain;
	private String idDbDomain;
	private List<RelevantInformation> profileInformations;

	public Profile(int idDomain) {
		this.idDomain=idDomain;
		this.profileInformations = new ArrayList<>();
		//MAPPA ID
		//icittà -> 1_5750678b3387e31f516fa1c7
		//cylex -> 2_5750678b3387e31f516fa1d0
		//inelenco -> 3_575067b33387e31f516face0
		//misterimprese -> 4_5750678b3387e31f516fa1cd
		//paginebianche -> 5_5750678a3387e31f516fa185
		if (idDomain == 1)
			this.idDbDomain="5750678b3387e31f516fa1c7";
		else if (idDomain == 2)
			this.idDbDomain="5750678b3387e31f516fa1d0";
		else if (idDomain == 3)
			this.idDbDomain="575067b33387e31f516face0";
		else if (idDomain == 4)
			this.idDbDomain="5750678b3387e31f516fa1cd";
		else if (idDomain == 5)
			this.idDbDomain="5750678a3387e31f516fa185";
	}

	public int getIdDomain() {
		return this.idDomain;
	}
	
	public String getIdDbDomain() {
		return this.idDbDomain;
	}

	public List<RelevantInformation> getProfileInformations() {
		return this.profileInformations;
	}

	//TODO anche identificativo
	public void addInformation(RelevantInformation info) {
		this.profileInformations.add(info);
	}
}
