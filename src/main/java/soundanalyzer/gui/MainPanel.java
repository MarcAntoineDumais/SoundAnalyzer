package soundanalyzer.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainPanel extends JPanel {
    private static final long serialVersionUID = -5429488492907735884L;
    
    private RealTimePanel realTimePanel;
    private PlaybackPanel playbackPanel;

    public MainPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (realTimePanel == null || playbackPanel == null) {
                    return;
                }
                if (tabbedPane.getSelectedIndex() == 0) {
                    realTimePanel.connect();
                    playbackPanel.disconnect();
                } else {
                    playbackPanel.connect();
                    realTimePanel.disconnect();
                }
            }
        });
        add(tabbedPane, BorderLayout.CENTER);
        
        realTimePanel = new RealTimePanel();
        tabbedPane.addTab("Real time", null, realTimePanel, null);
        
        playbackPanel = new PlaybackPanel();
        tabbedPane.addTab("Playback", null, playbackPanel, null);
    }
}
