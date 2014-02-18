package exceptions;

import ftp.FtpAnswer;

/**
 * Classe generique pour toute les exceptions specifiques au serveur FTP
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public abstract class FtpException extends Exception {
	private static final long serialVersionUID = 1L;
	private FtpAnswer answer;

	/**
	 * 
	 * @param answer
	 *            : La reponse FTP de l'erreur rencontree
	 */
	public FtpException(FtpAnswer answer) {
		this.answer = answer;
	}

	/**
	 * 
	 * @return la reponse du serveur FTP
	 */
	public FtpAnswer getAnswer() {
		return answer;
	}
}
