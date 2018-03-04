package soundanalyzer.gui;

import javax.swing.JPanel;

import soundanalyzer.audio.AudioDataListener;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.audio.AudioOutput;
import soundanalyzer.audio.AudioRecording;
import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.gui.graph.DataGraph;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PlaybackPanel extends JPanel implements AudioDataListener {
    private static final long serialVersionUID = -1949557262848746731L;

    private JButton btnRecord, btnPlay, btnReset;
    private JLabel lblPlaybackState;
    private DataGraph dataGraph;
    
    private enum Status {UNINITIALIZED, RECORDING, PAUSED, PLAYING, FINISHED}
    
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
                    status = Status.PLAYING;
                    btnPlay.setText("Pause");
                    new Thread() {
                        @Override
                        public void run() {
                            while (status == Status.PLAYING) {
                                byte[] data = recording.getNextPart();
                                lblPlaybackState.setText(recording.getProgress());
                                if (data.length > 0) {
                                    audioOutput.write(data);
                                } else {
                                    status = Status.FINISHED;
                                    btnPlay.setEnabled(false);
                                }
                            }
                            
                        }
                    }.start();
                    break;
                case PLAYING:
                    audioOutput.flush();
                    status = Status.PAUSED;
                    btnPlay.setText("Play");
                    break;
                default:
                    break;
                }
            }
        });
        add(btnPlay);
        
        btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AudioOutput audioOutput = ApplicationContextProvider.getApplicationContext().getBean(AudioOutput.class);
                switch (status) {
                case PLAYING:
                    audioOutput.flush();
                case PAUSED:
                case FINISHED:
                    status = Status.PAUSED;
                    btnPlay.setText("Play");
                    btnPlay.setEnabled(true);
                    recording.reset();
                    lblPlaybackState.setText(recording.getProgress());
                    break;
                default:
                    break;
                }
            }
        });
        add(btnReset);

        dataGraph = new DataGraph();
        add(dataGraph);
    }

    @Override
    public void readData(byte[] data) {
        if (status == Status.RECORDING) {
            recording.record(data);
            lblPlaybackState.setText(recording.getDuration());
        }
    }
    
    public void connect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).subscribeData(this);
    }
    
    public void disconnect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).unsubscribeData(this);
    }
    
    private void startRecording() {
        status = Status.RECORDING;
        recording.eraseRecording();
        connect();
        btnRecord.setText("Stop");
        btnPlay.setEnabled(false);
        btnReset.setEnabled(false);
    }
    
    private void stopRecording() {
        if (status == Status.RECORDING) {
            disconnect();
            status = Status.PAUSED;
            btnRecord.setText("Record");
            btnPlay.setEnabled(true);
            btnReset.setEnabled(true);
            btnPlay.setText("Play");
            recording.saveRecording();
            lblPlaybackState.setText(recording.getProgress());

            dataGraph.loadData(recording.getData(0, recording.getDataLength()));
        }
    }
}