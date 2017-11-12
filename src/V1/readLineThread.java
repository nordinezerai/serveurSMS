package V1;

import java.io.BufferedReader;
import java.io.IOException;

public class readLineThread extends Thread{
	
	private BufferedReader inFromServeur = null;
		
	public readLineThread(BufferedReader inFromServeur) {
		this.inFromServeur=inFromServeur;
	}
	
	public void run() {
		String msg="";
		while(true) {
			if(msg.equals("Deconnection")) break;
			try {		
				msg = inFromServeur.readLine();
				System.out.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
