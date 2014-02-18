package ftp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import exceptions.ArgumentManquantException;
import exceptions.CommandeInconnueException;
import exceptions.FtpException;
import exceptions.MotDePassIncorrectException;
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
				final Socket chaussette;

				chaussette = serveur.accept();
				new Thread(new Runnable() {
					FtpRequest requete = new FtpRequest(Serveur.this);

					@Override
					public void run() {
						try {
							boolean quit = false;
							chaussette.getOutputStream().write(
									new FtpAnswer(220,
											"Bonjour a toi, jeune padawan")
											.getBytes());
							BufferedInputStream bis = new BufferedInputStream(
									chaussette.getInputStream());
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
								FtpAnswer answer = Serveur.this.performCommand(
										requete, command[0], command);
								chaussette.getOutputStream().write(
										answer.getBytes());
							}
						} catch (IOException e) {
							System.err.println("Erreur d'ecriture socket: "
									+ e.getMessage());
							throw new RuntimeException(e);
						} catch (FtpException e) {
							try {
								chaussette.getOutputStream().write(
										e.getAnswer().getBytes());
							} catch (IOException e1) {
								System.err
										.println("Erreur d'ecriture sur socket: "
												+ e1.getMessage());
								throw new RuntimeException(e);
							}

						} finally {
							try {
								chaussette.close();
							} catch (IOException e) {
								System.err
										.println("Erreur de fermeture de serveur: "
												+ e.getMessage());
								throw new RuntimeException(e);
							}
						}

					}
				}).start();

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
	 */
	public FtpAnswer performCommand(FtpRequest requete, String command,
			String[] args) throws UtilisateurInconnuException,
			CommandeInconnueException, MotDePassIncorrectException,
			ArgumentManquantException {

		switch (command) {
		case "ABOR":

			break;
		case "ACCT":

			break;
		case "ADAT":

			break;
		case "ALLO":

			break;
		case "APPE":

			break;
		case "AUTH":

			break;
		case "CCC":

			break;
		case "CDUP":

			break;
		case "CONF":

			break;
		case "CWD":

			break;
		case "DELE":

			break;
		case "ENC":

			break;
		case "EPRT":

			break;
		case "EPSV":

			break;
		case "FEAT":

			break;
		case "HELP":

			break;
		case "LANG":

			break;
		case "LIST":
			return new FtpAnswer(212, requete.processLIST());
		case "LPRT":

			break;
		case "LPSV":

			break;
		case "MDTM":

			break;
		case "MIC":

			break;
		case "MKD":

			break;
		case "MLSD":

			break;
		case "MLST":

			break;
		case "MODE":

			break;
		case "NLST":
			return new FtpAnswer(212, requete.processNLST());
		case "NOOP":

			break;
		case "OPTS":

			break;
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

			break;
		case "PBSZ":

			break;
		case "PORT":
			// Attention, ici le port est dans le message fourni
			String[] values = args[1].split(",");
			return new FtpAnswer(200, ""
					+ requete.processPORT(Integer.parseInt(values[4]),
							Integer.parseInt(values[5])));
		case "PROT":

			break;
		case "PWD":

			break;
		case "QUIT":

			break;
		case "REIN":

			break;
		case "REST":

			break;
		case "RETR":

			break;
		case "RMD":

			break;
		case "RNFR":

			break;
		case "RNTO":

			break;
		case "SITE":

			break;
		case "SIZE":

			break;
		case "SMNT":

			break;
		case "STAT":

			break;
		case "STOR":

			break;
		case "STOU":

			break;
		case "STRU":

			break;
		case "SYST":
			return new FtpAnswer(215, requete.processSYST());
		case "TYPE":

			break;
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
		default:
			throw new CommandeInconnueException(command);
		}
		return new FtpAnswer(500, "");
	}
}
