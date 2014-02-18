package ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exceptions.FtpException;

/**
 * Cette classe represente une requette FTP. On y trouve les methodes executant
 * les commandes FTP possibles
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class FtpRequest implements Runnable {
	private Serveur serveur;
	private String username;
	private Socket socket;
	private File directory;
	boolean quit = false;

	public FtpRequest(Serveur serv, Socket socket) {
		this.serveur = serv;
		this.socket = socket;
		this.directory = serv.getFilesDirectory();
	}

	@Override
	public void run() {
		try {

			socket.getOutputStream().write(
					new FtpAnswer(220, "Bonjour a toi, jeune padawan")
							.getBytes());
			BufferedInputStream bis = new BufferedInputStream(
					socket.getInputStream());
			// boucle principale
			while (!quit) {
				byte[] buffer = new byte[32];
				bis.read(buffer);
				String recu = new String(buffer);
				// traitement
				if (recu.contains("\n"))
					recu = recu.substring(0, recu.indexOf('\n'));
				recu = recu.substring(0, recu.length() - 1);
				String[] command = recu.split(" ");
				if (Main.DEBUG_MODE)
					System.out.println(recu);

				FtpAnswer answer = serveur.performCommand(this, command[0],
						command);
				socket.getOutputStream().write(answer.getBytes());
			}
		} catch (IOException e) {
			System.err.println("Erreur d'ecriture socket: " + e.getMessage());
			throw new RuntimeException(e);
		} catch (FtpException e) {
			try {
				socket.getOutputStream().write(e.getAnswer().getBytes());
			} catch (IOException e1) {
				System.err.println("Erreur d'ecriture sur socket: "
						+ e1.getMessage());
				throw new RuntimeException(e);
			}
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Erreur de fermeture de serveur: "
						+ e.getMessage());
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Effectue la commande FTP USER
	 * 
	 * @param username
	 *            : le nom d'utilisateur
	 * @return true si l'username est connue par le serveur
	 */
	public boolean processUSER(String username) {
		this.username = username;
		return serveur.getUsers().containsKey(username);
	}

	/**
	 * Effectue la commande FTP PASS
	 */
	public boolean processPASS(String password) {
		return serveur.getUsers().get(username).getPwd().equals(password);
	}

	/**
	 * Effectue la commande FTP RETR
	 */
	public void processRETR() {
		// TODO RETR

	}

	/**
	 * Effectue la commande FTP STOR
	 */
	public void processSTOR() {
		// TODO STOR

	}

	/**
	 * Effectue la commande FTP LIST Sous UNIX, la commande affichera la liste
	 * des fichiers tel qu'une commande ls -l. <br/>
	 * Par exemple : -rw-r--r-- owner ownergroup 278 jan 14 13:37 monfichier<br/>
	 * <br/>
	 * Sous Windows, cela n'affichera que le nom du proprietaire, la date de
	 * derniere modification et le nom du fichier
	 */
	public String processLIST() {
		File[] files = directory.listFiles();
		String[] list = new String[files.length];
		for (int i = 0; i < list.length; i++) {

			String formatted = "";
			// formattage de la date
			Date last_modification = new Date(files[i].lastModified());
			String last_modification_string = last_modification.toString();
			last_modification_string = last_modification_string.substring(
					last_modification_string.indexOf(' ') + 1,
					last_modification_string.lastIndexOf(':'));

			formatted = last_modification_string + " " + formatted;
			// taille du fichier
			long taille = files[i].length();
			formatted = taille + " " + formatted;
			try {
				// groupe
				// Ne marche pas sous Windows
				if (!System.getProperty("os.name").toLowerCase()
						.startsWith("windows")) {
					String group = Files.getAttribute(files[i].toPath(),
							"posix:group", LinkOption.values()).toString();
					formatted = group + " " + formatted;
				}
				// proprietaire
				String proprietaire = Files.getOwner(files[i].toPath(),
						LinkOption.values()).toString();
				formatted = proprietaire + " " + formatted;
				// permissions
				if (!System.getProperty("os.name").toLowerCase()
						.startsWith("windows")) {
					List<PosixFilePermission> permissions = new ArrayList<PosixFilePermission>();
					// Ne marche pas sous Windows
					for (PosixFilePermission p : Files.getPosixFilePermissions(
							files[i].toPath(), LinkOption.values())) {
						permissions.add(p);
					}
					String permissions_string = "";
					for (PosixFilePermission type : PosixFilePermission
							.values()) {
						if (permissions.contains(type)) {
							if (type.toString().endsWith("READ"))
								permissions_string += "r";
							if (type.toString().endsWith("WRITE"))
								permissions_string += "w";
							if (type.toString().endsWith("EXECUTE"))
								permissions_string += "x";
						} else {
							permissions_string += "-";
						}
					}
					formatted = permissions_string + " " + formatted;
				}
				// formattage du nom de fichier
				if (!System.getProperty("os.name").toLowerCase()
						.startsWith("windows")) {
					formatted = (files[i].isDirectory()) ? "d" + formatted
							+ files[i].getName() + "/" : "-" + formatted
							+ files[i].getName();
				} else {
					formatted = (files[i].isDirectory()) ? formatted
							+ files[i].getName() + "/" : formatted
							+ files[i].getName();
				}
			} catch (IOException e) {
				System.err
						.println("Erreur de lecture du repertoire lors d'un LIST: "
								+ e.getMessage());
				throw new RuntimeException(e);
			}
			list[i] = formatted;
		}
		// mettre tout les fichiers l'un derriere l'autre
		String toReturn = "";
		for (String s : list) {
			toReturn = toReturn + s + "\n";
		}
		return toReturn;
	}

	/**
	 * Effectue la commande FTP QUIT
	 * 
	 */
	public void processQUIT() {
		// TODO QUIT
		quit = true;
	}

	/**
	 * Effectue le commande FTP SYST
	 * 
	 * @return le nom de l'OS sur lequel le serveur est execute
	 */
	public String processSYST() {
		return System.getProperty("os.name");
	}

	/**
	 * Execute la commande FTP PORT
	 * 
	 * @param a
	 *            : la 1ere valeur envoyee par le client
	 * @param b
	 *            : la 2e valeur envoyee par le client
	 * @return le port que doit utiliser le serveur (a*256 +b)
	 */
	public int processPORT(int a, int b) {
		return (a * 256) + b;
	}

	/**
	 * Execute le commande FTP NLST
	 * 
	 * @return la liste des fichiers contenu dans le repertoire
	 */
	public String processNLST() {
		String toReturn = "";
		for (File f : directory.listFiles()) {
			toReturn = (f.isDirectory()) ? toReturn + " " + f.getName() + "/"
					: toReturn + " " + f.getName();
		}
		return toReturn;
	}

	/**
	 * Execute la commande FTP PWD
	 * 
	 * @return
	 */
	public String processPWD() {
		// TODO Sans doute devoir ameliorer le PWD
		return "/" + directory.getPath();
	}

	/**
	 * Execute le commande FTP TYPE
	 * 
	 * @param type
	 *            : Le type envoye
	 */
	public void processTYPE(Type type) {

	}
}