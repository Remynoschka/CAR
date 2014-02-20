package ftp;

/**
 * Cette classe represente un utilisateur du serveur FTP
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class Utilisateur {
	private String username;
	private String pwd;
	private boolean readOnly = false;

	/**
	 * Creer une nouvelle instance d'utilisateur
	 * 
	 * @param name
	 *            : le nom de l'utilisateur
	 * @param pwd
	 *            : le mot de passe de l'utilisateur
	 */
	public Utilisateur(String name, String pwd) {
		this.username = name;
		this.pwd = pwd;
		if (name.equals("anonymous")) {
			readOnly = true;
		}
	}

	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @return le mot de passe de l'utilisateur
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * 
	 * @return true si l'utilisateur a des droits d'ecriture sur le serveur FTP
	 */
	public boolean canWrite() {
		return !readOnly;
	}
}
