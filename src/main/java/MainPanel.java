import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPanel extends JPanel {

    private ChromeDriver driver;

    private JButton whatsappButton;
    private JButton report;

    private JTextField text;
    private JTextField phoneNumber;

    private JLabel messages;

    private Font buttonFont;
    private Font textFont;
    private Font messagesFont;

    private boolean button;
    private boolean newMessage;

    private String reportPhoneNumber;
    private String reportMessageText;
    private String reportRecipientResponse;

    public MainPanel(int x, int y, int width, int height) {
        this.setLayout(null);
        this.setBounds(x, y, width, height);

        this.buttonFont = new Font("David", Font.BOLD, Constants.BUTTON_FONT_SIZE);
        this.textFont = new Font("David", Font.ROMAN_BASELINE, Constants.TEXT_FONT_SIZE);
        this.messagesFont = new Font("David", Font.ROMAN_BASELINE, Constants.MESSAGE_FONT_SIZE);

        buildPanel();

        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\dasha\\Downloads\\chromedriver_win32\\chromedriver.exe");


        this.whatsappButton.addActionListener((e) -> {
            this.reportPhoneNumber = "";
            this.reportMessageText = "";
            this.reportRecipientResponse = "";

            this.button = true;
        });

        this.report.addActionListener((e) -> {
            String reportText = "Recipient : " + this.reportPhoneNumber + "\n" +
                    "Message : " + this.reportMessageText + "\n" +
                    "Recipient response : " + this.reportRecipientResponse;
            writeToFile(reportText,Constants.PATH_TO_FILTERED_REPORT);
        });

        new Thread(() -> {
            try {
                while (true) {

                    if (this.button) {
                        this.newMessage = false;
                        this.messages.setForeground(Color.BLACK);
                        int phoneNumberInt = checkPhoneNumber();

                        if (phoneNumberInt != 0) {
                            this.reportPhoneNumber += "0" + phoneNumberInt;
                            if (!this.text.getText().equals("")) {
                                this.reportMessageText += this.text.getText();
                                this.driver = new ChromeDriver();
                                this.driver.get(Constants.WEB_WHATSAPP_ADDRESS + phoneNumberInt);

                                loginCheck();

                                sendMessage();

                                updateMessageStatus();

                                checkRespondMessage();
                            } else {
                                this.messages.setText("no message");
                            }
                        }
                        this.button = false;
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
        this.whatsappButton.setBounds((Constants.WINDOW_WIDTH - Constants.BUTTON_WIDTH) / 2, Constants.WINDOW_HEIGHT / 2, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        this.whatsappButton.setText("Web Whatsapp");
        this.whatsappButton.setFont(this.buttonFont);
        this.add(this.whatsappButton);

        this.messages = new JLabel();
        this.messages.setBounds((this.whatsappButton.getX() - (Constants.CONNECTED_TEXT_WIDTH - this.whatsappButton.getWidth()) / 2),
                this.whatsappButton.getY() + this.whatsappButton.getHeight() * 2, Constants.CONNECTED_TEXT_WIDTH * 2, this.whatsappButton.getHeight());
        this.messages.setFont(this.messagesFont);
        this.messages.setForeground(Color.BLACK);
        this.add(this.messages);

        this.text = new JTextField();
        this.text.setBounds((this.whatsappButton.getX() - (Constants.TEXT_FIELD_WIDTH - this.whatsappButton.getWidth()) / 2), this.whatsappButton.getY() - this.whatsappButton.getHeight(), Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
        this.add(this.text);

        JLabel textLabel = new JLabel("Enter text:");
        textLabel.setBounds(this.text.getX() - this.text.getWidth() - Constants.SPACE * 3, this.whatsappButton.getY() - this.whatsappButton.getHeight(), Constants.TEXT_FIELD_WIDTH * 2, Constants.TEXT_FIELD_HEIGHT);
        textLabel.setFont(this.textFont);
        this.add(textLabel);

        this.phoneNumber = new JTextField();
        this.phoneNumber.setBounds((this.whatsappButton.getX() - (Constants.TEXT_FIELD_WIDTH - this.whatsappButton.getWidth()) / 2), this.text.getY() - (this.text.getHeight() * 2), Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
        this.add(this.phoneNumber);

        JLabel phoneNumberLabel = new JLabel("Enter phone number:");
        phoneNumberLabel.setBounds(this.phoneNumber.getX() - this.phoneNumber.getWidth() - Constants.SPACE * 3, this.text.getY() - (this.text.getHeight() * 2), Constants.TEXT_FIELD_WIDTH * 2, Constants.TEXT_FIELD_HEIGHT);
        phoneNumberLabel.setFont(this.textFont);
        this.add(phoneNumberLabel);

        this.report = new JButton("Create a report");
        this.report.setBounds((Constants.WINDOW_WIDTH - Constants.BUTTON_WIDTH)-Constants.SPACE*2,Constants.SPACE,Constants.BUTTON_WIDTH,Constants.BUTTON_HEIGHT);
        this.report.setFont(this.buttonFont);
        this.add(this.report);
    }

    private int checkPhoneNumber() {
        String phoneNumberString = this.phoneNumber.getText();
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
        boolean isTextBoxExist = false;
        WebElement textBox = null;
        while (!isTextBoxExist) {
            try {
                textBox = (this.driver.findElement(By.cssSelector("div[title=\"Type a message\"]")));
                isTextBoxExist = true;
            } catch (NoSuchElementException exception) {
            }
        }
        textBox.sendKeys(this.text.getText());

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
        while (!isRead) {
            String currentMessageStatus = lastMessageStatus.getAttribute("aria-label");

            if (!currentMessageStatus.equals(messageStatus)) {
                if (currentMessageStatus.equals(" Sent ")) {
                    this.messages.setText("V");
                } else if (currentMessageStatus.equals(" Delivered ")) {
                    this.messages.setText("VV");
                } else if (currentMessageStatus.equals(" Read ")) {
                    isRead = true;
                    this.messages.setForeground(Color.BLUE);
                    this.messages.setText("VV");
                }
                messageStatus = currentMessageStatus;
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
                System.out.println("Received a new message");
                this.messages.setForeground(Color.BLACK);
                this.messages.setText(this.reportRecipientResponse);
                this.newMessage = true;
                this.driver.close();

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
        String lastMessageText = lastMessage.getText();
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