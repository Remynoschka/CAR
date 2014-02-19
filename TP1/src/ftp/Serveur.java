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
import exceptions.FichierIntrouvableException;
import exceptions.MotDePassIncorrectException;
import exceptions.TypeIncorrectException;
import exceptions.UtilisateurInconnuException;

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
			System.err
					.println("Probleme lors de la lecture du fichiers d'utilisateurs");
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			System.out.println("Fermeture du serveur");
			serveur.close();
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
	 * @throws UtilisateurInconnuException
	 * @throws CommandeInconnueException
	 * @throws MotDePassIncorrectException
	 * @throws ArgumentManquantException
	 * @throws TypeIncorrectException 
	 * @throws IOException 
	 * @throws FichierIntrouvableException 
	 */
	public FtpAnswer performCommand(FtpRequest requete, String command,
			String[] args) throws UtilisateurInconnuException,
			CommandeInconnueException, MotDePassIncorrectException,
			ArgumentManquantException, TypeIncorrectException, FichierIntrouvableException, IOException {

		switch (command) {
		case "CDUP":

			break;

		case "CWD":
			return requete.processCWD(args[1]);
		case "DELE":

			break;
		case "LIST":
			return new FtpAnswer(250, requete.processLIST());
		case "LPRT":

			break;
		case "MKD":
			// TODO Appeler processMKD
			break;
		case "MLSD":
			
			break;
		case "NLST":
			return new FtpAnswer(250, requete.processNLST());
		case "NOOP":
			return new FtpAnswer(200, "NOOP");
		case "PASS":
			if (args.length < 2) {
				throw new ArgumentManquantException();
			} else {
				if (requete.processPASS(args[1])) {
					return new FtpAnswer(230,
							"Tu as maintenant acces a tout tes fichiers MODAFOCKA !!!");
				} else {
					throw new MotDePassIncorrectException();
				}
			}
		case "PASV":
			requete.processPASV();
			return new FtpAnswer(227, "Entree en mode passif");
		case "PORT":
			// Attention, ici le port est dans le message fourni
			String[] values = args[1].split(",");
			return new FtpAnswer(200, ""
					+ requete.processPORT(Integer.parseInt(values[4]),
							Integer.parseInt(values[5])));
		case "PWD":
			return new FtpAnswer(257, requete.processPWD());
		case "QUIT":
			requete.processQUIT();
			return new FtpAnswer(221, "Au revoir :)");
		case "RMD":
			// TODO Appeler processRMD
			break;
		case "RNTO":

			break;
		case "STOR":
			return new FtpAnswer(250, "STORRRR");//TODO Changer repose serveur apres STOR
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
				case "A" :
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
					return new FtpAnswer(331, "Wesh t'as pas mot de passe ?");
				} else {
					throw new UtilisateurInconnuException();
				}
			}
		case "XCUP":
		case "XMKD":
			// TODO Appeler processMKD
		case "XPWD":
			return new FtpAnswer(257, requete.processPWD());
		case "XRMD":
			// TODO Appeler processRMD
		default:
			throw new CommandeInconnueException(command);
		}
		return new FtpAnswer(500, "");
	}
}