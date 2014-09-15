/**
 * @author Maxime Chaste
 */
package graphe;

import java.rmi.Naming;
import java.rmi.NotBoundException;

/**
 * Permet de créer les noeuds et ses fils et lance le serveur.
 * 
 */
public class SiteServeur {

	public static void main(String[] argv) {

		try {
			/* si des voisins on ete donnees */
			if (argv.length > 1) {
				SiteItf courant = null;
				try {
					/*
					 * on regarde si le noeud n'existe deja pas pour eviter de
					 * creer plusieur noeuds identiques
					 */
					if (Naming.lookup(argv[0]) != null) {
						System.out.println("Le noeud " + argv[0]
								+ " existe deja");
						System.exit(0);
					}
				} catch (NotBoundException e) {
					/* s'il n'existe pas alors on le cree */
					courant = new SiteImpl(Integer.parseInt(argv[0]));
					Naming.rebind(argv[0], courant);
				}
				SiteItf voisin;

				/* on parcours tous les voisins donnees en parametres */
				for (int i = 1; i < argv.length; i++) {
					try {
						voisin = (SiteItf) Naming.lookup(argv[i]);
						voisin.addVoisin(courant);
						courant.addVoisin(voisin);
						Naming.rebind(argv[0], courant);
					} catch (NotBoundException e) {
						System.err.println("Le noeud " + argv[i]
								+ " n'existe pas");
					}
				}
			} else {
				try {
					/*
					 * on regarde si le noeud n'existe deja pas pour eviter de
					 * creer plusieur noeuds identiques
					 */
					if (Naming.lookup(argv[0]) != null) {
						System.out.println("Le noeud " + argv[0]
								+ " existe deja");
						System.exit(0);
					}
				} catch (NotBoundException e) {
					Naming.rebind(argv[0],
							new SiteImpl(Integer.parseInt(argv[0])));
				}
			}
			System.out.println("Le serveur est lancé !");
		} catch (Exception e) {
			System.err.println("Impossible de lancer le serveur" + e);
		}
	}
}
