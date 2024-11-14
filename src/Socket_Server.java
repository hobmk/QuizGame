import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/* function
 * 1. serverStart : open welcome socket and wait accept client socket,
 *                  if accept client socket, create new socket to communicate with it
 * 2. run : send Question, accept answer, and feedback each answer (Correct or Incorrect)
 *                        if Quiz done, final score send
 * 3. createQuestions : create questions and answer with array list
 * 4. Array List QUESTIONS : store created questions
 *
 * class
 * 1. Question : use getQuestion and getAnswer methods
 * 2. ClientHandler : Implement Runnable for supporting multi thread
 *
 * */
public class Socket_Server {
    private static final List<Question> QUESTIONS = createQuestions();
    private static final int PORT = 1234;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(5);
    public static void main(String[] args) {

        new Socket_Server().serverStart(PORT);

    }
    private static void serverStart(int nport) {
        try (ServerSocket welcomeSocket = new ServerSocket(nport)) {
            System.out.println("Server start..(port# = )" + nport + ")\n");

            while (true) {
                try {
                    Socket connecionSocket = welcomeSocket.accept();
                    System.out.println("Successful connection to client: " + connecionSocket.getInetAddress());

                    threadPool.execute(new ClientHandler(connecionSocket));

                } catch (IOException e) {
                    System.out.println("Client error : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error : " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket connectionSocket;

        public ClientHandler(Socket connectionSocket) {
            this.connectionSocket = connectionSocket;
        }

        @Override
        public void run(){

            try(Socket socket = connectionSocket;
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
                int score = 0;

                Collections.shuffle(QUESTIONS);

                for (int i = 0; i < 5; i++) {
                    Question question = QUESTIONS.get(i);
                    out.println("QUESTION:" + question.getQuestion());
                    String answer = in.readLine();

                    if (answer != null && answer.equalsIgnoreCase(question.getAnswer())) {
                        out.println("FEEDBACK:Correct");
                        score += 20;
                    } else {
                        out.println("FEEDBACK:Incorrect");
                    }
                }
                out.println("COMPLETE:Quiz complete.");
                out.println("SCORE:Your final score : " + score);

                System.out.println("Sending final score to client. score : " + score);

            } catch (IOException e){
                System.out.println("Client error : " + e.getMessage());
            }

        }
    }


    private static class Question {
        private String question;
        private String answer;

        public Question(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

        public String getQuestion(){
            return question;
        }
        public String getAnswer() {
            return answer;
        }
    }
    private static List<Question> createQuestions(){
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("q1","a1"));
        questions.add(new Question("q2", "a2"));
        questions.add(new Question("q3", "a3"));
        questions.add(new Question("q4", "a4"));
        questions.add(new Question("q5", "a5"));

        return questions;
    }

}
