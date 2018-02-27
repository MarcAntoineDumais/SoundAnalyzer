package soundanalyzer.audio;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.stereotype.Service;

import soundanalyzer.config.AudioConfig;

@Service
public class AudioInput {
    private List<AudioConnectionListener> connectionListeners;
    private List<AudioDataListener> dataListeners;
    private AudioDataProcessor thread;
    private Mixer.Info mixerInfo;
    private AudioConfig config;
    private AudioFormat format;

    public AudioInput(AudioConfig config) {
        this.config = config;
        format = new AudioFormat(config.getFormat().getSampleRate(), 
                config.getFormat().getSampleSizeInBits(),
                config.getFormat().getChannels(),
                config.getFormat().isSigned(),
                config.getFormat().isBigEndien());
        connectionListeners = new ArrayList<AudioConnectionListener>();
        dataListeners = new ArrayList<AudioDataListener>();
    }

    public void start() {
        stop();
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            TargetDataLine line;
            if (mixerInfo == null) {
                line = (TargetDataLine)AudioSystem.getLine(info);
                line.close();
                thread = new AudioDataProcessor(line, connectionListeners, dataListeners);
                thread.start();
            } else {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                line = (TargetDataLine)mixer.getLine(info);
                line.close();
                thread = new AudioDataProcessor(line, connectionListeners, dataListeners);
                thread.start();
            }			
        } catch (LineUnavailableException e) {
            System.err.println("Could not initialize audio input");
            stop();
            e.printStackTrace();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stopProcessing();
            while (!thread.closed);
        }
    }

    public double getMaxFrequency() {
        return config.getFormat().getSampleRate() / 2.0;
    }

    public void setMixerInfo(Mixer.Info mixerInfo) {
        this.mixerInfo = mixerInfo;
    }

    public void subscribeConnection(AudioConnectionListener listener) {
        if (!connectionListeners.contains(listener)) {
            connectionListeners.add(listener);
        }
    }

    public void unsubscribeConnection(AudioConnectionListener listener) {
        connectionListeners.remove(listener);
    }
    
    public void subscribeData(AudioDataListener listener) {
        if (!dataListeners.contains(listener)) {
            dataListeners.add(listener);
        }
    }

    public void unsubscribeData(AudioDataListener listener) {
        dataListeners.remove(listener);
    }

    private class AudioDataProcessor extends Thread {
        private boolean stopped;
        private boolean closed;
        private TargetDataLine line;
        private List<AudioConnectionListener> connectionListeners;
        private List<AudioDataListener> dataListeners;

        public AudioDataProcessor(TargetDataLine line,
                List<AudioConnectionListener> connectionListeners,
                List<AudioDataListener> dataListeners) {
            this.line = line;
            this.connectionListeners = connectionListeners;
            this.dataListeners = dataListeners;
            this.closed = false;
        }

        @Override
        public void run() {
            stopped = false;

            try {
                line.open(format, config.getFormat().getBufferSize());
                line.start();
                connectionListeners.stream().forEach(listener -> listener.lineOpened());
                byte[] data = new byte[config.getFormat().getBufferSize() / 4];
                while (!stopped) {
                    line.read(data, 0, data.length);
                    dataListeners.stream().forEach(listener -> listener.readData(data));
                }
            } catch (LineUnavailableException e) {
                System.err.println("Could not initialize microphone input");
                e.printStackTrace();
            } finally {
                connectionListeners.stream().forEach(listener -> listener.lineClosed());
                closed = true;
            }			
        }

        public void stopProcessing() {
            this.stopped = true;
            line.close();
        }
    }
}
