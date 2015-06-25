package net.xcodersteam.lemoprint;

import com.github.mraa4j.GPIO;
import com.github.mraa4j.MraaException;
import com.github.mraa4j.enums.GPIODirT;

/**
 * Created by one on 25.06.15.
 */
public class Clock extends Thread {
    GPIO buzzer;

    public Clock() {
        try {
            buzzer = new GPIO(3);
            buzzer.setDirection(GPIODirT.MRAA_GPIO_OUT);
        } catch (MraaException e) {
            e.printStackTrace();
        }

    }

    public static final int alarmLen=10000;

    long alarmTime = 0;
    public boolean alarmNow(){
        return System.currentTimeMillis()>alarmTime && System.currentTimeMillis()<alarmTime+alarmLen;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(1000);
                if(alarmNow()){
                    buzzer.write(1);
                    Thread.sleep(100);
                    buzzer.write(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
