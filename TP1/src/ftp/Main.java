package ftp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Classe principale du programme
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class Main {
	public static final boolean DEBUG_MODE = true;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String directory_path = "";
		Scanner inputClavier = new Scanner(System.in);
		System.out.print("Repertoire des fichiers : ");
		directory_path = inputClavier.next(); // indication du repertoire
												// contenant les fichiers
		while (!Files.isDirectory(Paths.get("./" + directory_path),
				LinkOption.values())) { // test si repertoire existe
			System.err
					.print("Le chemin indique n'est pas un repertoire ou n'existe pas. Veuillez ressayer :");
			directory_path = inputClavier.next();
		}
		inputClavier.close();
		try {
			new Serveur(directory_path);
		} catch (IOException e) {
			System.err.println("Le serveur n'arrive pas a lire le repertoire des fichiers");
			throw new RuntimeException(e);
		}
	}

}
