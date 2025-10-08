package Ash_Hollow_Requiem;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine;
import java.util.ArrayList;
import java.util.List;

public class MicrophoneUtils {

    public static List<String> getAvailableMicrophones() {
        List<String> mics = new ArrayList<>();

        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            Mixer mixer = AudioSystem.getMixer(info);
            if (mixer.isLineSupported(new DataLine.Info(TargetDataLine.class, null))) {
                mics.add(info.getName());
            }
        }

        return mics;
    }
}
