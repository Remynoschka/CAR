package exceptions;

import ftp.FtpAnswer;

/**
 * Exception levee quand le type fournie par la commande TYPE n'est pas I ou A
 * @author FRANCOIS Remy and DEMOL David
 *
 */
public class TypeIncorrectException extends FtpException {

	private static final long serialVersionUID = 6409964968487791217L;

	public TypeIncorrectException() {
		super(
				new FtpAnswer(501,
						"Le type fourni n'est pas valide, I pour Binaire et A pour ASCII"));
	}

}
