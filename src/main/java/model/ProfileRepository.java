package model;

import java.util.HashMap;
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
			InformationsMatching currentMatching = matchings.get(i);
			for (int j=0; j<currentMatching.getInformations().size(); j++) {
				RelevantInformation info = currentMatching.getInformations().get(j);
				int infoIdDomain = info.getDomain();
				if (this.domain2profile.containsKey(infoIdDomain)) {
					Profile p = this.domain2profile.get(infoIdDomain);
					p.addInformation(info);
				}
				//il profilo con quell'id non è presente, lo creiamo
				else {
					Profile p = new Profile(infoIdDomain);
					p.addInformation(info);
					this.domain2profile.put(infoIdDomain, p);
				}

			}
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
