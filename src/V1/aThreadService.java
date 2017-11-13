package V1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by jepradat on 09/11/2017.
 */
public class aThreadService extends Thread {
    private Annuaire myAnnuaire;
    private Socket mySocket;
    private String myClient;

    public aThreadService(Annuaire myAnnuaire, Socket mySocket, String myClient) {
        this.myAnnuaire = myAnnuaire;
        this.mySocket = mySocket;
        this.myClient = myClient;
    }

    public void run(){
        System.out.println("Demarrage Serveur service pour " + this.myClient);
        try {       	
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(mySocket.getOutputStream());
            String mess ="";
            String recipient="";


	        while(true) {
	            //Lecture du message recu
	            mess = inFromClient.readLine();
	            System.out.println("Serveur Service a recu "+ mess);

	            //Désabonnement choisi
	            if(mess.equals(":E:DESABONNEMENT:")) {
	                    //Suppresion des couples ID-Socket, ID-Thread de l'annuaire
                    try{
                        myAnnuaire.getTableT().remove(myClient);
                        myAnnuaire.getTableS().remove(myClient);
                        outToClient.writeBytes("Desabonnement ... Plus aucun contact peut vous joindre.\n");
                    }catch(IOException e){
                        e.printStackTrace();
                        outToClient.writeBytes("Désolé, une erreure est survenue au moment de votre suppression au sein de notre annuaire\n");
                    }

					break;	         
	            }

	            //Déconnexion souhaitée
	            if(mess.equals(":E:QUIT:")) {
	            		outToClient.writeBytes("Deconnexion ...\n");
					break;
	            }
	            
	            //On split la chaîne de caractère saisie pour récupérer les informations nécéssaires
                //[1] : E pour l'envoi des messages, [2] : Emetteur du message, [3]  Destinataire du message, [4] Message
	            String tMess[] = mess.split(":");
	            
	            
	            if (! tMess[1].equals("E")){
	                outToClient.writeBytes("Mauvais Format : Votre message doit être de la forme VOTRE_ID:ID_DESTINATAIRE:MESSAGE \n");
	            }
	            else {
                    //Détermine le destinataire à qui s'adresse le message
                    recipient = tMess[3].toUpperCase();

                    //Si le client spécifie "ALL", on envoie le message à tout les contacts présents dans la liste des contacts de l'annuaire
                    if(recipient.equals("ALL")){
                        //Récupère les contacts de l'annuaire
                        Hashtable<String, Socket> contacts = this.myAnnuaire.getTableS();

                        outToClient.writeBytes("Contacts : \n") ;

                        //Affiche les contacts dns un premier temps pour que le client
                        contacts.forEach((key, value) -> {
                            try {
                                outToClient.writeBytes(" - "+key+"\n \n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        //Envoi le message à chacun des contacts
                        contacts.forEach((key, value) -> {
                            try {
                                if(!(key.equals(tMess[2]))){
                                    //Récupère le socket du destinataire courant
                                    Socket recipientSocket = myAnnuaire.getScoket(key);

                                    //Initialise son flux de sortie
                                    DataOutputStream outToRecipient = new DataOutputStream(recipientSocket.getOutputStream());

                                    //Transmet le message au destinataire sur son flux de sortie
                                    outToRecipient.writeBytes(tMess[2]+" > "+key+" : "+tMess[4]+"\n");

                                    //Transmet le message au client pour qu'il est un visuel
                                    outToClient.writeBytes(tMess[2]+" > "+key+" : "+tMess[4]+"\n");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                    } else{ // Si l'utilisateur est spécifié, on envoie un message à ce dernier
                        //Gestion du cas ou le message n'est pas au bon format
                        try {
                            //Récupère le socket du destinataire
                            Socket recipientSocket = myAnnuaire.getScoket(recipient);

                            //Initialise son flux de sortie
                            DataOutputStream outToRecipient = new DataOutputStream(recipientSocket.getOutputStream());

                            //Transmet le message au destinataire sur son flux de sortie
                            outToRecipient.writeBytes(tMess[2] + " > " + tMess[3] + " : " + tMess[4] + "\n");

                            //Transmet le message au client pour l'historique
                            outToClient.writeBytes(tMess[2] + " > " + tMess[3] + " : " + tMess[4] + "\n");
                        }catch(NullPointerException e){
                            System.out.println("erreur de "+myClient+" : format non valide ou expediteur/destinataire non existant !");
                            outToClient.writeBytes("erreur : format non valide ou expediteur/destinataire non existant !\n");
                        }
                    }
	            }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}