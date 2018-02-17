package soundanalyzer.gui;

import javax.swing.JPanel;

import soundanalyzer.audio.AudioInput;
import soundanalyzer.audio.AudioOutput;
import soundanalyzer.audio.AudioRawDataListener;
import soundanalyzer.audio.AudioRecording;
import soundanalyzer.config.ApplicationContextProvider;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PlaybackPanel extends JPanel implements AudioRawDataListener{
    private static final long serialVersionUID = -1949557262848746731L;

    private JButton btnRecord, btnPlay;
    private JLabel lblPlaybackState;
    
    private enum Status {UNINITIALIZED, RECORDING, PAUSED, PLAYING}
    
    private Status status;
    private AudioRecording recording;
    
    public PlaybackPanel() {
        status = Status.UNINITIALIZED;
        recording = new AudioRecording();
        
        btnRecord = new JButton("Record");
        btnRecord.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (status == Status.RECORDING) {
                    stopRecording();
                } else {
                    startRecording();
                }
            }
        });
        add(btnRecord);
        
        lblPlaybackState = new JLabel("0:00");
        add(lblPlaybackState);
        
        btnPlay = new JButton("Play");
        btnPlay.setEnabled(false);
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioOutput audioOutput = ApplicationContextProvider.getApplicationContext().getBean(AudioOutput.class);
                switch (status) {
                case PAUSED:
                    //audioOutput.play();
                    audioOutput.write(recording.getRecording());
                    System.out.println("finished writing");
                    status = Status.PLAYING;
                    btnPlay.setText("Pause");
                    break;
                case PLAYING:
                    //audioOutput.pause();
                    status = Status.PAUSED;
                    btnPlay.setText("Play");
                    break;
                default:
                    break;
                }
            }
        });
        add(btnPlay);

    }

    @Override
    public void readData(byte[] data) {
        if (status == Status.RECORDING) {
            recording.record(data);
            lblPlaybackState.setText(recording.getDuration());
        }
    }
    
    public void connect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).subscribeRawData(this);
    }
    
    public void disconnect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).unsubscribeRawData(this);
    }
    
    public void startRecording() {
        status = Status.RECORDING;
        recording.reset();
        connect();
        btnRecord.setText("Stop");
        btnPlay.setEnabled(false);
    }
    
    public void stopRecording() {
        if (status == Status.RECORDING) {
            disconnect();
            status = Status.PAUSED;
            btnRecord.setText("Record");
            btnPlay.setEnabled(true);
            btnPlay.setText("Play");
            recording.saveRecording();
            //ApplicationContextProvider.getApplicationContext().getBean(AudioOutput.class).pause();
        }
    }
}