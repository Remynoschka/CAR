/**
 * @author Maxime Chaste and Remy Francois
 */
package arbre;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Gere les fils et l'envoi des messages re�u pour le Site en cours.
 * 
 */
@SuppressWarnings("serial")
public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private SiteItf pere;
	private ArrayList<SiteItf> fils;
	private int id;
	private ArrayList<Timestamp> tsTab;

	/**
	 * Constructeur de la classe SiteImpl version en Arbre.
	 * 
	 * @param id
	 *            l'identifiant du Site.
	 * @param pere
	 *            le num�ro du p�re.
	 * @throws RemoteException
	 *             l'exception Remote.
	 */
	public SiteImpl(int id, SiteItf pere) throws RemoteException {
		super();
		this.id = id;
		this.pere = pere;
		this.fils = new ArrayList<SiteItf>();
		this.tsTab = new ArrayList<Timestamp>();

	}

	/**
	 * Ajout un fils �la liste des fils du site en cours.
	 */
	public void addFils(SiteItf fils) throws RemoteException {
		System.out.println("le Fils " + fils.getId() + " est ajoute au noeud "
				+ this.id);
		this.fils.add(fils);
	}

	/**
	 * Permet de recuperer l'identifiant du site en cours.
	 * 
	 * @return l'identifiant du Site.
	 */
	public int getId() throws RemoteException {
		return this.id;
	}

	/**
	 * Permet de propager un message vers les fils du noeuds.
	 * 
	 * @param message
	 *            le message a propager.
	 * @param idEnvoyeur
	 *            le numero de l'envoyeur du message.
	 * @param ts
	 *            le TimeStamps.
	 * @return 
	 * @throws RemoteException
	 *             l'exception Remote.
	 * @throws UnsupportedEncodingException
	 *             l'exception UnsupportedEncoding.
	 */
	public  synchronized void propager(byte[] message, int idEnvoyeur, Timestamp ts)
			throws RemoteException, UnsupportedEncodingException {
		if (!tsTab.contains(ts)) {

			tsTab.add(ts);
			String t = new String(message);
			String mess = "";
			String enfants = "";
			String envoye = "\n" + "J'envoie le message a mes fils n�";

			mess += "Le message " + t + " est envoye par " + idEnvoyeur
					+ ". Je suis le noeud n°" + id + "\n";

			if (pere != null && idEnvoyeur != pere.getId()) {
				mess += ", Mon pere est le noeud n°" + pere.getId()
						+ " et je lui envoie le message ";

				if (this.fils.isEmpty()) {
					mess += "Mes enfants sont : \n";
					for (SiteItf enfant : this.fils) {
						enfants += enfant.getId() + ", ";
					}
				} else
					mess += "je n'ai pas d'enfant";

				pere.propager(message, this.id, ts);

			} else if (pere != null)
				mess += ", mon pere est le noeud n°" + pere.getId()
						+ ". Mes enfants sont : \n";
			else
				mess += ", je n'ai pas de pere. Mes enfants sont : \n";

			for (SiteItf enfant : this.fils) {

				enfants += enfant.getId() + ", ";
				if (enfant.getId() != idEnvoyeur) {
					envoye += enfant.getId() + ", ";
					enfant.propager(message, this.id, ts);
				}
			}
			System.out.println(mess + " " + enfants + " " + envoye);
		}
	}
}