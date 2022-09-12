import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPanel extends JPanel {

    private ChromeDriver driver;

    private JButton whatsappButton;
    private JButton report;
    private JButton approveButton;

    private JTextField textToSend;
    private JTextArea phoneNumber;

    private JLabel messages;

    private final Font buttonFont;
    private final Font textFont;
    private final Font messagesFont;

    private ArrayList<PhoneNumber> correctPhoneNumbers;

    private boolean whatsappButtonClicked;

    private String reportMessageText;

    private PhoneNumber currentPhoneNumber;


    private boolean notFirst;

    public MainPanel(int x, int y, int width, int height) {
        this.setLayout(null);
        this.setBounds(x, y, width, height);
        this.setBackground(Color.PINK);


        this.buttonFont = new Font("David", Font.BOLD, Constants.BUTTON_FONT_SIZE);
        this.textFont = new Font("David", Font.ITALIC, Constants.TEXT_FONT_SIZE);
        this.messagesFont = new Font("David", Font.ITALIC, Constants.MESSAGE_FONT_SIZE);

        buildPanel();

        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\tehil\\Downloads\\chromedriver_win32 (1)\\chromedriver.exe");


        this.whatsappButton.addActionListener((e) -> {
            this.reportMessageText = "";


            this.whatsappButtonClicked = true;
        });

        this.report.addActionListener((e) -> {
            String reportText = "";

            if (this.correctPhoneNumbers != null) {

                for (PhoneNumber correctPhoneNumber : correctPhoneNumbers) {
                    reportText = correctPhoneNumber.toString();
                    reportText += "\n" + "\n";
                }
            }
            writeToFile(reportText);
        });

        new Thread(() -> {
            try {
                while (true) {

                    if (this.whatsappButtonClicked) {
                        correctPhoneNumbers = new ArrayList<>();

                        this.messages.setForeground(Color.BLACK);
                        String[] phoneNumbers = this.phoneNumber.getText().split("\\n");
                        int counter = 0;
                        for (String number : phoneNumbers) {
                            int currentNumber = checkPhoneNumber(number);
                            if (currentNumber != 0) {
                                correctPhoneNumbers.add(new PhoneNumber("0" + currentNumber));
                                counter++;
                            }
                        }

                        this.messages.setText("You entered " + phoneNumbers.length + " recipient numbers, of which " + counter + " are valid cell phone numbers.");


                        if (counter != 0) {
                            buildQuestionPanel();

                            approveButton.addActionListener((e) -> {
                                sendingMessages();

//                                new Thread(() -> {
//                                    try {
//                                        while (true) {
//                                            for (int i = 0; i < correctPhoneNumbers.size(); i++) {
//                                                this.currentPhoneNumber = correctPhoneNumbers.get(i);
//                                                checkRespondMessage();
//                                            }
//
//                                            Thread.sleep(1);
//                                        }
//                                    } catch (InterruptedException exception) {
//                                        exception.printStackTrace();
//                                    }
//                                }).start();

                            });
                        }

                        this.whatsappButtonClicked = false;
                    }
                    Thread.sleep(10000);
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();
    }

    private void sendingMessages() {
        for (int i = 0; i < correctPhoneNumbers.size(); i++) {
            this.currentPhoneNumber = correctPhoneNumbers.get(i);

            if (!this.textToSend.getText().equals("")) {
                this.reportMessageText = this.textToSend.getText();
                if (i == 0) {
                    this.driver = new ChromeDriver();
                }

                this.driver.get(Constants.WEB_WHATSAPP_ADDRESS + this.currentPhoneNumber.getPhoneNumber());


                if (i == 0) {
                    loginCheck();
                }

                sendMessage();
                System.out.println("sent");
//
//                if (this.currentPhoneNumber.isExistInWhatsapp()) {
//                    updateMessageStatus();
//                }
            } else {
                this.messages.setText("no message");
            }
        }
    }

    private void buildPanel() {
        this.whatsappButton = new JButton();
        this.whatsappButton.setBounds((Constants.WINDOW_WIDTH - Constants.BUTTON_WIDTH) / 2, (Constants.WINDOW_HEIGHT / 3) * 2, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        this.whatsappButton.setText("Web Whatsapp");
        this.whatsappButton.setFont(this.buttonFont);
        this.add(this.whatsappButton);

        this.messages = new JLabel();
        this.messages.setBounds((this.whatsappButton.getX() - (Constants.CONNECTED_TEXT_WIDTH - this.whatsappButton.getWidth()) * 7),
                this.whatsappButton.getY() + this.whatsappButton.getHeight() + Constants.SPACE, Constants.CONNECTED_TEXT_WIDTH * 4, this.whatsappButton.getHeight() / 2);
        this.messages.setFont(this.messagesFont);
        this.messages.setForeground(Color.BLACK);
        this.messages.setHorizontalAlignment(JLabel.CENTER);
        this.add(this.messages);

        this.textToSend = new JTextField();
        this.textToSend.setBounds((this.whatsappButton.getX() - (Constants.TEXT_FIELD_WIDTH - this.whatsappButton.getWidth()) / 2), this.whatsappButton.getY() - (this.whatsappButton.getHeight()), Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
        this.add(this.textToSend);

        JLabel textLabel = new JLabel("Enter text:");
        textLabel.setBounds(this.textToSend.getX() - this.textToSend.getWidth() - Constants.SPACE * 3, this.textToSend.getY(), Constants.TEXT_FIELD_WIDTH * 2, Constants.TEXT_FIELD_HEIGHT);
        textLabel.setFont(this.textFont);
        this.add(textLabel);

        this.phoneNumber = new JTextArea();
        this.phoneNumber.setBounds((this.whatsappButton.getX() - (Constants.TEXT_FIELD_WIDTH - this.whatsappButton.getWidth()) / 2), this.textToSend.getY() - (this.textToSend.getHeight() * 8), Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT * 7);
        this.add(this.phoneNumber);

        JLabel phoneNumberLabel = new JLabel("Enter phone number:");
        phoneNumberLabel.setBounds(this.phoneNumber.getX() - this.phoneNumber.getWidth() - Constants.SPACE * 3, this.phoneNumber.getY(), Constants.TEXT_FIELD_WIDTH * 2, Constants.TEXT_FIELD_HEIGHT);
        phoneNumberLabel.setFont(this.textFont);
        this.add(phoneNumberLabel);

        this.report = new JButton("Create a report");
        this.report.setBounds((Constants.WINDOW_WIDTH - Constants.BUTTON_WIDTH) - Constants.SPACE * 2, Constants.SPACE, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        this.report.setFont(this.buttonFont);
        this.add(this.report);

    }

    private void buildQuestionPanel() {
        JLabel questionLabel = new JLabel();
        questionLabel.setBounds(this.messages.getX() * 3, this.messages.getY() + this.messages.getHeight(), this.messages.getWidth() / 2, this.messages.getHeight());
        questionLabel.setForeground(Color.BLACK);
        questionLabel.setFont(this.messagesFont);
        this.add(questionLabel);
        questionLabel.setHorizontalTextPosition(JLabel.CENTER);
        questionLabel.setText("Whether to send the message?");

        JButton cancelButton = new JButton();
        cancelButton.setBounds(questionLabel.getX() + questionLabel.getWidth(), questionLabel.getY(), questionLabel.getWidth() / 3, questionLabel.getHeight());
        cancelButton.setText("Cancel");
        cancelButton.setFont(this.buttonFont);
        this.add(cancelButton);


        this.approveButton = new JButton();
        this.approveButton.setBounds(cancelButton.getX() + cancelButton.getWidth() + Constants.SPACE, cancelButton.getY(), cancelButton.getWidth(), cancelButton.getHeight());
        this.approveButton.setText("Approve");
        this.approveButton.setFont(this.buttonFont);
        this.add(approveButton);

        cancelButton.addActionListener((e) -> {
            this.messages.setText("Enter phone numbers: ");
            questionLabel.setText("");
        });
    }

    private int checkPhoneNumber(String phoneNumberString) {
        int phoneNumberInt = 0;
        if (phoneNumberString.equals("")) {
            messages.setText("There is no phone number");
        } else if (phoneNumberString.length() != 10) {
            messages.setText("Invalid input length");
        } else if (!phoneNumberString.startsWith("05")) {
            messages.setText("Area code incorrect");
        } else {
            try {
                phoneNumberInt = Integer.parseInt(phoneNumberString);
            } catch (NumberFormatException exception) {
                messages.setText("Invalid input");
            }
        }
        return phoneNumberInt;
    }

    private void loginCheck() {
        boolean isConnected = false;
        while (!isConnected) {
            try {
                isConnected = (this.driver.findElement(By.id("side")).isDisplayed());
            } catch (NoSuchElementException exception) {
            }
        }
        messages.setText("You are connected!");
    }

    private void sendMessage() {
        boolean isElementExist = false;
        WebElement textBox = null;
        while (!isElementExist) {
            try {
                textBox = (this.driver.findElement(By.cssSelector("div[title=\"Type a message\"]")));
                isElementExist = true;
                this.currentPhoneNumber.setExistInWhatsapp(true);
            } catch (NoSuchElementException exception) {

                try {
                    WebElement okButton = (this.driver.findElement(By.cssSelector("div[data-testid=\"popup-controls-ok\"]")));
                    if (okButton != null) {
                        okButton.click();
                        messages.setText("Invalid telephone number");
                        isElementExist = true;
                        this.currentPhoneNumber.setExistInWhatsapp(false);
                    }
                } catch (NoSuchElementException e) {
                }
            } catch (UnhandledAlertException e) {
            }
        }
        if (this.currentPhoneNumber.isExistInWhatsapp()) {
            textBox.sendKeys(this.textToSend.getText());

            clickOnSend();
            messages.setText("The message was sent successfully!");
            System.out.println("The message was sent successfully!");
        }
    }

    private void updateMessageStatus() {
        List<WebElement> sentMessagesList = null;
        boolean isSentMessagesExist = false;
        while (!isSentMessagesExist) {
            try {
                sentMessagesList = this.driver.findElements(By.cssSelector("span[aria-label=\" Pending \"]"));
                isSentMessagesExist = true;
            } catch (NoSuchElementException exception) {
            }
        }
        WebElement lastMessageStatus = sentMessagesList.get(sentMessagesList.size() - 1);


        String messageStatus = lastMessageStatus.getAttribute("aria-label");

        while (currentPhoneNumber.getMessageStatus() == Constants.SENT_STATUS) {
            try {
                String currentMessageStatus;
                currentMessageStatus = lastMessageStatus.getAttribute("aria-label");

                if (!currentMessageStatus.equals(messageStatus)) {
                    if (currentMessageStatus.equals(" Sent ")) {
                        this.messages.setText("V");
                        currentPhoneNumber.setMessageStatus(Constants.SENT_STATUS);

                    } else if (currentMessageStatus.equals(" Delivered ")) {
                        this.messages.setText("VV");
                        currentPhoneNumber.setMessageStatus(Constants.DELIVERED_STATUS);

                    } else if (currentMessageStatus.equals(" Read ")) {
                        this.messages.setForeground(Color.BLUE);
                        this.messages.setText("VV");
                        this.messages.setForeground(Color.BLACK);
                        currentPhoneNumber.setMessageStatus(Constants.READ_STATUS);
                    }
                    messageStatus = currentMessageStatus;
                }
            } catch (StaleElementReferenceException e) {
                sentMessagesList = this.driver.findElements(By.cssSelector("span[aria-label=\" Pending \"]"));
                lastMessageStatus = sentMessagesList.get(sentMessagesList.size() - 1);
            }
        }
    }

    private void checkRespondMessage() {
        boolean isReceivedNewMessage = false;

//            while (!isReceivedNewMessage) {
        if (!this.reportMessageText.equals(extractRespondMessage())) {
            this.currentPhoneNumber.setRecipientResponse(extractRespondMessage());
//            isReceivedNewMessage = true;
        }
//            }
        this.messages.setForeground(Color.BLACK);
        this.messages.setText(this.currentPhoneNumber.getRecipientResponse());
    }

    private String extractRespondMessage() {
        List<WebElement> chatMessages = this.driver.findElements(By.cssSelector("span[class=\"i0jNr selectable-text copyable-text\"]"));
        WebElement lastMessage = chatMessages.get((chatMessages.size() - 1));
        String lastMessageText = "";
        if (lastMessage != null) {
            lastMessageText = lastMessage.getText();
        }
        return lastMessageText;
    }

    private void clickOnSend() {
        WebElement sendButton = null;
        boolean isButtonExist = false;
        while (!isButtonExist) {
            try {
                sendButton = (this.driver.findElement(By.cssSelector("span[data-icon=\"send\"]")));
                isButtonExist = true;
            } catch (NoSuchElementException exception) {
            }
        }
        sendButton.click();
    }

    private void writeToFile(String text) {
        try {
            FileWriter writer = new FileWriter(Constants.PATH_TO_FILTERED_REPORT);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}