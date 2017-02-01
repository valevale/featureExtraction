package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//TODO dovrebbero essere informazioni da attaccare al dominio
public class ProfileRepository {

	private static ProfileRepository instance = null;
	private Map<Integer,Profile> domain2profile;

	public static ProfileRepository getInstance() {
		if (instance == null)
			instance = new ProfileRepository();
		return instance;
	}

	private ProfileRepository() {
		this.domain2profile=new HashMap<>();
	}
	
	public Map<Integer,Profile> getRepository() {
		return this.domain2profile;
	}
	
	public Profile getProfile(int idDomain) {
		Profile p = this.domain2profile.get(idDomain);
		return p;
	}

	/* aggiunta di nuove informazioni a più profili */
	public void updateProfiles(List<InformationsMatching> matchings) {
		for (int i=0; i<matchings.size(); i++) {
			System.out.println("NUOVO MATCHING");
			InformationsMatching currentMatching = matchings.get(i);
			List<RelevantInformation> infosOfCurrentMatching = currentMatching.getInformations();
			System.out.println("Id della path "+currentMatching.getIdPath());
			//fin qui prende bene gli id dei path CONTINUA DA QUI
			System.out.println("Ora scorro le sue informazioni");
			System.out.println("Le informazioni sono: "+infosOfCurrentMatching.size());
			for (int j=0; j<infosOfCurrentMatching.size(); j++) {
				System.out.println("Nuova info:");
				RelevantInformation info = infosOfCurrentMatching.get(j);
				int infoIdDomain = info.getDomain();
				System.out.println("dominio dell'info: "+infoIdDomain);
				System.out.println("valore dell'info: "+info.getXpath().getXpath());
				System.out.println("id path: "+info.getMatching().getIdPath());
				if (this.domain2profile.containsKey(infoIdDomain)) {
					System.out.println("Abbiamo già un profilo di quel dominio, aggiungiamo l'info");
					Profile p = this.domain2profile.get(infoIdDomain);
					p.addInformation(info);
				}
				//il profilo con quell'id non è presente, lo creiamo
				else {
					System.out.println("Non abbiamo un profilo per quel dominio. creiamo il profilo e aggiungiamo l'info");
					Profile p = new Profile(infoIdDomain);
					p.addInformation(info);
					this.domain2profile.put(infoIdDomain, p);
				}
				System.out.println();

			}
			System.out.println();
		}
		System.out.println("Alla fine ecco cosa abbiamo");
		Iterator<Integer> it = this.domain2profile.keySet().iterator();
		while (it.hasNext()) {
			Integer currentDomain = it.next();
			Profile p=this.domain2profile.get(currentDomain);
			System.out.println("Dominio: "+currentDomain);
			System.out.println("Dimensione delle info del profilo: "+p.getProfileInformations().size());
			for (int i=0; i<p.getProfileInformations().size(); i++) {
				System.out.println("dominio info: "+p.getProfileInformations().get(i).getDomain());
				System.out.println("xpath info: "+p.getProfileInformations().get(i).getXpath().getXpath());
				System.out.println("path info: "+p.getProfileInformations().get(i).getMatching().getIdPath());
			}
			System.out.println();
		}
	}

	/* aggiunta di nuove informazioni a un profilo di un certo dominio */
	public void updateProfile(int idDomain, List<InformationsMatching> matchings) {
		for (int i=0; i<matchings.size(); i++) {
			InformationsMatching currentMatching = matchings.get(i);
			RelevantInformation info = currentMatching.getInformationOfDomain(idDomain);
			if (this.domain2profile.containsKey(idDomain)) {
				Profile p = this.domain2profile.get(idDomain);
				p.addInformation(info);
			}
			else {
				Profile p = new Profile(idDomain);
				p.addInformation(info);
				this.domain2profile.put(idDomain, p);
			}
		}
	}
}
