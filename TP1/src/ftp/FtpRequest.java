package ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import exceptions.AuthentificationException;
import exceptions.FileUnreachableException;
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
	private Utilisateur user;
	private Socket socket;
	private File directory;
	boolean quit = false;
	private Type type;
	private int dataPort;
	private ServerSocket serveurPassif;
	private Socket dataChannel;
	private Mode mode;

	public FtpRequest(Serveur serv, Socket socket) {
		this.serveur = serv;
		this.socket = socket;
		this.directory = serv.getFilesDirectory();
		mode = Mode.ACTIF;
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
				byte[] buffer = new byte[256];
				bis.read(buffer);
				String recu = new String(buffer);
				// traitement
				if (recu.contains("\n"))
					recu = recu.substring(0, recu.indexOf('\n'));
				recu = recu.substring(0, recu.length() - 1);
				String[] command = recu.split(" ", 2);
				if (Main.DEBUG_MODE)
					System.out.println(recu);

				FtpAnswer answer = serveur.performCommand(this, command[0],
						command);
				socket.getOutputStream().write(answer.getBytes());
			}
		} catch (IOException e) {
			System.err.println("Erreur d'ecriture socket: " + e.getMessage());
			throw new RuntimeException(e);
		} catch (AuthentificationException e) {
			try {
				System.err
						.println("Authentification invalide, fermeture de la connexion");
				socket.close();
			} catch (IOException e1) {
				System.err.println("Erreur de fermeture de la socket : "
						+ e.getMessage());
				throw new RuntimeException(e);
			}
		} catch (FtpException e) {
			try {
				socket.getOutputStream().write(e.getAnswer().getBytes());
			} catch (IOException e1) {
				System.err.println("Erreur d'ecriture sur la socket: "
						+ e1.getMessage());
				throw new RuntimeException(e);
			}
		}
	}

	public Socket getSocket() {
		return this.socket;
	}

	protected void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * 
	 * @return l'utilisateur de la requete FTP
	 */
	public Utilisateur getUser() {
		return this.user;
	}

	/**
	 * 
	 * @return si la connexion est en mode Acitf ou Passif
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Effectue la commande FTP USER
	 * 
	 * @param username
	 *            : le nom d'utilisateur
	 * @return true si l'username est connue par le serveur
	 */
	public boolean processUSER(String username) {
		this.user = serveur.getUsers().get(username);
		return serveur.getUsers().containsKey(username);
	}

	/**
	 * Effectue la commande FTP PASS
	 */
	public boolean processPASS(String password) {
		return serveur.getUsers().get(user.getUsername()).getPwd()
				.equals(password);
	}

	/**
	 * Effectue la commande FTP RETR
	 * 
	 * @throws IOException
	 */
	public void processRETR(String fichier) {
		// TODO Corriger RETR
		System.out.println(fichier);
		try {
			socket.getOutputStream().write(
					new FtpAnswer(150, "Les fichiers arrivent").getBytes());
			if (mode == Mode.PASSIF) {
				dataChannel = serveurPassif.accept();
			} else {
				dataChannel = new Socket(socket.getInetAddress(), dataPort);
			}
			OutputStream out = dataChannel.getOutputStream();

			FileInputStream fis = new FileInputStream(directory.getPath() + "/"
					+ fichier);
			int numberByte;
			byte[] data = new byte[2048];

			while ((numberByte = fis.read(data)) != -1) {
				out.write(data, 0, numberByte);
			}
			fis.close();
			out.close();
		} catch (IOException e) {
			System.err.println("Erreur lors de l'envoie de fichier : "
					+ e.getMessage());
			throw new RuntimeException(e);
		}

	}

	/**
	 * Effectue la commande FTP STOR
	 */
	public void processSTOR(String filename) {
		// TODO Corriger STOR

		try {
			socket.getOutputStream()
					.write(new FtpAnswer(150,
							"Les fichiers sont prets a etre envoyes")
							.getBytes());
			if (mode == Mode.PASSIF) {
				dataChannel = serveurPassif.accept();
			} else {
				dataChannel = new Socket(socket.getInetAddress(), dataPort);
			}
			InputStream in = dataChannel.getInputStream();
			// A modifier (pour le test)
			OutputStream out = new FileOutputStream(directory + "/" + filename);

			int numberByte;
			byte[] data = new byte[2048];

			while ((numberByte = in.read(data)) != -1) {
				out.write(data, 0, numberByte);
			}

			out.flush();
			out.close();
		} catch (IOException e) {
			System.err.println("Erreur lors du stockage du fichier : "
					+ e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Effectue la commande FTP LIST. Transmet au client la liste
	 * 
	 * @throws IOException
	 */
	public void processLIST() throws IOException {
		File[] files = directory.listFiles();
		String[] list = new String[files.length];
		socket.getOutputStream().write(
				new FtpAnswer(150, "La liste arrive").getBytes());
		// ouverture de socket
		if (mode == Mode.PASSIF) {
			dataChannel = serveurPassif.accept();
		} else {
			dataChannel = new Socket(socket.getInetAddress(), dataPort);
		}

		for (int i = 0; i < list.length; i++) {
			if (!files[i].isHidden()) {
				String filename = "";
				// String proprietaire = "";
				// formattage de la date
				double date = files[i].lastModified() / 1000.0;
				String last_modification_string = ",m" + date;
				// taille du fichier
				long taille = files[i].length();
				//
				// try {
				// // proprietaire
				// proprietaire = Files.getOwner(files[i].toPath(),
				// LinkOption.values()).toString();
				// // permissions
				// if (!System.getProperty("os.name").toLowerCase()
				// .startsWith("windows")) {
				// List<PosixFilePermission> permissions = new
				// ArrayList<PosixFilePermission>();
				// // Ne marche pas sous Windows
				// for (PosixFilePermission p : Files
				// .getPosixFilePermissions(files[i].toPath(),
				// LinkOption.values())) {
				// permissions.add(p);
				// }
				// String permissions_string = "";
				// for (PosixFilePermission type : PosixFilePermission
				// .values()) {
				// if (permissions.contains(type)) {
				// if (type.toString().endsWith("READ"))
				// permissions_string += "r";
				// if (type.toString().endsWith("WRITE"))
				// permissions_string += "w";
				// if (type.toString().endsWith("EXECUTE"))
				// permissions_string += "x";
				// } else {
				// permissions_string += "-";
				// }
				// }
				// }
				//
				// } catch (IOException e) {
				// System.err
				// .println("Erreur de lecture du repertoire lors d'un LIST: "
				// + e.getMessage());
				// throw new RuntimeException(e);
				// }
				//
				// nom de fichier
				filename = "\011" + files[i].getName();
				String fileDescription = (files[i].isDirectory()) ? "+/" : "+r"
						+ ",s" + taille;
				list[i] = fileDescription + last_modification_string + filename
						+ "\015\012";
			}
		}
		// ecrire tout les fichiers
		for (String s : list) {
			dataChannel.getOutputStream().write(
					new String(s + "\r\n").getBytes());
		}
		dataChannel.close();

	}

	/**
	 * Effectue la commande FTP QUIT
	 * 
	 * @throws IOException
	 * 
	 */
	public void processQUIT() throws IOException {
		// TODO Ameliorer QUIT
		dataChannel.close();
		serveurPassif.close();
		socket.close();
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
		this.dataPort = (a * 256) + b;
		return dataPort;
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
	 * Execute la commande FTP CWD
	 * 
	 * @param path
	 *            : le chemin vers le repertoire de destination
	 * @return la reponse FTP renvoye par la commande
	 * @throws IOException
	 * @throws FileUnreachableException
	 */
	public FtpAnswer processCWD(String path) throws IOException,
			FileUnreachableException {

		if (path.equals("/")
				|| path.equals("/" + serveur.getFilesDirectory().getName())) {
			directory = serveur.getFilesDirectory();
			return new FtpAnswer(250, "Le nouveau repertoire est " + path);
		} else {

			String fileSeparator = (processSYST().toLowerCase()
					.startsWith("win")) ? "\\" : "/";
			for (File f : directory.listFiles()) {
				if (f.getCanonicalPath().equals(
						directory.getCanonicalPath() + fileSeparator + path)
						&& f.isDirectory()) {
					directory = f;
					return new FtpAnswer(250, "Le nouveau repertoire est "
							+ path);
				}
			}
			throw new FileUnreachableException();
		}
	}

	/**
	 * Execute la commande FTP CDUP
	 * 
	 * @throws FileUnreachableException
	 */
	public FtpAnswer processCDUP() throws FileUnreachableException {
		if (!directory.getPath().equals(serveur.getFilesDirectory().getPath())) {
			System.out.println("cdup");
			directory = new File(directory.getParent());
			return new FtpAnswer(250, "Retour au repertoire /"
					+ directory.getPath());
		}
		throw new FileUnreachableException();
	}

	/**
	 * Execute la commande FTP PWD
	 * 
	 * @return le chemin du repertoire courant
	 */
	public String processPWD() {
		return "/" + directory.getPath().replace('\\', '/');
	}

	/**
	 * Execute le commande FTP TYPE
	 * 
	 * @param type
	 *            : Le type envoye
	 */
	public void processTYPE(Type type) {
		this.type = type;
	}

	/**
	 * Execute la commande FTP PASV
	 * 
	 * @return le port genere par PASV
	 */
	public int processPASV() {
		mode = Mode.PASSIF;
		try {
			serveurPassif = new ServerSocket(0);
			dataPort = serveurPassif.getLocalPort();
		} catch (IOException e) {
			System.err
					.println("Impossible d'ouvrir le serveur de donnees en passif");
			throw new RuntimeException(e);
		}
		return dataPort;
	}

	/**
	 * Execute la commande FTP MKD
	 * 
	 * @param pathname
	 *            : le chemin du repertoire a creer
	 * @return true si le repertoire a bien ete cree, false sinon
	 */
	public boolean processMKD(String pathname) {
		File newDir = new File(directory + "/" + pathname);
		return newDir.mkdir();
	}

	/**
	 * Execute la commande FTP RMD
	 * 
	 * @param name
	 *            : le nom du repertoire a supprimer
	 * @return true si le repertoire a bien ete supprime, false sinon
	 */
	public boolean processRMD(String name) {
		File toDelete = new File(directory.getPath() + "/" + name);
		if (toDelete.exists()) {
			toDelete.delete();
			return true;
		}
		return false;
	}
}