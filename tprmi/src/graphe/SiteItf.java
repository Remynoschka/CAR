/**
 * @author Maxime Chaste and Remy Francois
 */
package graphe;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * Decrit les fonctions necessaire pour l'implémentation d'un Site sous controle
 * RMI.
 * 
 */
public interface SiteItf extends Remote {

	/**
	 * Permet d'ajouter un voisin au Site en cours.
	 * 
	 * @param voisin
	 *            le voision a ajouter au Site.
	 * @throws RemoteException
	 *             l'exception Remote.
	 */
	public void addVoisin(SiteItf voisin) throws RemoteException;

	/**
	 * Permet de propager un message vers les fils du noeuds.
	 * 
	 * @param message
	 *            le message à  propager.
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
			throws RemoteException, UnsupportedEncodingException;

	/**
	 * Permet de récupérer l'identifiant du site en cours.
	 * 
	 * @return l'identifiant du Site.
	 * @throws RemoteException
	 */
	public int getId() throws RemoteException;

}
