package V1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jepradat on 09/11/2017.
 */
public class theMain {

    public static void main(String[] args) throws IOException {
        Annuaire Anu = new Annuaire();

        ServerSocket sockServer;
        sockServer = new ServerSocket(common.portService);
        
	    while(true) {
			//Attente d'une demande de connexion sur la socket d'accueil
	        Socket socketClient = sockServer.accept();

			//Création du flux d'entrée attaché à la socket
	        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

			//Création du flux en sortie attaché à la socket
	        DataOutputStream outToClient = new DataOutputStream(socketClient.getOutputStream());

			//Lecture des données arrivant du client
	        String mess = inFromClient.readLine();
	        System.out.println("Serveur P a recu "+ mess);

			//Extrait les informations nécéssaires pour le traitement du message recu par le client
	        String tMess[] = mess.split(":");
	        if (! tMess[1].equals("A")){
	            System.out.println("Erreur sur prop $" + tMess[1]+"$");
				//Envoi la réponse du serveur au client
	            outToClient.writeBytes("Error protocole\n");
	        }
	        else{
	            System.out.println("Demande Abonnement de "+tMess[2]);
				//Envoi la réponse du serveur au client
	            outToClient.writeBytes("OK\n");

				//Enregistre la socket associée au client dans la table des sockets de l'annuaire
	            Anu.putSocket(tMess[2], socketClient);

				//Démarre un nouveau thread
	            aThreadService t = new aThreadService(Anu, socketClient, tMess[2]);
	            t.start();
	            Anu.putThread(tMess[2],t);
	        }
        
        }

    }
}
