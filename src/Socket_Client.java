import java.io.*;
import java.net.*;

public class Socket_Client {
    public static void main(String[] args) throws Exception{
        String sentence;
        String modifiedSentence;
        String serverIP = "127.0.0.1";
        int nport = 6789;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket(serverIP, nport);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');
        modifiedSentence = inFromServer.readLine();
        System.out.println("From Server : " + modifiedSentence);
        clientSocket.close();
    }
}
