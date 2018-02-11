package soundanalyzer.gui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.audio.AudioListener;
import soundanalyzer.config.ApplicationContextProvider;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainPanel extends JPanel implements AudioListener{
	private static final long serialVersionUID = 2949687444255909471L;
	
	private MainWindow mainWindow;
	
	private AnalyzerService analyzerService;
	private AudioInput audioInput;
	private FourierGraphPanel fourierGraphPanel;
	
	public MainPanel(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		analyzerService = ApplicationContextProvider.getApplicationContext().getBean(AnalyzerService.class);
		audioInput = ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class);
		
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setLayout(new BorderLayout(0, 0));
		fourierGraphPanel = new FourierGraphPanel();
		add(fourierGraphPanel);
		
		JPanel settingsPanel = new JPanel();
		add(settingsPanel, BorderLayout.EAST);
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.X_AXIS));
		
		JPanel spacerPanel = new JPanel();
		spacerPanel.setPreferredSize(new Dimension(10, spacerPanel.getHeight()));
		settingsPanel.add(spacerPanel);
		
		JPanel frequencySettingsPanel = new JPanel();
		settingsPanel.add(frequencySettingsPanel);
		frequencySettingsPanel.setLayout(new BoxLayout(frequencySettingsPanel, BoxLayout.Y_AXIS));
		
		JLabel lblMaxFrequency = new JLabel("Max Frequency");
		lblMaxFrequency.setAlignmentX(Component.CENTER_ALIGNMENT);
		frequencySettingsPanel.add(lblMaxFrequency);

		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!slider.getValueIsAdjusting()) {
					new Thread() {
						@Override
						public void run() {
							audioInput.setMaxFrequency(slider.getValue());
							fourierGraphPanel.setMaxFrequency(slider.getValue());
						}
					}.start();
				}
			}
		});
		slider.setMinorTickSpacing(50);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(500);
		slider.setSnapToTicks(true);
		slider.setMinimum(50);
		slider.setMaximum(8000);
		slider.setValue(8000);
		slider.setOrientation(SwingConstants.VERTICAL);
		frequencySettingsPanel.add(slider);
	}

	public void recalculatePositions() {
		fourierGraphPanel.recalculatePositions();
	}
	
	@Override
	public void readData(double[] data) {
		if (data.length > 0) {
			fourierGraphPanel.addWaves(analyzerService.fourierTransform(data));
		}
	}

	@Override
	public void lineClosed() {
		mainWindow.microphoneLineClosed();
	}
	@Override
	public void lineOpened() {
		mainWindow.microphoneLineOpened();
	}
}
