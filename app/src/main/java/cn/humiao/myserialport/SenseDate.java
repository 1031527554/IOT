package cn.humiao.myserialport;

public class SenseDate {
    private double temp=0,humidity=0,light=0,co2=0;

    public double getTemp(){
        return temp;
    }
    public double getHumidity(){
        return humidity;
    }
    public double getLight(){
        return light;
    }
    public double getCo2(){
        return co2;
    }

    public void setTemp(double temp){
        this.temp=temp;
    }
    public void setHumidity(double humidity){
        this.humidity=humidity;
    }
    public void setLight(double light){
        this.light=light;
    }
    public void setCo2(double co2){
        this.co2=co2;
    }
}
