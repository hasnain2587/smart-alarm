package net.xcodersteam.lemoprint;

import com.github.mraa4j.GPIO;
import com.github.mraa4j.MraaException;
import com.github.mraa4j.SPI;
import com.github.mraa4j.enums.GPIODirT;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by one on 24.06.15.
 */
public class TimeController implements Runnable {
    GPIO data;
    GPIO[] latches;
    GPIO clock;
    GPIO point;

    char number[] = {0b00000011, 0b10011111, 0b00100101, 0b00001101, 0b10011001, 0b01001001, 0b01000001, 0b00011111, 0b0000001, 0b00001001};

    public TimeController() {
        try {
            data = new GPIO(9);
            data.setDirection(GPIODirT.MRAA_GPIO_OUT);
            latches = new GPIO[]{new GPIO(5),new GPIO(6),new GPIO(7),new GPIO(8)};
            for(GPIO gpio:latches)
                gpio.setDirection(GPIODirT.MRAA_GPIO_OUT);
            clock = new GPIO(10);
            clock.setDirection(GPIODirT.MRAA_GPIO_OUT);

            point = new GPIO(4);
            point.setDirection(GPIODirT.MRAA_GPIO_OUT);
        } catch (MraaException e) {
            e.printStackTrace();
        }

    }

    private final static int mask = 0b00000001 << 31;
    public void sendData(int b) throws MraaException, InterruptedException {
        for (int z=0;z<4;z++) {
            for (int i = 0; i < 8; i++) {
                data.write(((mask & b)>0)?1:0);
                Thread.sleep(1);
                clock.write(1);
                Thread.sleep(1);
                clock.write(0);
                b <<= 1;
                Thread.sleep(1);
            }
            latches[z].write(1);
            Thread.sleep(1);
            latches[z].write(0);
        }

    }

    Date d= new Date();
    SimpleDateFormat dt = new SimpleDateFormat("hhmmss");
    int time=0;
    @Override
    public void run() {
        while (true){
            d.setTime(System.currentTimeMillis());
            dt.setTimeZone(TimeZone.getTimeZone("GMT+3"));
            String str=dt.format(d);

            try {
                int newtime=number[str.charAt(0)-0x30] | number[str.charAt(1)-0x30] << 8 | number[str.charAt(2)-0x30]<<16 | number[str.charAt(3) - 0x30]<<24;
                if(time!=newtime) {
                    sendData(newtime);
                    time=newtime;
                }

                point.write(d.getSeconds()%2==0?1:0);
                System.out.println(str);
                Thread.sleep(100);

            } catch (MraaException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
