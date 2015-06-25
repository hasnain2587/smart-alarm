package net.xcodersteam.lemoprint;

import com.github.mraa4j.GPIO;
import com.github.mraa4j.MraaException;
import com.github.mraa4j.enums.GPIODirT;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 1 on 24.06.2015.
 */
public class DreamPhaseDetector extends Thread{
    GPIO pin;
    GPIO LED;
    List<DreamPhase> phases=new LinkedList<>();
    DreamPhase currentPhase=new DreamPhase();
    public DreamPhaseDetector() {
        try {
            pin = new GPIO(12);
            pin.setDirection(GPIODirT.MRAA_GPIO_IN);
            LED = new GPIO(13);
            LED.setDirection(GPIODirT.MRAA_GPIO_OUT);
        } catch (MraaException e) {
            e.printStackTrace();
        }
        new Thread(()->{
            try {
                while (true) {

                    boolean isDeep=(count<1000);
                    if(currentPhase.deep_phase!=isDeep){
                        currentPhase.end=System.currentTimeMillis();
                        phases.add(currentPhase);
                        currentPhase=new DreamPhase();
                        currentPhase.deep_phase=isDeep;
                    }
                        System.out.println(count);
                        count=0;

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
                LED.write(sensor_state);
                //System.out.println(sensor_state);
                count+=sensor_state;
                Thread.sleep(1);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
