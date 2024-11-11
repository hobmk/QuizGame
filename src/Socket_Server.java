import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Socket_Server {
    public static void main(String[] args) throws IOException{
        ServerSocket welcomeSocket;
        String clientSentence;
        String capitalizedSentence;
        int nport;

        nport = 6789;

        welcomeSocket = new ServerSocket(nport);
        System.out.println("Server start... (port# = )" + nport + ")\n");
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            clientSentence = inFromClient.readLine();
            System.out.println("From Client : " + clientSentence);
            capitalizedSentence = clientSentence.toUpperCase() + '\n';
            outToClient.writeBytes(capitalizedSentence);
        }
    }
}
