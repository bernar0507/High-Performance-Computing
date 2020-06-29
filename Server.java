// Java implementation of  Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// Server class 
public class Server {

    public static void main(String[] args) throws IOException {
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(59995);

        // running infinite loop for getting 
        // client request 
        while (true) {
            Socket s = null;

            try {
                // socket object to receive incoming client requests 
                s = ss.accept();

                System.out.println("A new client is connected : " + s);

                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Assigning new thread for this client");

                // create a new thread object 
                Thread t = new ClientHandler(s, dis, dos);

                // Invoking the start() method 
                t.start();

            } catch (Exception e) {
                s.close();
                e.printStackTrace();
            }
        }
    }
}

// ClientHandler class 
class ClientHandler extends Thread {

    DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd");
    DateFormat fortime = new SimpleDateFormat("hh:mm:ss");
    final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;

    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
    }

    @Override
    public void run() {
        String received;
        String toreturn;
        String msg = menu();
        Boolean isConnected = true;
        Boolean specs = false;
        while (isConnected) {
            try {

                // Ask user what he wants 
                dos.writeUTF(msg);

                // receive the answer from client 
                received = dis.readUTF();
                System.out.println("Recebido do cliente: " + received);
                if (specs && received.length() > 20) {
                    received = received.substring(20);
                    int posNL = received.indexOf("\n");
                    String totalRAM = received.substring(0, posNL);
                    long memoriaFisica = Long.parseLong(totalRAM);
                    float memFGigas = (float) memoriaFisica / 1024 / 1024 / 1024;
                    String freeRAM = received.substring(posNL);
                    freeRAM = freeRAM.substring(20,freeRAM.length()-1);
                    float memLGigas = (float) Long.parseLong(freeRAM)/1024/1024;
                    dos.writeUTF("Memoria total do seu computador: " + memFGigas + " Gigas\n"
                    + "Memoria livre do seu computador: " + memLGigas + " Gigas\n"
                    + "Percentagem de memoria livre: " + Math.round(memLGigas/memFGigas*100) + "%");
                    specs = false;
                    msg = menu();
                } else {

                    if (received.equals("Sair")) {
                        System.out.println("Cliente " + this.s + " enviou sair...");
                        System.out.println("Encerrando esta conexao.");
                        this.s.close();
                        System.out.println("Conexao encerrada");
                        break;
                    }

                    // creating Date object 
                    Date date = new Date();

                    // write on output stream based on the 
                    // answer from the client 
                    switch (received) {

                        case "Data":
                            toreturn = fordate.format(date);
                            dos.writeUTF(toreturn);
                            msg = menu();
                            break;

                        case "Hora":
                            toreturn = fortime.format(date);
                            dos.writeUTF(toreturn);
                            msg = menu();
                            break;

                        case "MySpecs":
                            specs = true;
                            dos.writeUTF("");
                            msg = "COMANDO#wmic ComputerSystem get TotalPhysicalMemory /value "
                                    + "&& wmic OS get FreePhysicalMemory /value";
                            break;

                        default:
                            dos.writeUTF("Invalid input");
                            msg = menu();
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
            }
        }
        try {
            // closing resources 
            if (!this.s.isClosed()) {
                this.s.close();
            }
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String menu() {
        return "\nO que deseja?\nData - O servidor envia a data\n"
                + "Hora - O servidor envia a hora\n"
                + "MySpecs - O servidor interroga seu computador e reporta uso de recursos\n"
                + "Sair - para terminar a conexao.";
    }

    void getAndReportClientSpecs(DataInputStream dis, DataOutputStream dos) {
    }
}
