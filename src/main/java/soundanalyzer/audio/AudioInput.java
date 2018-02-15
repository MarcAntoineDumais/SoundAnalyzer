package soundanalyzer.audio;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import soundanalyzer.model.RawPoint;

@Service
public class AudioInput {
    private List<AudioConnectionListener> connectionListeners;
    private List<AudioDataListener> dataListeners;
    private AudioDataProcessor thread;
    private Mixer.Info mixerInfo;

    private AudioFormat format;
    @Autowired
    private AudioOutput audioOutput;
    
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 1;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIEN = true;

    public AudioInput() {
        connectionListeners = new ArrayList<AudioConnectionListener>();
        dataListeners = new ArrayList<AudioDataListener>();
        format = new AudioFormat(16000, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIEN);
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
        return format.getSampleRate() / 2.0;
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

        public AudioDataProcessor(TargetDataLine line, List<AudioConnectionListener> connectionlisteners, List<AudioDataListener> datalisteners) {
            this.line = line;
            this.connectionListeners = connectionlisteners;
            this.dataListeners = datalisteners;
            this.closed = false;
        }

        @Override
        public void run() {
            stopped = false;

            try {
                line.open(format, 1024);
                line.start();
                connectionListeners.stream().forEach(listener -> listener.lineOpened());
                int bytesRead;
                byte[] data = new byte[line.getBufferSize()];
                long temp, t, elapsed;
                double sample;
                long lastTime = System.currentTimeMillis();
                while (!stopped) {
                    bytesRead = line.read(data, 0, data.length);
                    audioOutput.write(data);
                    t = System.currentTimeMillis();
                    elapsed = lastTime - t;
                    lastTime = t;
                    RawPoint[] samples = new RawPoint[bytesRead/2];
                    for (int i = 0; i < bytesRead/2; i++) {
                        temp = ((data[2*i] & 0xffL) << 8L) |
                                (data[2*i + 1] & 0xffL);
                        sample = (temp << 48) >> 48;
                        sample = sample / Math.pow(2, 15);
                        samples[i] = new RawPoint(sample, lastTime + (bytesRead/2 - i - 1) * elapsed / (bytesRead/2.0));
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
