/**
 * @author Maxime Chaste and Remy Francois
 * @version 1.0
 * @description Mise en pratique du RMI - Version en Arbre.
 * @date Avril 2014
 */
package arbre;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Diffuse un message vers un site placé en paramétre Utilisation :
 * "DiffuseurMessage 2 Bonjour" enverra le message "Bonjour" vers le Site 2 qui
 * le traitera.
 * 
 */
public class DiffuseurMessage {

	public static void main(String[] argv) throws NumberFormatException,
			UnsupportedEncodingException {
		try {
			SiteItf noeud = (SiteItf) Naming.lookup(argv[0]);
			Date d = new Date();
			noeud.propager(argv[1].getBytes(), Integer.parseInt(argv[0]),
					new Timestamp(d.getTime()));
			System.out.println("Envoi du message \"" + (argv[1])
					+ "\" au Site " + argv[0]);
		} catch (MalformedURLException e) {
			System.err
					.println("MalformedURLException : URL mal formee\nDescription de l'erreur : "
							+ e);
		} catch (RemoteException e) {
			System.err.println("RemoteException \nDescription de l'erreur : "
					+ e);
		} catch (NotBoundException e) {
			System.err
					.println("NotBoundException : Objet RMI non trouve\nDescription de l'erreur : "
							+ e);
		}
	}

}