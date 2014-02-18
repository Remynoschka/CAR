package exceptions;

import ftp.FtpAnswer;

public class TypeIncorrectException extends FtpException {

	public TypeIncorrectException() {
		super(
				new FtpAnswer(501,
						"Le type fourni n'est pas valide, I pour Binaire et A pour ASCII"));
	}

}
