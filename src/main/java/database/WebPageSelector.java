package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Source;
import model.WebPage;
import model.WebPageDocument;
//import scala.Tuple2;
//import scala.Tuple2;
import scala.Tuple2;

/*classe che serve per prendere certi tipi di pagine
 * */
public class WebPageSelector {

	//raccolta di pagine con ancore uniche, del dominio specificato
	//mvn -e exec:java -D exec.mainClass=test.TestPagineUniche
	public static List<WebPage> getPageWithUniqueName(Source source) {
		System.out.println("******ELABORO UNA NUOVA SORGENTE");
		List<WebPage> pages = new ArrayList<>();
		Map<String,List<WebPage>> ancora2pagine = new HashMap<>();
		//scorro le sue pagine
		for (int j=0;j<source.getPages().size();j++) {
//			if ((j+1)%100==0)
//				System.out.println("*****pagina numero: "+(j+1)+"/"+source.getPages().size());
			WebPage currentPage = source.getPages().get(j);
			String ancora = currentPage.getQuery().getQuery();
			List<WebPage> pagineConAncora = ancora2pagine.get(ancora);
			if (pagineConAncora == null) {
				//non è presente l'ancora nella mappa. creo
				pagineConAncora = new ArrayList<>();
			}
			pagineConAncora.add(currentPage);
			//test: dimensione
			//			System.out.println("DIMENSIONE LISTA DI PAGINE CON ANCORA "+ancora+" :"+pagineConAncora.size());
			ancora2pagine.put(ancora, pagineConAncora);
		}

		//ora aggiungo alla lista tutte le pagine con ancora unica nella sorgente
		Iterator<String> ancoreIt = ancora2pagine.keySet().iterator();
		while (ancoreIt.hasNext()) {
			String ancoraCorrente = ancoreIt.next();
			int numeroPagineConAncora = ancora2pagine.get(ancoraCorrente).size();
			if (numeroPagineConAncora == 1) {
				pages.add(ancora2pagine.get(ancoraCorrente).get(0));
			}
		}
		return pages;
	}

	//dato una lista di domini (o insomma la lista delle liste delle pagine con ancore uniche di quei domini)
	//restituisci una lista delle pagine (o delle sole ancore) presenti in almeno 2 domini
	//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
	public static Map<String,Set<Tuple2<String,WebPage>>> getPagesWUNIMTOS_new(
			Map<String,List<WebPage>> domain2pages) throws Exception {
		Map<String,Set<Tuple2<String,WebPage>>> ancora2pagesWUNIMTOS = new HashMap<>();
		//scorro le liste
		Iterator<String> domainIt = domain2pages.keySet().iterator();
		int c=1;
		while (domainIt.hasNext()) {
			System.out.println("dominio numero "+(c));
			String currentSource = domainIt.next();
			List<WebPage> currentPagesList = domain2pages.get(currentSource);
			//scorro la lista
			for (int j=0;j<currentPagesList.size();j++) {
				System.out.println("pagina "+(j+1)+"/"+currentPagesList.size());
				//per ogni pagina della lista corrente
				WebPage currentPage = currentPagesList.get(j);
				String currentAncora = currentPage.getQuery().getQuery();
				//controllo se l'ancora è presente nelle altre liste
				//devo scorrere le altre liste
				Iterator<String> otherDomainIt = domain2pages.keySet().iterator();
				while (otherDomainIt.hasNext()) {
					String currentOtherSource = otherDomainIt.next();
					//se è la stessa lista non li comparo
					if (!currentSource.equals(currentOtherSource)) {
						List<WebPage> otherPagesList = domain2pages.get(currentOtherSource);
						WebPage otherPageWithSameAncora = searchPageWith(currentAncora,otherPagesList);
						if (otherPageWithSameAncora != null) {
							//cerco l'ancora
							Set<Tuple2<String,WebPage>> setOfCurrentAncora = 
									ancora2pagesWUNIMTOS.get(currentAncora);
							if (setOfCurrentAncora == null) {
								setOfCurrentAncora = new HashSet<>();
							}
							setOfCurrentAncora.add(new Tuple2<>(currentSource, currentPage));
							setOfCurrentAncora.add(new Tuple2<>(currentOtherSource,otherPageWithSameAncora));

							ancora2pagesWUNIMTOS.put(currentAncora, setOfCurrentAncora);
						}
					}
				}
			}
			c++;
		}
		return ancora2pagesWUNIMTOS;
	}

	//dato una lista di domini (o insomma la lista delle liste delle pagine con ancore uniche di quei domini)
	//restituisci una lista delle pagine (o delle sole ancore) presenti in almeno 2 domini
	//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
	public static Map<String,Set<WebPageDocument>> getPagesWUNIMTOS(
			Map<String,List<WebPage>> domain2pages) throws Exception {
		Map<String,Set<WebPageDocument>> ancora2pagesWUNIMTOS = new HashMap<>();
		//scorro le liste
		Iterator<String> domainIt = domain2pages.keySet().iterator();
		int c=1;
		while (domainIt.hasNext()) {
			System.out.println("dominio numero "+(c));
			String currentSource = domainIt.next();
			List<WebPage> currentPagesList = domain2pages.get(currentSource);
			//scorro la lista
			for (int j=0;j<currentPagesList.size();j++) {
				System.out.println("pagina "+(j+1)+"/"+currentPagesList.size());
				//per ogni pagina della lista corrente
				WebPage currentPage = currentPagesList.get(j);
				String currentAncora = currentPage.getQuery().getQuery();
				//controllo se l'ancora è presente nelle altre liste
				//devo scorrere le altre liste
				Iterator<String> otherDomainIt = domain2pages.keySet().iterator();
				while (otherDomainIt.hasNext()) {
					String currentOtherSource = otherDomainIt.next();
					//se è la stessa lista non li comparo
					if (!currentSource.equals(currentOtherSource)) {
						List<WebPage> otherPagesList = domain2pages.get(currentOtherSource);
						WebPage otherPageWithSameAncora = searchPageWith(currentAncora,otherPagesList);
						if (otherPageWithSameAncora != null) {
							//cerco l'ancora
							Set<WebPageDocument> setOfCurrentAncora = 
									ancora2pagesWUNIMTOS.get(currentAncora);
							System.out.println("ho preso il set con l'ancora corrente");
							if (setOfCurrentAncora == null) {
								setOfCurrentAncora = new HashSet<>();
							}
							System.out.println("creo le pagine web");
							WebPageDocument wpdOfCurrentPage = new WebPageDocument(currentPage,currentSource);
							System.out.println("creata la prima");
							WebPageDocument wpdOfOtherPage = new WebPageDocument(otherPageWithSameAncora,
									currentOtherSource);
							System.out.println("creata la seconda");

							setOfCurrentAncora.add(wpdOfCurrentPage);
							setOfCurrentAncora.add(wpdOfOtherPage);

							System.out.println("aggiunte alla lista");
							ancora2pagesWUNIMTOS.put(currentAncora, setOfCurrentAncora);
							System.out.println("aggiunto alla mappa");
							//rimuovo l'elemento "other", in modo da snellire le mappe
							otherPagesList.remove(otherPageWithSameAncora);
						}
					}
				}
			}
			//rimuovo la sorgente corrente
			domain2pages.remove(currentSource);
			c++;
		}
		return ancora2pagesWUNIMTOS;
	}


	//dato una lista di domini (o insomma la lista delle liste delle pagine con ancore uniche di quei domini)
	//restituisci una lista delle pagine (o delle sole ancore) presenti in almeno 2 domini
	//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
	public static Map<String,Set<WebPageDocument>> getPagesWUNIMTOS_old(Map<Source,List<WebPage>> domain2pages) throws Exception {
		Map<String,Set<WebPageDocument>> ancora2pagesWUNIMTOS = new HashMap<>();
		//scorro le liste
		Iterator<Source> domainIt = domain2pages.keySet().iterator();
		int c=1;
		while (domainIt.hasNext()) {
			System.out.println("dominio numero "+(c));
			Source currentSource = domainIt.next();
			List<WebPage> currentPagesList = domain2pages.get(currentSource);
			//scorro la lista
			for (int j=0;j<currentPagesList.size();j++) {
				System.out.println("pagina "+(j+1)+"/"+currentPagesList.size());
				//per ogni pagina della lista corrente
				WebPage currentPage = currentPagesList.get(j);
				String currentAncora = currentPage.getQuery().getQuery();
				//controllo se l'ancora è presente nelle altre liste
				//devo scorrere le altre liste
				Iterator<Source> otherDomainIt = domain2pages.keySet().iterator();
				while (otherDomainIt.hasNext()) {
					Source currentOtherSource = otherDomainIt.next();
					//se è la stessa lista non li comparo
					if (!currentSource.getId().toString().equals(currentOtherSource.getId().toString())) {
						List<WebPage> otherPagesList = domain2pages.get(currentOtherSource);
						WebPage otherPageWithSameAncora = searchPageWith(currentAncora,otherPagesList);
						if (otherPageWithSameAncora != null) {
							//cerco l'ancora
							Set<WebPageDocument> setOfCurrentAncora = 
									ancora2pagesWUNIMTOS.get(currentAncora);
							System.out.println("ho preso il set con l'ancora corrente");
							if (setOfCurrentAncora == null) {
								setOfCurrentAncora = new HashSet<>();
							}
							System.out.println("creo le pagine web");
							WebPageDocument wpdOfCurrentPage = new WebPageDocument(currentPage,currentSource);
							System.out.println("creata la prima");
							WebPageDocument wpdOfOtherPage = new WebPageDocument(otherPageWithSameAncora,
									currentOtherSource);
							System.out.println("creata la seconda");

							setOfCurrentAncora.add(wpdOfCurrentPage);
							setOfCurrentAncora.add(wpdOfOtherPage);

							System.out.println("aggiunte alla lista");
							ancora2pagesWUNIMTOS.put(currentAncora, setOfCurrentAncora);
							System.out.println("aggiunto alla mappa");
						}
					}
				}
			}
			c++;
		}
		return ancora2pagesWUNIMTOS;
	}


	//dato una lista di domini (o insomma la lista delle liste delle pagine con ancore uniche di quei domini)
	//restituisci una lista delle pagine (o delle sole ancore) presenti in almeno 2 domini
	//WUNIMTOS = WithUniqueNamesInMoreThanOneSource
	//	public static Map<Source,Set<WebPage>> getPagesWUNIMTOS_old(Map<Source,List<WebPage>> domain2pages) {
	//		Map<Source,Set<WebPage>> domain2pagesWUNIMTOS = new HashMap<>();
	//		//scorro le liste
	//		Iterator<Source> domainIt = domain2pages.keySet().iterator();
	//		while (domainIt.hasNext()) {
	//			Source currentSource = domainIt.next();
	//			String currentDomain = domainIt.next().getId().toString();
	//			List<WebPage> currentPagesList = domain2pages.get(currentSource);
	//			//scorro la lista
	//			for (int j=0;j<currentPagesList.size();j++) {
	//				//per ogni pagina della lista corrente
	//				WebPage currentPage = currentPagesList.get(j);
	//				String currentAncora = currentPage.getQuery().getQuery();
	//				//controllo se l'ancora è presente nelle altre liste
	//				//devo scorrere le altre liste
	//				Iterator<Source> otherDomainIt = domain2pages.keySet().iterator();
	//				while (otherDomainIt.hasNext()) {
	//					Source currentOtherSource = otherDomainIt.next();
	//					String currentOtherDomain = otherDomainIt.next().getId().toString();
	//					//se è la stessa lista non li comparo
	//					if (!currentDomain.equals(currentOtherDomain)) {
	//						List<WebPage> otherPagesList = domain2pages.get(currentOtherSource);
	//						WebPage otherPageWithSameAncora = searchPageWith(currentAncora,otherPagesList);
	//						if (otherPageWithSameAncora != null) {
	//							//posso aggiungere quest'ancora ai set di ciascun dominio
	//							Set<WebPage> pagesWUNIMTOSOfCurrentDomain = domain2pagesWUNIMTOS.get(currentSource);
	//							if (pagesWUNIMTOSOfCurrentDomain == null) {
	//								//creo il set
	//								pagesWUNIMTOSOfCurrentDomain = new HashSet<>();
	//							}
	//							pagesWUNIMTOSOfCurrentDomain.add(currentPage);
	//							domain2pagesWUNIMTOS.put(currentSource, pagesWUNIMTOSOfCurrentDomain);
	//							//l'altro dominio
	//							Set<WebPage> pagesWUNIMTOSOfOtherDomain = domain2pagesWUNIMTOS.get(currentOtherSource);
	//							if (pagesWUNIMTOSOfOtherDomain == null) {
	//								//creo il set
	//								pagesWUNIMTOSOfOtherDomain = new HashSet<>();
	//							}
	//							pagesWUNIMTOSOfOtherDomain.add(otherPageWithSameAncora);
	//							domain2pagesWUNIMTOS.put(currentOtherSource, pagesWUNIMTOSOfOtherDomain);
	//						}
	//					}
	//				}
	//			}
	//		}
	//		return domain2pagesWUNIMTOS;
	//	}

	//data un'ancora e una lista, restituisce la pagina con quell'ancora
	//se non c'è nessuna pagina con quell'ancora, restituisce null
	public static WebPage searchPageWith(String ancora, List<WebPage> otherPagesList) {
		//scorro la lista
		for (int i=0;i<otherPagesList.size();i++) {
			//			System.out.println("ricerca interna: "+(i+1)+"/"+otherPagesList.size());
			WebPage currentPage = otherPagesList.get(i);
			if (currentPage.getQuery().getQuery().equals(ancora)) {
				//				System.out.println("TROVATA!");
				return currentPage;
			}
		}
		return null;
	}
	//raccolta di pagine con ancore uniche, del dominio specificato
	//	public static List<Tuple2<WebPage,String>> getPagesWithUniqueName(Source source) {
	//		List<Tuple2<WebPage,String>> pages = new ArrayList<>();
	//		Map<String,List<WebPage>> ancora2pagine = new HashMap<>();
	//		//scorro le sue pagine
	//		for (int j=0;j<source.getPages().size();j++) {
	//			WebPage currentPage = source.getPages().get(j);
	//			String ancora = currentPage.getQuery().getQuery();
	//			List<WebPage> pagineConAncora = ancora2pagine.get(ancora);
	//			if (pagineConAncora == null) {
	//				//non è presente l'ancora nella mappa. creo
	//				pagineConAncora = new ArrayList<>();
	//			}
	//			pagineConAncora.add(currentPage);
	//			//test: dimensione
	//			System.out.println("DIMENSIONE LISTA DI PAGINE CON ANCORA "+ancora+" :"+pagineConAncora.size());
	//			ancora2pagine.put(ancora, pagineConAncora);
	//		}
	//
	//		//ora aggiungo alla lista tutte le pagine con ancora unica nella sorgente
	//		Iterator<String> ancoreIt = ancora2pagine.keySet().iterator();
	//		while (ancoreIt.hasNext()) {
	//			String ancoraCorrente = ancoreIt.next();
	//			int numeroPagineConAncora = ancora2pagine.get(ancoraCorrente).size();
	//			if (numeroPagineConAncora == 1) {
	//				pages.add(new Tuple2<>(ancora2pagine.get(ancoraCorrente).get(0),ancoraCorrente));
	//			}
	//		}
	//		return pages;
	//	}


	//raccolta di pagine con ancore uniche, del dominio specificato
	//	public static List<String> getUniqueName(Source source) {
	//		List<String> pages = new ArrayList<>();
	//		//ogni pagina è rappresentata dal suo id
	//		Map<String,List<String>> ancora2pagine = new HashMap<>();
	//		//scorro le sue pagine
	//		for (int j=0;j<source.getPages().size();j++) {
	//			WebPage currentPage = source.getPages().get(j);
	//			String ancora = currentPage.getQuery().getQuery();
	//			List<String> pagineConAncora = ancora2pagine.get(ancora);
	//			if (pagineConAncora == null) {
	//				//non è presente l'ancora nella mappa. creo
	//				pagineConAncora = new ArrayList<>();
	//			}
	//			pagineConAncora.add(currentPage.getId().toString());
	//			//test: dimensione
	//			System.out.println("DIMENSIONE LISTA DI PAGINE CON ANCORA "+ancora+" :"+pagineConAncora.size());
	//			ancora2pagine.put(ancora, pagineConAncora);
	//		}
	//
	//		//ora aggiungo alla lista tutte le ancore uniche nella sorgente
	//		Iterator<String> ancoreIt = ancora2pagine.keySet().iterator();
	//		while (ancoreIt.hasNext()) {
	//			String ancoraCorrente = ancoreIt.next();
	//			int numeroPagineConAncora = ancora2pagine.get(ancoraCorrente).size();
	//			if (numeroPagineConAncora == 1) {
	//				pages.add(ancoraCorrente);
	//			}
	//		}
	//		return pages;
	//	}

	//dato una lista di domini (o insomma la lista delle liste delle pagine con ancore uniche di quei domini)
	//restituisci una lista delle pagine (o delle sole ancore) presenti in almeno 2 domini
	//	public static Map<Integer,Set<String>> getUniqueNamesThatAreInMoreThanOneSource(
	//			Map<Integer,List<String>> domain2ancore) {
	//		Map<Integer,Set<String>> uniqueNames = new HashMap<>();
	//		//scorro le liste
	//		Iterator<Integer> domainIt = domain2ancore.keySet().iterator();
	//		while (domainIt.hasNext()) {
	//			//		for (int i=0;i<nameslists.size();i++) {
	//			//			List<String> currentList = nameslists.get(i);
	//			int currentDomain = domainIt.next();
	//			List<String> currentList = domain2ancore.get(currentDomain);
	//			//scorro la lista
	//			for (int j=0;j<currentList.size();j++) {
	//				String currentAncora = currentList.get(j);
	//				//controllo se è presente nelle altre liste
	//				Iterator<Integer> otherDomainIt = domain2ancore.keySet().iterator();
	//				while (otherDomainIt.hasNext()) {
	//					//				for (int k=0;k<nameslists.size();k++) {
	//					//					List<String> otherList = nameslists.get(k);
	//					int currentOtherDomain = otherDomainIt.next();
	//					List<String> otherList = domain2ancore.get(currentOtherDomain);
	//					//se è la stessa lista non li comparo
	//					if (currentDomain != currentOtherDomain) {
	//						if (otherList.contains(currentAncora)) {
	//							//posso aggiungere quest'ancora ai set di ciascun dominio
	//							Set<String> ancoreOfCurrentDomain = uniqueNames.get(currentDomain);
	//							if (ancoreOfCurrentDomain == null) {
	//								//creo il set
	//								ancoreOfCurrentDomain = new HashSet<>();
	//							}
	//							ancoreOfCurrentDomain.add(currentAncora);
	//							uniqueNames.put(currentDomain, ancoreOfCurrentDomain);
	//							//l'altro dominio
	//							Set<String> ancoreOfOtherDomain = uniqueNames.get(currentOtherDomain);
	//							if (ancoreOfOtherDomain == null) {
	//								//creo il set
	//								ancoreOfOtherDomain = new HashSet<>();
	//							}
	//							ancoreOfOtherDomain.add(currentAncora);
	//							uniqueNames.put(currentOtherDomain, ancoreOfOtherDomain);
	//						}
	//					}
	//				}
	//			}
	//		}
	//		return uniqueNames;
	//	}

	//dato una lista di domini (o insomma la lista delle liste delle pagine con ancore uniche di quei domini)
	//restituisci una lista delle pagine (o delle sole ancore) presenti in almeno 2 domini
	//	public static Map<Integer,Set<WebPage>> getPagesWithUniqueNamesThatAreInMoreThanOneSource(
	//			Map<Integer,List<Tuple2<WebPage,String>>> domain2ancore) {
	//		Map<Integer,Set<WebPage>> uniqueNames = new HashMap<>();
	//		//scorro le liste
	//		Iterator<Integer> domainIt = domain2ancore.keySet().iterator();
	//		while (domainIt.hasNext()) {
	//			//			for (int i=0;i<nameslists.size();i++) {
	//			//				List<String> currentList = nameslists.get(i);
	//			int currentDomain = domainIt.next();
	//			List<Tuple2<WebPage,String>> currentList = domain2ancore.get(currentDomain);
	//			//scorro la lista
	//			for (int j=0;j<currentList.size();j++) {
	//				String currentAncora = currentList.get(j)._2();
	//				//controllo se è presente nelle altre liste
	//				Iterator<Integer> otherDomainIt = domain2ancore.keySet().iterator();
	//				while (otherDomainIt.hasNext()) {
	//					//					for (int k=0;k<nameslists.size();k++) {
	//					//						List<String> otherList = nameslists.get(k);
	//					int currentOtherDomain = otherDomainIt.next();
	//					List<Tuple2<WebPage,String>> otherList = domain2ancore.get(currentOtherDomain);
	//					//se è la stessa lista non li comparo
	//					if (currentDomain != currentOtherDomain) {
	//						if (otherList.contains(currentAncora)) {
	//							//posso aggiungere quest'ancora ai set di ciascun dominio
	//							Set<WebPage> ancoreOfCurrentDomain = uniqueNames.get(currentDomain);
	//							if (ancoreOfCurrentDomain == null) {
	//								//creo il set
	//								ancoreOfCurrentDomain = new HashSet<>();
	//							}
	//							ancoreOfCurrentDomain.add(currentList.get(j)._1());
	//							uniqueNames.put(currentDomain, ancoreOfCurrentDomain);
	//							//l'altro dominio
	//							Set<WebPage> ancoreOfOtherDomain = uniqueNames.get(currentOtherDomain);
	//							if (ancoreOfOtherDomain == null) {
	//								//creo il set
	//								ancoreOfOtherDomain = new HashSet<>();
	//							}
	//							ancoreOfOtherDomain.add(otherList.get(j)._1());
	//							uniqueNames.put(currentOtherDomain, ancoreOfOtherDomain);
	//						}
	//					}
	//				}
	//			}
	//		}
	//		return uniqueNames;
	//	}

}
