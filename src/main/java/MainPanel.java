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

    private JTextField textToSend;
    private JTextArea phoneNumber;

    private JLabel messages;

    private Font buttonFont;
    private Font textFont;
    private Font messagesFont;

    private ArrayList<Integer> correctPhoneNumbers;

    private boolean whatsappButtonClicked;
    private boolean newMessage;
    private boolean isValidWhatsappNumber;
    private boolean isMessageSend;
    private boolean isLastPhoneNumber;

    private String reportPhoneNumber;
    private String reportMessageText;
    private String reportRecipientResponse;

    public MainPanel(int x, int y, int width, int height) {
        this.setLayout(null);
        this.setBounds(x, y, width, height);
        this.setBackground(Color.PINK);


        this.buttonFont = new Font("David", Font.BOLD, Constants.BUTTON_FONT_SIZE);
        this.textFont = new Font("David", Font.ROMAN_BASELINE, Constants.TEXT_FONT_SIZE);
        this.messagesFont = new Font("David", Font.ROMAN_BASELINE, Constants.MESSAGE_FONT_SIZE);


        buildPanel();


        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\tehil\\Downloads\\chromedriver_win32 (1)\\chromedriver.exe");


        this.whatsappButton.addActionListener((e) -> {
            this.reportPhoneNumber = "";
            this.reportMessageText = "";
            this.reportRecipientResponse = "";

            this.whatsappButtonClicked = true;
        });

        this.report.addActionListener((e) -> {
            String reportText = "";

            if (this.correctPhoneNumbers != null) {

                for (int i = 0; i < correctPhoneNumbers.size(); i++) {
                    if (this.isValidWhatsappNumber) {
                        reportText += "Recipient : " + (this.correctPhoneNumbers.get(i) == null ? "" : "0" + this.correctPhoneNumbers.get(i)) + "\n" +
                                "Message : " + (this.reportMessageText == null ? "" : this.reportMessageText) + "\n" +
                                "Recipient response : " + (this.reportRecipientResponse == null ? "" : this.reportRecipientResponse);
                    } else {
                        reportText += "Invalid telephone number";
                    }
                    reportText += "\n" + "\n";
                }
            }
            writeToFile(reportText, Constants.PATH_TO_FILTERED_REPORT);
        });

        new Thread(() -> {
            try {
                while (true) {

                    if (this.whatsappButtonClicked) {
                        correctPhoneNumbers = new ArrayList<>();
                        this.newMessage = false;
                        this.messages.setForeground(Color.BLACK);
                        String[] phoneNumbers = this.phoneNumber.getText().split("\\n");
                        int counter = 0;
                        for (int i = 0; i < phoneNumbers.length; i++) {
                            int currentNumber = checkPhoneNumber(phoneNumbers[i]);
                            if (currentNumber != 0) {
                                correctPhoneNumbers.add(currentNumber);
                                counter++;
                            }
                        }
//

                        this.messages.setText("You entered " + phoneNumbers.length + " recipient numbers, of which " + counter + " are valid cell phone numbers.");


                        if (counter != 0) {
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


                            JButton approveButton = new JButton();
                            approveButton.setBounds(cancelButton.getX() + cancelButton.getWidth() + Constants.SPACE, cancelButton.getY(), cancelButton.getWidth(), cancelButton.getHeight());
                            approveButton.setText("Approve");
                            approveButton.setFont(this.buttonFont);
                            this.add(approveButton);

                            cancelButton.addActionListener((e) -> {
                                this.messages.setText("Enter phone numbers: ");
                                questionLabel.setText("");
                            });

                            approveButton.addActionListener((e) -> {
                                this.isMessageSend = true;

                                for (int i = 0; i < correctPhoneNumbers.size(); i++) {
                                    this.isMessageSend = false;
                                    int currentPhoneNumber = correctPhoneNumbers.get(i);
                                    this.reportPhoneNumber += "0" + currentPhoneNumber;

                                    if (!this.textToSend.getText().equals("")) {
                                        this.reportMessageText = this.textToSend.getText();
                                        if (i == 0) {
                                            this.driver = new ChromeDriver();
                                            this.driver.get(Constants.WEB_WHATSAPP_ADDRESS + currentPhoneNumber);
                                            loginCheck();
                                        }

                                        sendMessage();
                                        System.out.println("The message sent");


                                        if (this.isValidWhatsappNumber) {
                                            updateMessageStatus();
//
//                                            checkRespondMessage();
                                        }

                                        if (this.isMessageSend && i != correctPhoneNumbers.size() - 1) {
                                            System.out.println("get  " + correctPhoneNumbers.get(i + 1));
                                            this.driver.get(Constants.WEB_WHATSAPP_ADDRESS + correctPhoneNumbers.get(i + 1));
                                            System.out.println("end  " + i);
                                        }


                                        if (i == correctPhoneNumbers.size() - 1) {
                                            this.isLastPhoneNumber = true;
                                        }


                                    } else {
                                        this.messages.setText("no message");
                                    }
                                }

                            });
                        }

                        this.whatsappButtonClicked = false;
                    }
                    Thread.sleep(1);
                }
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();
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

    private int checkPhoneNumber(String phoneNumberString) {
        int phoneNumberInt = 0;
        if (phoneNumberString.equals("")) {
            messages.setText("There is no phone number");
        } else if (phoneNumberString.length() != 10) {
            messages.setText("Invalid input length");
        } else if (!phoneNumberString.substring(0, 2).equals("05")) {
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
            System.out.println();
            try {
                textBox = (this.driver.findElement(By.cssSelector("div[title=\"Type a message\"]")));
                isElementExist = true;
                this.isValidWhatsappNumber = true;
            } catch (NoSuchElementException exception) {

                try {
                    WebElement okButton = (this.driver.findElement(By.cssSelector("div[data-testid=\"popup-controls-ok\"]")));
                    if (okButton != null) {
                        okButton.click();
                        messages.setText("Invalid telephone number");
                        isElementExist = true;
                    }
                }catch (NoSuchElementException e) {
                }
            } catch (UnhandledAlertException e) {

            }
        }
        if (this.isValidWhatsappNumber) {
            textBox.sendKeys(this.textToSend.getText());

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
            messages.setText("The message was sent successfully!");
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
        boolean isRead = false;
        boolean isSend = false;
        while (!isRead) {
            try {
                String currentMessageStatus = lastMessageStatus.getAttribute("aria-label");

                if (!currentMessageStatus.equals(messageStatus)) {
                    if (currentMessageStatus.equals(" Sent ")) {
                        this.messages.setText("V");

                        isRead = true;
                        this.isMessageSend = true;

                    } else if (currentMessageStatus.equals(" Delivered ")) {
                        this.messages.setText("VV");

                        isRead = true;
                        isSend = true;
                        this.isMessageSend = true;

                    } else if (currentMessageStatus.equals(" Read ")) {
                        isRead = true;
                        isSend = true;
                        this.messages.setForeground(Color.BLUE);
                        this.messages.setText("VV");

                        this.isMessageSend = true;

                    }
                    messageStatus = currentMessageStatus;
                }
            }catch (StaleElementReferenceException e) {
                System.out.println("Error");
            }
        }
    }

    private void checkRespondMessage() {
        new Thread(() -> {
            while (!this.newMessage) {
                boolean isReceivedNewMessage = false;

                while (!isReceivedNewMessage) {
                    if (!this.reportMessageText.equals(extractRespondMessage())) {
                        this.reportRecipientResponse += extractRespondMessage();
                        isReceivedNewMessage = true;
                    }
                }
                this.messages.setForeground(Color.BLACK);
                this.messages.setText(this.reportRecipientResponse);
                this.newMessage = true;
                if (this.isLastPhoneNumber) {
                    this.driver.close();
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
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

    private void writeToFile(String text, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}