package model;

import java.util.ArrayList;
import java.util.List;

/*classe provvisoria che sostituisce il database delle sorgenti*/
public class DomainsRepository {
	
	private static DomainsRepository instance = null;
	private List<DomainSource> domains;

	public static DomainsRepository getInstance() {
		if (instance == null)
			instance = new DomainsRepository();
		return instance;
	}

	private DomainsRepository() {
		this.domains = new ArrayList<>();
	}
	
	public void addDomain(DomainSource domain) {
		this.domains.add(domain);
	}
	
	//NOTA SULL'INTERFACCIA: crea un dominio. se esiste già nel repository lo restituisce, altrimenti lo crea
	public DomainSource createDomain(String parameter) {
		for (int i=0;i<this.domains.size();i++) {
			if (this.domains.get(i).getParameter().equals(parameter))
				return this.domains.get(i);
		}
		DomainSource newDomain = new DomainSource(parameter);
		this.addDomain(newDomain);
		return newDomain;
	}
	
	public DomainSource getDomain(String parameter) {
		for (int i=0;i<this.domains.size();i++) {
			if (this.domains.get(i).getParameter().equals(parameter))
				return this.domains.get(i);
		}
		return null;
	}
}
