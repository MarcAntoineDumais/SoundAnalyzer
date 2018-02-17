package soundanalyzer.audio;

import java.io.ByteArrayOutputStream;

public class AudioRecording {
    
    private byte[] data;
    private ByteArrayOutputStream recording;
    
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
            this.data = recording.toByteArray();
        }
    }
    
    public byte[] getRecording() {
        return data;
    }
    
    public String getDuration() {
        int secondsFull = recording.size() / 32000;
        String minutes = "" + secondsFull / 60;
        String seconds = "" + secondsFull % 60;
        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }
        return minutes + ":" + seconds;
    }
}
