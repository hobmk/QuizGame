import java.io.*;
import java.net.*;

/* function
* 1. clientStart : connect IP address and port number from input.txt
*               if file miss, connect default server IP and server port number("localhost",1234)
* 2. readFile : read IP address and port number in input.txt file
* 3. connectToServer : connect to server, and receive quiz and send answer
*                   each quiz feedback from server and when quiz done,
*                    receive final score from server and display it
*

* */
public class Socket_Client {

    // default IP and port number
    private static final String default_IP = "localhost";
    private static final int default_nport = 1234;
    private String serverIP;
    private int serverPort;
    public static void main(String[] args) {
        new Socket_Client().clientStart();
    }

    public void clientStart() {
        serverIP = default_IP;
        serverPort = default_nport;
        readFile("/input.txt");
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

    private void connectToServer() {
        try (Socket socket = new Socket(serverIP, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connect to server (" + serverIP + ", " + serverPort + ")");

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                if (serverMessage.equals("Quiz complete.")) {
                    System.out.println(serverMessage);

                    String score = in.readLine();
                    System.out.println(score);
                    break;
                }
                System.out.println("Question: " + serverMessage);

                System.out.print("Your answer: ");
                String answer = userInput.readLine();
                out.println(answer);

                String feedback = in.readLine();
                System.out.println("Feedback: " + feedback);
            }

        } catch (IOException e) {
            System.out.println("Client error : " + e.getMessage());
        }
    }

}
