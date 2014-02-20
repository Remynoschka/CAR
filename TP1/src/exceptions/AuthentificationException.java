package exceptions;

import ftp.FtpAnswer;

/**
 * Cette exception est levee quand l'utilisateur rentre un mot de passe
 * invalide. Code d'erreur 430
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class AuthentificationException extends FtpException {

	public AuthentificationException() {
		super(new FtpAnswer(430, "Nom d'utilisateur ou mot de passe invalide"));
	}

	private static final long serialVersionUID = 259492506392288432L;

}
