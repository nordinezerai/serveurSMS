    package V1;

    import java.io.BufferedReader;
    import java.io.DataOutputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.net.InetAddress;
    import java.net.Socket;
    import java.util.Scanner;

    /**
     * Created by jepradat on 09/11/2017.
     */
    public class aClient {
        private static String myName = "NORDINE";
        private static BufferedReader inFromServeur = null;

        public aClient(String myName) {
            this.myName = myName;
        }

        public static DataOutputStream debut() throws IOException {
            //Création de la socket client et demande de connexion
            Socket s = new Socket(InetAddress.getLocalHost(), common.portService);

            //Préparation du message
            String mess = ":A:" + myName + ":\n";

            //Envoi du message au serveur principal
            DataOutputStream outToServer = new DataOutputStream(s.getOutputStream());
            outToServer.writeBytes(mess);

            //Lecture de la réponse du serveur
            inFromServeur = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String rep = inFromServeur.readLine();
            System.out.println("Reponse du Serveur : "+ rep);

            return outToServer;
        }

        public static void main(String[] args) throws IOException {
            DataOutputStream outToServer ;
            String message="";

            //Initialise le flux de sortie
            outToServer = debut();
            

            System.out.println("Bienvenue "+ myName +" ! \n \n");
            System.out.println("----- MENU ----- \n 1 : Chatter avec un abonné \n 2 : Se désabonner \n \n");
            System.out.println("Que souhaitez-vous faire ? (Indiquez le chiffre) : ");
            Scanner sn = new Scanner(System.in);
            int menuChoice = sn.nextInt();
            sn.close();
            
            readLineThread r = new readLineThread(inFromServeur);
            r.start();
            
            switch (menuChoice){
                case 1:
                    System.out.println("\n \n Entrez un message : ");
                    while(!message.equals("QUIT")) {
                        //Lecture du message du client
                        Scanner sc = new Scanner(System.in);
                        //Formatage du message
                        message = sc.nextLine().toUpperCase();
                        sc.close();
                        //Message à destination du service Envoi
                        String mess = new String(":E:"+message+":\n");
                        //Envoi du message au service Envoi
                        outToServer.writeBytes(mess);
                        System.out.println(mess);
                    }
                    System.out.println("\n \n Fin du programme");                   
                    break;

                case 2:
                    System.out.println("Vous vous appretez à vous désabonner: ");
                    outToServer.writeBytes("Desabonement\n");              
                    break;

                default:
                    System.out.println("Indiquez 1 ou 2 en fonction du menu souhaité");

            }
        }
    }
