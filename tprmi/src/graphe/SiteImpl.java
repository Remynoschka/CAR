/**
 * @author Maxime Chaste and Remy Francois
 */

package graphe;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Gère les voisins et l'envoi des messages reçu pour le Site en cours.
 * 
 */
@SuppressWarnings("serial")
public class SiteImpl extends UnicastRemoteObject implements SiteItf {

	private ArrayList<SiteItf> voisins;
	private int id;
	private ArrayList<Timestamp> tsTab;

	/**
	 * Constructeur de la classe SiteImpl version en Graphe.
	 * 
	 * @param id
	 *            l'identifiant du Site.
	 * @throws RemoteException
	 *             l'exception Remote.
	 */
	public SiteImpl(int id) throws RemoteException {
		super();
		this.id = id;
		this.voisins = new ArrayList<SiteItf>();
		this.tsTab = new ArrayList<Timestamp>();

	}

	/**
	 * Ajout un voisin à la liste des voisins du Site en cours.
	 */
	public void addVoisin(SiteItf voisin) throws RemoteException {
		if (!voisins.contains(voisin)) {
			System.out.println("le noeud n°" + id + " ajoute le voisin n°"
					+ voisin.getId());
			this.voisins.add(voisin);
		}
	}

	/**
	 * Permet de récupérer l'identifiant du site en cours.
	 * 
	 * @return l'identifiant du site.
	 */
	public int getId() throws RemoteException {
		return this.id;
	}

	/**
	 * Permet de propager un message vers les fils du noeuds.
	 * 
	 * @param message
	 *            le message Ã  propager.
	 * @param idEnvoyeur
	 *            le numero de l'envoyeur du message.
	 * @param ts
	 *            le TimeStamps.
	 * @throws RemoteException
	 *             l'exception Remote.
	 * @throws UnsupportedEncodingException
	 *             l'exception UnsupportedEncoding.
	 */
	public void propager(byte[] message, int idEnvoyeur, Timestamp ts)
			throws RemoteException, UnsupportedEncodingException {
		if (!tsTab.contains(ts)) {

			String t = new String(message, "Cp1252");
			String mess;

			if (idEnvoyeur == this.getId()) {
				mess = "Je suis le noeud " + this.getId()
						+ ".\nJ'envoie le message a : ";
			} else {
				mess = "Je suis le noeud " + this.getId() + ".\nJ'ai recu " + t
						+ " de " + idEnvoyeur + ".\nJe l'envoie aux voisins : ";
			}
			tsTab.add(ts);

			for (SiteItf voisin : this.voisins) {
				if (voisin.getId() != idEnvoyeur) {
					mess += voisin.getId() + ", ";
					voisin.propager(message, this.id, ts);
				}
			}
			System.out.println(mess);
		}
	}
}
