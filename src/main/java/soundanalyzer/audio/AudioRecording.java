package soundanalyzer.audio;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.config.AudioConfig;

public class AudioRecording {
    
    private byte[] data;
    private ByteArrayOutputStream recording;
    private static final int PART_SIZE = 80;
    private int currentPart = 0;
    
    public AudioRecording() {
        eraseRecording();
    }
    
    public void eraseRecording() {
        recording = new ByteArrayOutputStream();
    }
    
    public void reset() {
        currentPart = 0;
    }
    
    public void record(byte[] data) {
        recording.write(data, 0, data.length);
    }
    
    public void saveRecording() {
        if (recording != null) {
            data = recording.toByteArray();
            currentPart = 0;
        }
    }
    
    public int getDataLength() {
        return data.length;
    }
    
    public byte[] getRemainingData(int remaining) {
        return Arrays.copyOfRange(data, data.length - remaining, data.length);
    }
    
    public byte[] getData(int start, int end) {
        return Arrays.copyOfRange(data, start, end);
    }
    
    public byte[] getNextPart() {
        int start = currentPart * PART_SIZE;
        currentPart++;
        return getData(Math.min(start, data.length), Math.min(start + PART_SIZE, data.length));
    }
    
    public String getDuration() {
        AudioConfig config = ApplicationContextProvider.getApplicationContext().getBean(AudioConfig.class);
        int secondsFull = recording.size() / (config.getFormat().getSampleRate() * config.getFormat().getSampleSizeInBits() / 8);
        String minutes = "" + secondsFull / 60;
        String seconds = "" + secondsFull % 60;
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds;
    }
    
    public String getProgress() {
        AudioConfig config = ApplicationContextProvider.getApplicationContext().getBean(AudioConfig.class);
        int secondsFull = currentPart * PART_SIZE / (config.getFormat().getSampleRate() * config.getFormat().getSampleSizeInBits() / 8);
        String minutes = "" + secondsFull / 60;
        String seconds = "" + secondsFull % 60;
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds + " / " + getDuration();
    }
}
