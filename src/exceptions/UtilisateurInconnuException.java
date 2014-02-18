package exceptions;

import ftp.FtpAnswer;

/**
 * Cette exception est levee lorsque l'utilisateur rentre un identifiant qui
 * n'existe pas. Code d'erreur 430
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class UtilisateurInconnuException extends FtpException {
	private static final long serialVersionUID = 5602988347401053654L;

	public UtilisateurInconnuException() {
		super(new FtpAnswer(430, "Nom d'utilisateur ou mot de passe invalide"));
	}

}
