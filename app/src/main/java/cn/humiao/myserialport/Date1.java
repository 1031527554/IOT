package cn.humiao.myserialport;

public class Date1 {
    private String date;
    private String message;
    private String signal;
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate(){
        return message;
    }
    public String getSignal(){
        return signal;
    }
    public void collation(){
        this.signal = date.substring(28,30);
        this.message = date.substring(34,46);
    }
}
