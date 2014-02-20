package ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import exceptions.ArgumentManquantException;
import exceptions.CommandeInconnueException;
import exceptions.FileUnreachableException;
import exceptions.AuthentificationException;
import exceptions.TypeIncorrectException;

/**
 * Cette classe decrit un serveur FTP. Elle se charge d'accepter les connexions
 * des clients et de creer des Threads pour chaque client. Quand un client
 * envoies une commande, elle verifie quelle commande doit etre executee et
 * delegue cette execution a la classe FtpRequest
 * 
 * @author FRANCOIS Remy and DEMOL David
 * 
 */
public class Serveur {
	public static final int PORT = 2121;
	private File directory;
	private Map<String, Utilisateur> users;
	private ServerSocket serveur;

	/**
	 * 
	 * @param directory_path
	 *            : le chemin vers le dossier contenant le dossier contenant les
	 *            fichiers utilises par le client FTP
	 * @throws IOException
	 */
	public Serveur(String directory_path) throws IOException {
		// On lit le fichier contenant tout les utilisateurs
		users = new HashMap<String, Utilisateur>();
		UsersFileReader reader;
		try {
			reader = new UsersFileReader("./users");
			String[] line = reader.readLine();
			while (line != null) {
				users.put(line[0], new Utilisateur(line[0], line[1]));
				line = reader.readLine();
			}

			// On se place dans le repertoire des fichiers
			directory = new File(directory_path);
			serveur = new ServerSocket(PORT);
			while (true) {
				// Connexion d'un client
				Socket chaussette;
				chaussette = serveur.accept();
				new Thread(new FtpRequest(this, chaussette)).start();
			}
		} catch (FileNotFoundException e) {
			System.err.println("Fichier d'utilisateurs introuvable");
			System.out.println("Fermeture du serveur");
			serveur.close();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @return le repertoire contenant les fichiers utilises par le client FTP
	 */
	public File getFilesDirectory() {
		return directory;
	}

	/**
	 * 
	 * @return les utilisateus du serveur FTP
	 */
	public Map<String, Utilisateur> getUsers() {
		return users;
	}

	/**
	 * Execute une commande FTP et renvoie la reponse du serveur
	 * 
	 * @param command
	 *            : la commande FTP a executer
	 * @param args
	 *            : les arguments de la commande a execute (arguments a partir
	 *            de l'indice 1)
	 * @return une FtpAnswer qui est la reponse du serveur (code + message)
	 * @throws CommandeInconnueException
	 * @throws AuthentificationException
	 * @throws ArgumentManquantException
	 * @throws TypeIncorrectException
	 * @throws IOException
	 * @throws FileUnreachable
	 */
	public FtpAnswer performCommand(FtpRequest requete, String command,
			String[] args) throws CommandeInconnueException,
			AuthentificationException, ArgumentManquantException,
			TypeIncorrectException, FileUnreachableException, IOException {
		switch (command) {
		case "CDUP":
			return requete.processCDUP();
		case "CWD":
			return requete.processCWD(args[1]);
		case "LIST":
			requete.processLIST();
			return new FtpAnswer(250, "Liste des fichiers");
		case "MKD":
			if (requete.getUser().canWrite()) {
				if (requete.processMKD(args[1])) {
					return new FtpAnswer(250, "Repertoire " + args[1] + " cree");
				}
			}
			throw new FileUnreachableException();
		case "NLST":
			return new FtpAnswer(250, requete.processNLST());
		case "NOOP":
			return new FtpAnswer(200, "NOOP");
		case "PASS":
			if (args.length < 2) {
				throw new ArgumentManquantException();
			} else {
				if (requete.processPASS(args[1])) {
					return new FtpAnswer(230, "Connexion au serveur etablie");
				} else {
					throw new AuthentificationException();
				}
			}
		case "PASV":
			int portPASV = requete.processPASV();
			String ipTab[] = requete.getSocket().getLocalAddress()
					.getHostAddress().split("\\.");
			int port1 = portPASV / 256;
			int port2 = portPASV % 256;

			String toSend = ipTab[0] + "," + ipTab[1] + "," + ipTab[2] + ","
					+ ipTab[3] + "," + port1 + "," + port2;
			return new FtpAnswer(227, toSend);
		case "PORT":
			String[] values = args[1].split(",");
			requete.processPORT(Integer.parseInt(values[4]),
					Integer.parseInt(values[5]));
			requete.setMode(Mode.ACTIF);
			return new FtpAnswer(200, "");
		case "PWD":
			return new FtpAnswer(257, requete.processPWD());
		case "QUIT":
			requete.processQUIT();
			return new FtpAnswer(221, "Au revoir :)");
		case "RETR":
			requete.processRETR(args[1]);
			return new FtpAnswer(226, "Fichier recu");
		case "RMD":
			if (requete.getUser().canWrite()) {
				if (requete.processRMD(args[1])) {
					return new FtpAnswer(250, "Repertoire " + args[1]
							+ " supprime");
				}
			}
			throw new FileUnreachableException();
		case "STOR":
			if (requete.getUser().canWrite()) {
				requete.processSTOR(args[1]);
				return new FtpAnswer(226, "Fichier enregistre sur le serveur");
			} else {
				throw new FileUnreachableException();
			}
		case "SYST":
			return new FtpAnswer(215, requete.processSYST());
		case "TYPE":
			if (args.length < 2) {
				throw new ArgumentManquantException();
			} else {
				switch (args[1]) {
				case "I":
					requete.processTYPE(Type.BINARY);
					return new FtpAnswer(200, "Binary");
				case "A":
					requete.processTYPE(Type.ASCII);
					return new FtpAnswer(200, "ASCII");
				default:
					throw new TypeIncorrectException();
				}
			}
		case "USER":
			if (args.length < 2) {
				throw new ArgumentManquantException();
			} else {
				if (requete.processUSER(args[1])) {
					return new FtpAnswer(331, "Mot de passe ?");
				} else {
					throw new AuthentificationException();
				}
			}
		case "XMKD":
			if (requete.getUser().canWrite()) {
				if (requete.processMKD(args[1])) {
					return new FtpAnswer(250, "Repertoire " + args[1] + " cree");
				}
			}
			throw new FileUnreachableException();
		case "XPWD":
			return new FtpAnswer(257, requete.processPWD());
		case "XRMD":
			if (requete.getUser().canWrite()) {
				if (requete.processRMD(args[1])) {
					return new FtpAnswer(250, "Repertoire " + args[1]
							+ " supprime");
				}
			}
			throw new FileUnreachableException();
		default:
			throw new CommandeInconnueException(command);
		}
	}
}