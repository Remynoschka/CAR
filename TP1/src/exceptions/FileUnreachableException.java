package exceptions;

import ftp.FtpAnswer;

/**
 * Cette exception est levee lorsque l'on tente d'atteindre un fichier qui
 * n'existe pas
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class FileUnreachableException extends FtpException {
	private static final long serialVersionUID = 5308301867227553411L;

	public FileUnreachableException() {
		super(new FtpAnswer(550, "Fichier introuvable ou bien vous n'avez pas le droit de faire cela."));
	}

}
