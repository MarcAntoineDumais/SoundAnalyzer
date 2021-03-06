package soundanalyzer.audio;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.springframework.stereotype.Service;

import soundanalyzer.config.AudioConfig;

@Service
public class AudioOutput {
    
    private List<AudioConnectionListener> connectionListeners;
    private AudioConfig config;
    private Mixer.Info mixerInfo;
    private SourceDataLine speakers;
    
    public AudioOutput(AudioConfig config) {
        this.config = config;
        connectionListeners = new ArrayList<AudioConnectionListener>();
    }
    
    public void setMixerInfo(Mixer.Info mixerInfo) {
        this.mixerInfo = mixerInfo;
    }
    
    public void start() {
        stop();
        
        AudioFormat format = new AudioFormat(config.getFormat().getSampleRate(), 
                config.getFormat().getSampleSizeInBits(),
                config.getFormat().getChannels(),
                config.getFormat().isSigned(),
                config.getFormat().isBigEndien());
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
        
        try {
            if (mixerInfo == null) {
                speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                speakers.close();
            } else {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                speakers = (SourceDataLine)mixer.getLine(dataLineInfo);
                speakers.close();
            }
            
            speakers.open(format);
            speakers.start();
            connectionListeners.stream().forEach(listener -> listener.lineOpened());
        } catch (LineUnavailableException e) {
            System.err.println("Could not initialize audio ouput");
            stop();
            e.printStackTrace();
        }
    }

    public void stop() {
        if (speakers != null) {
            speakers.close();
            connectionListeners.stream().forEach(listener -> listener.lineClosed());
        }
    }
    
    public void write(byte[] data) {
        if (speakers != null && speakers.isOpen()) {
            speakers.write(data, 0, data.length);
        }
    }
    
    public void flush() {
        if (speakers != null && speakers.isOpen()) {
            speakers.flush();
        }
    }
    
    public void subscribe(AudioConnectionListener listener) {
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
    }

    public void unsubscribe(AudioConnectionListener listener) {
        connectionListeners.remove(listener);
    }
}
