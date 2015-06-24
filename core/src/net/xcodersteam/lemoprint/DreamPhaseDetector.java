package net.xcodersteam.lemoprint;

import com.github.mraa4j.GPIO;
import com.github.mraa4j.MraaException;
import com.github.mraa4j.enums.GPIODirT;

/**
 * Created by 1 on 24.06.2015.
 */
public class DreamPhaseDetector extends Thread{
    GPIO pin;

    public DreamPhaseDetector() {
        try {
            pin = new GPIO(12);
            pin.setDirection(GPIODirT.MRAA_GPIO_IN);
        } catch (MraaException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            try {
                while (true) {
                    if(count>2){
                        System.out.println(count);
                        count=0;
                    }
                    Thread.sleep(60000);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    int count = 0;
    @Override
    public void run() {
        try {
            while (true) {
                int sensor_state = pin.read();
                //System.out.println(sensor_state);
                count+=sensor_state;
                Thread.sleep(1);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
