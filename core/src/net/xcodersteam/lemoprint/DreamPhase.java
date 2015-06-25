package net.xcodersteam.lemoprint;

import com.github.mraa4j.GPIO;

/**
 * Created by 1 on 24.06.2015.
 */
public class DreamPhase {
    int phase = 0;
    boolean deep_phase = true;
    public long start;
    public long end;
    public long interval(){
        return end-start;
    }
    public DreamPhase(){

        start=System.currentTimeMillis();
       // for(int i = 0; i < array.length; i++)
        //{
          //  if(array[i] > 500)
           //     phase++;
       // }
        //if(phase > 2)
          //  deep_phase = false;
    }
}

