public class PhoneNumber {
    private final String phoneNumber;
    private boolean isExistInWhatsapp;
    private int messageStatus;
    private String recipientResponse;


    public PhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isExistInWhatsapp() {
        return isExistInWhatsapp;
    }

    public void setExistInWhatsapp(boolean existInWhatsapp) {
        isExistInWhatsapp = existInWhatsapp;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        if (messageStatus == Constants.SENT_STATUS || messageStatus == Constants.DELIVERED_STATUS || messageStatus == Constants.READ_STATUS) {
            this.messageStatus = messageStatus;
        }
    }

    public String getRecipientResponse() {
        return recipientResponse;
    }

    public void setRecipientResponse(String recipientResponse) {
        this.recipientResponse = recipientResponse;
    }

    @Override
    public String toString() {
        if (isExistInWhatsapp) {
            String status;
            if (messageStatus == Constants.SENT_STATUS) {
                status = "Sent";
            }else if (messageStatus == Constants.DELIVERED_STATUS) {
                status = "Delivered";
            }else {
                status = "Read";
            }
            return "phoneNumber='" + phoneNumber + '\'' +
                    ", messageStatus=" + status +
                    ", recipientResponse='" + recipientResponse + '\'' +
                    '}';
        }else {
            return "Invalid telephone number";
        }
    }
}
