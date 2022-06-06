import javax.swing.*;
import java.awt.*;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPanel extends JPanel {

    private JButton whatsappButton;
    private JTextField text;
    private JTextField phoneNumber;
    private JLabel messages;

    private Font buttonFont;
    private Font textFont;

    private boolean isConnected;

    public MainPanel(int x, int y, int width, int height) {
        this.setLayout(null);
        this.setBounds(x, y, width, height);

        this.buttonFont = new Font("David", Font.BOLD, Constants.BUTTON_FONT_SIZE);
        this.textFont = new Font("David", Font.ROMAN_BASELINE, Constants.BUTTON_FONT_SIZE);

        this.whatsappButton = new JButton();
        this.whatsappButton.setBounds((Constants.WINDOW_WIDTH - Constants.BUTTON_WIDTH) / 2, Constants.WINDOW_HEIGHT / 2, Constants.BUTTON_WIDTH, Constants.BUTTON_HEIGHT);
        this.whatsappButton.setText("Web Whatsapp");
        this.whatsappButton.setFont(this.buttonFont);
        this.add(this.whatsappButton);
        this.isConnected = false;


        this.messages = new JLabel();
        this.messages.setBounds((this.whatsappButton.getX() - (Constants.CONNECTED_TEXT_WIDTH - this.whatsappButton.getWidth()) / 2),
                this.whatsappButton.getY() + this.whatsappButton.getHeight(), Constants.CONNECTED_TEXT_WIDTH, this.whatsappButton.getHeight());
        this.messages.setFont(this.textFont);
        this.add(this.messages);

        this.text = new JTextField();
        this.text.setBounds((this.whatsappButton.getX() - (Constants.TEXT_FIELD_WIDTH - this.whatsappButton.getWidth()) / 2), this.whatsappButton.getY() - this.whatsappButton.getHeight(), Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
        this.add(this.text);

        this.phoneNumber = new JTextField();
        this.phoneNumber.setBounds((this.whatsappButton.getX() - (Constants.TEXT_FIELD_WIDTH - this.whatsappButton.getWidth()) / 2), this.text.getY() - (this.text.getHeight() * 2), Constants.TEXT_FIELD_WIDTH, Constants.TEXT_FIELD_HEIGHT);
        this.add(this.phoneNumber);


        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\tehil\\Downloads\\chromedriver_win32\\chromedriver.exe");
        this.whatsappButton.addActionListener((e) -> {

            int phoneNumberInt = checkPhoneNumber();


            if (phoneNumberInt != 0) {
                if (!this.text.getText().equals("")) {
                    ChromeDriver driver = new ChromeDriver();
                    driver.get(Constants.WEB_WHATSAPP_ADDRESS + phoneNumberInt);
                    while (!this.isConnected) {
                        try {
                            this.isConnected = (driver.findElement(By.id("side")).isDisplayed());
                        } catch (NoSuchElementException exception) {
                        }
                    }
                    messages.setText("You are connected!");




                    boolean isTextBoxExist = false;
                    WebElement textBox = null;
                    while (!isTextBoxExist) {
                        try {
                            textBox = (driver.findElement(By.cssSelector("div[title=\"Type a message\"]")));
                            isTextBoxExist = true;
                        } catch (NoSuchElementException exception) {
                        }
                    }
                    textBox.sendKeys(this.text.getText());

                    WebElement sendButton = null;
                    boolean isButtonExist = false;
                    while (!isButtonExist) {
                        try {
                            sendButton = (driver.findElement(By.className("tvf2evcx oq44ahr5 lb5m6g5c svlsagor p2rjqpw5 epia9gcq")));
                            isButtonExist = true;
                        } catch (NoSuchElementException exception) {
                        }
                        System.out.println(isButtonExist);
                    }

                    sendButton.click();



                } else {
                    this.messages.setText("no message");
                }

            }


        });

//        new Thread(() -> {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException exception) {
//                exception.printStackTrace();
//            }
//        }).start();

//        div[title=\"Type a message\"]"

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

}