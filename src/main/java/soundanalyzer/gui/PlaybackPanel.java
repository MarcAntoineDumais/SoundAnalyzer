package soundanalyzer.gui;

import javax.swing.JPanel;

import soundanalyzer.audio.AudioDataListener;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.model.RawPoint;
import javax.swing.JButton;
import javax.swing.JLabel;

public class PlaybackPanel extends JPanel implements AudioDataListener{
    private static final long serialVersionUID = -1949557262848746731L;

    public PlaybackPanel() {
        
        JButton btnRecord = new JButton("Record");
        add(btnRecord);
        
        JLabel lblPlaybackState = new JLabel("0");
        add(lblPlaybackState);
        
        JButton btnPlay = new JButton("Play");
        add(btnPlay);

    }

    @Override
    public void readData(RawPoint[] data) {
        // TODO Auto-generated method stub
        
    }
    
    public void connect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).subscribeData(this);
    }
    
    public void disconnect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).unsubscribeData(this);
    }
}
