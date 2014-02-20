package ftp;

/**
 * Classe representant les reponses du serveur FTP (sous forme de code +
 * message)
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class FtpAnswer {
	private int code;
	private String msg;

	/**
	 * Creer une instance de FtpAnswer
	 * 
	 * @param code
	 *            : le code de retour de la reponse du serveur FTP
	 * @param msg
	 *            : le message associe au code de retour de la reponse
	 */
	public FtpAnswer(int code, String msg) {
		this.code = code;
		this.msg = msg;
		if (Main.DEBUG_MODE)
			System.out.println(code +" "+msg);
	}

	/**
	 * 
	 * @return le code de retour de la reponse du serveur FTP
	 */
	public int getCode() {
		return code;
	}

	/**
	 * 
	 * @return le message de retour de la reponse du serveur FTP
	 */
	public String getMessage() {
		return msg;
	}

	/**
	 * Methode qui renvoies la reponse du serveur FTP compactee sous formes d'un
	 * tableau d'octets. Pratique pour transferer cette reponse par une socket
	 * 
	 * @return un tableau de bytes correspondant a la reponse du serveur FTP
	 */
	public byte[] getBytes() {
		String toSend = code + " " + msg + "\n";
		return toSend.getBytes();
	}

}
