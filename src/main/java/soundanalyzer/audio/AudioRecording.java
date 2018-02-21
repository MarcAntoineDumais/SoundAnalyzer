package soundanalyzer.audio;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.config.AudioFormatConfig;

public class AudioRecording {
    
    private byte[] data;
    private ByteArrayOutputStream recording;
    private static final int PART_SIZE = 80;
    private int currentPart = 0;
    
    public AudioRecording() {
        reset();
    }
    
    public void reset() {
        recording = new ByteArrayOutputStream();
    }
    
    public void record(byte[] data) {
        recording.write(data, 0, data.length);
    }
    
    public void saveRecording() {
        if (recording != null) {
            data = recording.toByteArray();
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
        return getData(start, Math.max(start + PART_SIZE, data.length));
    }
    
    public String getDuration() {
        AudioFormatConfig config = ApplicationContextProvider.getApplicationContext().getBean(AudioFormatConfig.class);
        int secondsFull = recording.size() / (config.getSampleRate() * config.getSampleSizeInBits() / 8);
        String minutes = "" + secondsFull / 60;
        String seconds = "" + secondsFull % 60;
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds;
    }
}
