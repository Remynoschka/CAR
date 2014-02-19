package exceptions;

import ftp.FtpAnswer;

/**
 * Excpetion levee quand il manque un argument a une commande
 * @author FRANCOIS Remy and DEMOL David
 *
 */
public class ArgumentManquantException extends FtpException {
	private static final long serialVersionUID = 7819349664497725970L;

	public ArgumentManquantException() {
		super(new FtpAnswer(501, "Une information est manquante pour executer la commande"));
	}

}
