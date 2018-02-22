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

import soundanalyzer.config.AudioFormatConfig;

@Service
public class AudioInput {
    private List<AudioConnectionListener> connectionListeners;
    private List<AudioDataListener> dataListeners;
    private List<AudioRawDataListener> rawDataListeners;
    private AudioDataProcessor thread;
    private Mixer.Info mixerInfo;
    private AudioFormatConfig formatConfig;
    private AudioFormat format;

    public AudioInput(AudioFormatConfig formatConfig) {
        this.formatConfig = formatConfig;
        format = new AudioFormat(formatConfig.getSampleRate(), 
                formatConfig.getSampleSizeInBits(),
                formatConfig.getChannels(),
                formatConfig.isSigned(),
                formatConfig.isBigEndien());
        connectionListeners = new ArrayList<AudioConnectionListener>();
        dataListeners = new ArrayList<AudioDataListener>();
        rawDataListeners = new ArrayList<AudioRawDataListener>();
    }

    public void start() {
        stop();
        
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
            TargetDataLine line;
            if (mixerInfo == null) {
                line = (TargetDataLine)AudioSystem.getLine(info);
                line.close();
                thread = new AudioDataProcessor(line, connectionListeners, dataListeners, rawDataListeners);
                thread.start();
            } else {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                line = (TargetDataLine)mixer.getLine(info);
                line.close();
                thread = new AudioDataProcessor(line, connectionListeners, dataListeners, rawDataListeners);
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
        return formatConfig.getSampleRate() / 2.0;
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
    
    public void subscribeRawData(AudioRawDataListener listener) {
        if (!rawDataListeners.contains(listener)) {
            rawDataListeners.add(listener);
        }
    }

    public void unsubscribeRawData(AudioRawDataListener listener) {
        rawDataListeners.remove(listener);
    }

    private class AudioDataProcessor extends Thread {
        private boolean stopped;
        private boolean closed;
        private TargetDataLine line;
        private List<AudioConnectionListener> connectionListeners;
        private List<AudioDataListener> dataListeners;
        private List<AudioRawDataListener> rawDataListeners;

        public AudioDataProcessor(TargetDataLine line,
                List<AudioConnectionListener> connectionListeners,
                List<AudioDataListener> dataListeners,
                List<AudioRawDataListener> rawDataListeners) {
            this.line = line;
            this.connectionListeners = connectionListeners;
            this.dataListeners = dataListeners;
            this.rawDataListeners = rawDataListeners;
            this.closed = false;
        }

        @Override
        public void run() {
            stopped = false;

            try {
                line.open(format, formatConfig.getBufferSize());
                line.start();
                connectionListeners.stream().forEach(listener -> listener.lineOpened());
                int bytesRead;
                byte[] data = new byte[formatConfig.getBufferSize() / 4];
                long temp;
                double sample;
                while (!stopped) {
                    bytesRead = line.read(data, 0, data.length);
                    rawDataListeners.stream().forEach(listener -> listener.readData(data));
                    double[] samples = new double[bytesRead/2];
                    for (int i = 0; i < bytesRead/2; i++) {
                        temp = ((data[2*i] & 0xffL) << 8L) |
                                (data[2*i + 1] & 0xffL);
                        sample = (temp << 48) >> 48;
                        sample = sample / Math.pow(2, 15);
                        samples[i] = sample;
                    }
                    dataListeners.stream().forEach(listener -> listener.readData(samples));
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

        public boolean isStopped() {
            return stopped;
        }

        public boolean isClosed() {
            return closed;
        }
    }
}
