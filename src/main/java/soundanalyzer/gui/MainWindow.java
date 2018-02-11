package soundanalyzer.gui;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import soundanalyzer.audio.MicrophoneInput;
import soundanalyzer.config.ApplicationContextProvider;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = -4539965066131036060L;
	
	@Autowired
	private MicrophoneInput microphoneInput;
	
	private MainPanel mainPanel;
	private JMenuItem mntmStartRecording, mntmStopRecording;

	public MainWindow() {
		microphoneInput = ApplicationContextProvider.getApplicationContext().getBean(MicrophoneInput.class);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnMicrophone = new JMenu("Microphone");
		menuBar.add(mnMicrophone);
		
		mntmStartRecording = new JMenuItem("Start Recording");
		mntmStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				microphoneInput.start();
				mntmStartRecording.setEnabled(false);
				mntmStopRecording.setEnabled(true);
			}
		});
		mnMicrophone.add(mntmStartRecording);
		
		mntmStopRecording = new JMenuItem("Stop Recording");
		mntmStopRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				microphoneInput.stop();
				mntmStartRecording.setEnabled(true);
				mntmStopRecording.setEnabled(false);
			}
		});
		mntmStopRecording.setEnabled(false);
		mnMicrophone.add(mntmStopRecording);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		mainPanel = new MainPanel();
		microphoneInput.subscribe(mainPanel);
		contentPane.add(mainPanel, BorderLayout.CENTER);
		setContentPane(contentPane);
		
		validate();
		pack();
		setLocationRelativeTo(null);
		mainPanel.recalculatePositions();
	}
	
	public void microphoneLineClosed() {
		mntmStartRecording.setEnabled(true);
		mntmStopRecording.setEnabled(false);
	}
}
