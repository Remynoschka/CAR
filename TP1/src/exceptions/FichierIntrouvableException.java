package exceptions;

import ftp.FtpAnswer;

/**
 * Cette exception est levee lorsque l'on tente d'atteindre un fichier qui
 * n'existe pas
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class FichierIntrouvableException extends FtpException {
	private static final long serialVersionUID = 5308301867227553411L;

	public FichierIntrouvableException() {
		super(new FtpAnswer(550, "Fichier introuvable"));
	}

}
