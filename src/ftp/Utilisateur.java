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

	}

	public String getUsername() {
		return username;
	}

	public String getPwd() {
		return pwd;
	}

}
