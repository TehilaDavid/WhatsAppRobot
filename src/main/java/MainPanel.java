import javax.swing.*;
import java.awt.*;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPanel extends JPanel {

    private ChromeDriver driver;

    private JButton whatsappButton;

    private JTextField text;
    private JTextField phoneNumber;

    private JLabel messages;

    private Font buttonFont;
    private Font textFont;

    private int amountResponseMessages;

    private WebElement lastMessage;

    private boolean button;

    public MainPanel(int x, int y, int width, int height) {
        this.setLayout(null);
        this.setBounds(x, y, width, height);

        this.buttonFont = new Font("David", Font.BOLD, Constants.BUTTON_FONT_SIZE);
        this.textFont = new Font("David", Font.ROMAN_BASELINE, Constants.BUTTON_FONT_SIZE);

        buildPanel();

        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\tehil\\Downloads\\chromedriver_win32\\chromedriver.exe");


        this.whatsappButton.addActionListener((e) -> {
            this.button = true;
        });


        new Thread(() -> {
            try {
                while (true) {

                    if (this.button) {

                        int phoneNumberInt = checkPhoneNumber();


                        if (phoneNumberInt != 0) {
                            if (!this.text.getText().equals("")) {
                                this.driver = new ChromeDriver();
                                this.driver.get(Constants.WEB_WHATSAPP_ADDRESS + phoneNumberInt);

                                loginCheck();

                                sendMessage();

                                updateMessageStatus();

                                this.amountResponseMessages = getResponseMessages().size();

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

//        Pending
//        Sent
//        Delivered
//        Read

//        new Thread(() -> {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException exception) {
//                exception.printStackTrace();
//            }
//        }).start();


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

    private void updateMessageStatus() {
        List<WebElement> sentMessagesList = null;
        boolean isSentMessagesExist = false;
        while (!isSentMessagesExist) {
            try {
                sentMessagesList = this.driver.findElements(By.cssSelector("span[aria-label=\" Pending \"]"));
                isSentMessagesExist = true;
            } catch (NoSuchElementException exception) {
//                            System.out.println("No");
            }
        }
//                    System.out.println("Yes");
        WebElement lastMessageStatus = sentMessagesList.get(sentMessagesList.size() - 1);

        String messageStatus1 = lastMessageStatus.getAttribute("aria-label");
        boolean isRead = false;
        while (!isRead) {
            String messageStatus2 = lastMessageStatus.getAttribute("aria-label");
            System.out.println(messageStatus2);

            if (!messageStatus2.equals(messageStatus1)) {
                if (messageStatus2.equals(" Sent ")) {
                    this.messages.setText("V");
                } else if (messageStatus2.equals(" Delivered ")) {
                    this.messages.setText("VV");
                } else if (messageStatus2.equals(" Read ")) {
                    isRead = true;
                    this.messages.setText("Blue VV");
                }
                messageStatus1 = messageStatus2;
            }
        }

//        div[title=\"Type a message\"]"
//        Pending
//        Sent
//        Delivered
//        Read
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
        messages.setText("the message was sent successfully!");
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

    private List<WebElement> getResponseMessages() {
        List<WebElement> responseMessagesList = null;
        boolean isResponseMessagesExist = false;
        while (!isResponseMessagesExist) {
            try {
                responseMessagesList = this.driver.findElements(By.cssSelector("span[data-icon=\"tail-in\"]"));
                isResponseMessagesExist = true;
            } catch (NoSuchElementException exception) {

            }
        }
        return responseMessagesList;
    }

    private void checkRespondMessage() {
        List<WebElement> responseMessagesList = null;
        boolean isReceivedNewMessage = false;
        while (!isReceivedNewMessage) {
            responseMessagesList = getResponseMessages();
            isReceivedNewMessage = (this.amountResponseMessages < responseMessagesList.size());
        }
        System.out.println("Received a new message");
        System.out.println(responseMessagesList.get(this.amountResponseMessages).getText());
    }

    private void buildPanel() {
        this.whatsappButton = new JButton();
        this.whatsappButton.setBounds((Constants.WINDOW_WIDTH - Constants.BUTTON_WIDTH) / 2, Constants.WINDOW_HEIGHT / 2, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        this.whatsappButton.setText("Web Whatsapp");
        this.whatsappButton.setFont(this.buttonFont);
        this.add(this.whatsappButton);


        this.messages = new JLabel();
        this.messages.setBounds((this.whatsappButton.getX() - (Constants.CONNECTED_TEXT_WIDTH - this.whatsappButton.getWidth()) / 2),
                this.whatsappButton.getY() + this.whatsappButton.getHeight(), Constants.CONNECTED_TEXT_WIDTH, this.whatsappButton.getHeight());
        this.messages.setFont(this.textFont);
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
    }
}