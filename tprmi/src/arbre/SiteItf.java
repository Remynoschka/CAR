/**
 * @author Maxime Chaste and Remy Francois
 */
package arbre;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Timestamp;

/**
 * D�crit les fonctions necessaire pour l'impl�mentation d'un Site utilisant
 * RMI dans le cadre d'une topologie en arbre.
 * 
 */
public interface SiteItf extends Remote {

	/**
	 * Permet d'ajouter un fils au site en cours.
	 * 
	 * @param fils
	 *            le fils � ajouter au Site.
	 * @throws RemoteException
	 *             l'exception Remote.
	 */
	public void addFils(SiteItf fils) throws RemoteException;

	/**
	 * Propage un message vers les fils du noeuds.
	 * 
	 * @param message
	 *            le message � propager.
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
	 * Permet de r�cup�rer l'identifiant du site en cours.
	 * 
	 * @return l'identifiant du Site.
	 * @throws RemoteException
	 */
	public int getId() throws RemoteException;

}