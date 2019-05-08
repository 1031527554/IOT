package cn.humiao.myserialport;


import java.text.DecimalFormat;

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
    public void collation() {
        if (date.substring(0, 1).equals("$")) {    //zigbee 数据
            this.signal = date.substring(29, 31);
            this.message = date.substring(35, 47);
        }
        if (date.substring(0,1).equals("!"))
        {
            this.signal = "!";
            this.message = date.substring(1);
        }
    }
    public String humidity(){
        double HH =DataUtils.HexToInt(this.message.substring(4,6));
        double HL =DataUtils.HexToInt(this.message.substring(6,8));
        DecimalFormat df = new DecimalFormat("###.00");
        String H1 = df.format ((HH*256+HL)/10);
        return H1;
    }

    public String temperature(){
        double TH =DataUtils.HexToInt(this.message.substring(8,10));
        double TL =DataUtils.HexToInt(this.message.substring(10));
        DecimalFormat df = new DecimalFormat("###.00");
        String T1 = df.format ((TH*256+TL)/10);
        return T1;
    }
}
