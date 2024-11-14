import java.io.*;
import java.net.*;

/* function
* 1. clientStart : connect IP address and port number from input.txt
*               if file miss, connect default server IP and server port number("localhost",1234)
* 2. readFile : read IP address and port number in input.txt file
* 3. connectToServer : connect to server, and receive quiz and send answer
*                   each quiz feedback from server and when quiz done,
*                    receive final score from server and display it
* 4. sendAnswer : send answer to server
* 5. receivingMessage : read buffer
* 6. closeConnection : close connect socket, PrintWriter, BufferedReader
*
* */
public class Socket_Client {

    // default IP and port number
    private static final String default_IP = "localhost";
    private static final int default_nport = 1234;
    private String serverIP;
    private int serverPort;
    private PrintWriter out;
    private BufferedReader in;
    Socket socket;

    public void clientStart(String filename) {
        serverIP = default_IP;
        serverPort = default_nport;
        readFile(filename);
        connectToServer();
    }
    private void readFile(String filename) {

        // read input.txt
        // if file miss, use default IP and port number
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                System.out.println("Reading line: " + line); // 각 줄을 출력하여 확인
                line = line.toUpperCase(); // case insensitive
                if (line.startsWith("IP:")) {
                    serverIP = line.split(":")[1].trim();
                } else if (line.startsWith("PORT NUMBER:")) {
                    serverPort = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            System.out.println("Successfully read IP and port from input.txt: " + serverIP + ", " + serverPort);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Using default IP and port number (localhost and 1234)");
        }
    }

    public void connectToServer() {
        try {
            Socket socket = new Socket(serverIP, serverPort);
             out = new PrintWriter(socket.getOutputStream(), true);
             in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connect to server (" + serverIP + ", " + serverPort + ")");

        } catch (IOException e) {
            System.out.println("Client error : " + e.getMessage());
        }
    }
    public void sendAnswer(String answer) {
        if (out != null) {
            out.println(answer);
        }
    }

    public String receiveMessage() {
        try {
            if (in != null) {
                return in.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
        }
        return null;
    }
    public void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

}
