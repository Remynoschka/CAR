/**
 * @author Maxime Chaste and Remy Francois
 */
package arbre;

import java.rmi.Naming;
import java.rmi.NotBoundException;

/**
 * Crée les noeuds et ses fils et lance le serveur.
 * 
 */
public class SiteServeur {

	public static void main(String[] argv) {
		try {
			if (argv.length == 2) {
				SiteItf pere = (SiteItf) Naming.lookup(argv[1]);
				try {
					if (Naming.lookup(argv[0]) != null) {
						System.out.println("Le noeud " + argv[0]
								+ " existe deja");
						System.exit(0);
					}
				} catch (NotBoundException e) {
					SiteItf fils = new SiteImpl(Integer.parseInt(argv[0]), pere);
					pere.addFils(fils);
					Naming.rebind(argv[0], fils);
				}
			} else {
				try {
					if (Naming.lookup(argv[0]) != null) {
						System.out.println("Le noeud " + argv[0]
								+ " existe deja");
						System.exit(0);
					}
				} catch (NotBoundException e) {
					Naming.rebind(argv[0],
							new SiteImpl(Integer.parseInt(argv[0]), null));
				}
			}
			System.out.println("Le serveur est lancé !");
		} catch (Exception e) {
			System.err.println("Impossible de lancer le serveur" + e);
		}
	}
}