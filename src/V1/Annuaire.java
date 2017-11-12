package V1;

import java.net.Socket;
import java.util.Hashtable;

/**
 * Created by jepradat on 09/11/2017.
 */
public class Annuaire {

    private Hashtable<String, Socket> tableS;
    private Hashtable<String, Thread> tableT;

    public Annuaire() {
        setTableS(new Hashtable<String, Socket>());
        tableT = new Hashtable<String, Thread>();
    }

    public Thread getThread(String name){
        return tableT.get(name);
    }
    public Socket getScoket(String name){
        return getTableS().get(name);
    }

    public void putSocket (String name, Socket s){
        getTableS().put(name,s);
    }
    public void putThread (String name, Thread t){
        tableT.put(name, t);
    }

    public Hashtable<String, Socket> getTableS() {
        return tableS;
    }

    public void setTableS(Hashtable<String, Socket> tableS) {
        this.tableS = tableS;
    }

	public Hashtable<String, Thread> getTableT() {
		return tableT;
	}

	public void setTableT(Hashtable<String, Thread> tableT) {
		this.tableT = tableT;
	}
    
}
