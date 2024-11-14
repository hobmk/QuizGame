import javax.swing.*;
import java.awt.*;

/*
* function
* 1. connectToServer : invoke clientStart and start receive message with thread
* 2. receiveMessage : process the message and display user
* 3. sendAnswer : send nswer entered answerField
*
* content
* 1. questionArea : display question received by server
* 2. answerField : user input answer
* 3. connectButton : connect to server
* 4. submitButton : submit answer entered user
* 5. feedbackLabel : display feedback received by server
* 6. scoreLabel : display final score received by server
* 7. progressLabel : current question number / number of total questions
*
* */


public class Socket_Client_GUI extends JFrame {
    private final Socket_Client client;
    private JTextArea questionArea;
    private JTextField answerField;
    private JButton connectButton, submitButton;
    private JLabel feedbackLabel, scoreLabel, progressLabel;
    private ImageIcon icon;


    public Socket_Client_GUI() {
        client = new Socket_Client();
        setTitle("Quiz Game!");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main Layout
        setLayout(new BorderLayout(10, 10));

        icon = new ImageIcon("src/QUIZ.png");
        // Custom JPanel with background image
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this); // Stretch the image to fill the panel
            }
        };
        background.setLayout(new BorderLayout(10, 10));


        // Question Area
        questionArea = new JTextArea("Questions will appear here...");
        questionArea.setEditable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(new Font("Arial", Font.PLAIN, 20));
        questionArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(questionArea);

        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setOpaque(false);

        questionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        questionPanel.add(scrollPane, BorderLayout.CENTER);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(300, 200, 200, 200));


        // Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        // Answer Field
        JLabel guideLabel = new JLabel("  Enter your answer : ");
        answerField = new JTextField();
        answerField.setMargin(new Insets(5, 5, 5, 5));

        // Submit button
        submitButton = new JButton("Submit Answer");
        submitButton.setEnabled(false);
        inputPanel.add(guideLabel, BorderLayout.WEST);
        inputPanel.add(answerField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);


        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout(10,10));
        // Feedback, Score, Progress(question number)
        feedbackLabel = new JLabel("Let's go!");
        scoreLabel = new JLabel();
        progressLabel = new JLabel("Progress : 0/5");
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        statusPanel.add(feedbackLabel,BorderLayout.WEST);
        statusPanel.add(scoreLabel,BorderLayout.EAST);
        statusPanel.add(progressLabel, BorderLayout.SOUTH);


        // Connect Button
        connectButton = new JButton("Connect to Server");
        connectButton.setPreferredSize(new Dimension(200,50));
        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        connectPanel.add(connectButton,BorderLayout.CENTER);
        connectPanel.setOpaque(false);

        background.add(connectPanel,BorderLayout.CENTER);

        // If button is pressed, Remove panel include button
        connectButton.addActionListener(e -> {
            connectToServer();
            background.remove(connectPanel);
            setSize(1000,800);
            background.add(questionPanel, BorderLayout.CENTER);
            background.add(statusPanel, BorderLayout.NORTH);
            background.add(inputPanel, BorderLayout.SOUTH);

            revalidate();
            repaint();
        });

        add(background, BorderLayout.CENTER);

        // Press submit button ot Enter
        submitButton.addActionListener(e -> sendAnswer());
        answerField.addActionListener(e -> sendAnswer());

        setVisible(true);
    }

    private void connectToServer() {
        client.clientStart("src/input.txt");
        connectButton.setEnabled(false);
        submitButton.setEnabled(true);

        // Start receiving messages in a new thread
        new Thread(this::receiveMessages).start();
    }

    private void receiveMessages() {
        try {
            String message;
            int currentQuestion = 0;
            while ((message = client.receiveMessage()) != null) {
                if (message.startsWith("QUESTION:")) {
                    currentQuestion++;
                    questionArea.setText(message.substring(9)); // Remove "QUESTION:"
                    progressLabel.setText("Progress: " + currentQuestion + "/5");   // Total question number 5
                } else if (message.startsWith("FEEDBACK:")) {
                    feedbackLabel.setText(message.substring(9)); // Remove "FEEDBACK:"
                    feedbackLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                } else if (message.startsWith("COMPLETE:")) {
                    questionArea.setText(message.substring(9) + "\nCheck Your Final Score :)"); // Remove "COMPLETE:"
                    answerField.setEnabled(false);
                    submitButton.setEnabled(false);
                    scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
                    scoreLabel.setText(client.receiveMessage().substring(6)); // Remove "SCORE:"

                    client.closeConnection(); // Close connection
                    break;
                }
            }
        } catch (Exception e) {
            feedbackLabel.setText("Error receiving messages: " + e.getMessage());
        }
    }

    private void sendAnswer() {
        try {
            String answer = answerField.getText();
            client.sendAnswer(answer);
            answerField.setText("");    // After sending answer, Make answer field blank

        } catch (Exception e) {
            feedbackLabel.setText("Error sending answer: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Socket_Client_GUI();
    }
}
