package soundanalyzer.gui;
import java.awt.BorderLayout;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import soundanalyzer.audio.AudioInput;
import soundanalyzer.config.ApplicationContextProvider;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = -4539965066131036060L;
	
	@Autowired
	private AudioInput audioInput;
	
	private MainPanel mainPanel;
	private JMenuItem mntmStartMicrophone, mntmStopInput;
	private JMenuItem mntmStartSoundcard;

	public MainWindow() {
		audioInput = ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnAudioInput = new JMenu("Input");
		menuBar.add(mnAudioInput);
		
		mntmStartMicrophone = new JMenuItem("Use Microphone");
		mntmStartMicrophone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						audioInput.setMixerInfo(null);
						audioInput.start();
					}
				}.start();
			}
		});
		mnAudioInput.add(mntmStartMicrophone);
		
		mntmStartSoundcard = new JMenuItem("Use Soundcard");
		mntmStartSoundcard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
				Mixer.Info result = (Mixer.Info)JOptionPane.showInputDialog(
						null,
						"Please select the audio input source:",
						"Input Source Selection",
						JOptionPane.QUESTION_MESSAGE,
						null, mixerInfo, null);
				
				new Thread() {
					@Override
					public void run() {
						if (result != null) {
							audioInput.setMixerInfo(result);
							audioInput.start();
						}						
					}
				}.start();
			}
		});
		mnAudioInput.add(mntmStartSoundcard);
		
		mntmStopInput = new JMenuItem("Stop Input");
		mntmStopInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						audioInput.stop();
					}
				}.start();
			}
		});
		mntmStopInput.setEnabled(false);
		mnAudioInput.add(mntmStopInput);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		mainPanel = new MainPanel(this);
		audioInput.subscribe(mainPanel);
		contentPane.add(mainPanel, BorderLayout.CENTER);
		setContentPane(contentPane);
		
		validate();
		pack();
		setLocationRelativeTo(null);
		mainPanel.recalculatePositions();
	}
	
	public void microphoneLineClosed() {
		mntmStartMicrophone.setEnabled(true);
		mntmStartSoundcard.setEnabled(true);
		mntmStopInput.setEnabled(false);
	}
	
	public void microphoneLineOpened() {
		mntmStartMicrophone.setEnabled(false);
		mntmStartSoundcard.setEnabled(false);
		mntmStopInput.setEnabled(true);
	}
}
