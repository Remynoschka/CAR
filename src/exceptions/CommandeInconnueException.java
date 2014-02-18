package exceptions;

import ftp.FtpAnswer;
/**
 * Cette exception est levee lorsque la commande FTP demandee n'est pas prise en charge par le serveur
 * @author FRANCOIS Remy and DEMOL David
 *
 */
public class CommandeInconnueException extends FtpException {

	private static final long serialVersionUID = 2939440007549810010L;

	public CommandeInconnueException(String commande) {
		super(new FtpAnswer(502, "La commande " + commande + " n'existe pas"));
	}

}
