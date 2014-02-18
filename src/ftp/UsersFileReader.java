package ftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe servant a lire le fichier d'utilisateur
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class UsersFileReader {
	private File file;
	private BufferedReader reader;

	/**
	 * 
	 * @param file
	 *            : l'objet File du fichier contenant la liste des utilisateurs
	 * @throws FileNotFoundException
	 */
	public UsersFileReader(File file) throws FileNotFoundException {
		this.file = file;
		reader = new BufferedReader(new FileReader(file));
	}

	/**
	 * 
	 * @param path
	 *            : le chemin vers le fichier contenant la liste des
	 *            utilisateurs
	 * @throws FileNotFoundException
	 */
	public UsersFileReader(String path) throws FileNotFoundException {
		file = new File(path);
		reader = new BufferedReader(new FileReader(path));
	}

	/**
	 * Lit une ligne dans le fichier utilisateur et retourne l'identifiant et le
	 * mot de passe
	 * 
	 * @return String[0] = identifiant. String[1] = mot de passe
	 */
	public String[] readLine() {
		// ligne commencant par # = commentaire
		String ligne = "";
		try {
			ligne = reader.readLine();
			if(ligne == null){
				return null;
			}
			while (ligne.startsWith("#") || ligne.equals(""))
				ligne = reader.readLine();
		} catch (IOException e) {
			System.err.println("Erreur lors de la lecture du fichier d'utilisateurs");
			throw new RuntimeException(e);
		}
		return ligne.split(":");
	}
	/**
	 * 
	 * @return le File du fichier contenant la liste des utilisateurs
	 */
	public File getFile(){
		return file;
	}
}
