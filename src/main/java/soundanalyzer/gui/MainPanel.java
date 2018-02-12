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
		
		JLabel lblMaxFrequency = new JLabel("Max Frequency (Hz)");
		lblMaxFrequency.setAlignmentX(Component.CENTER_ALIGNMENT);
		frequencySettingsPanel.add(lblMaxFrequency);

		JSlider frequencySlider = new JSlider();
		frequencySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (!frequencySlider.getValueIsAdjusting()) {
					new Thread() {
						@Override
						public void run() {
							int value = Math.max(50, frequencySlider.getValue());
							audioInput.setMaxFrequency(value);
							fourierGraphPanel.setMaxFrequency(value);
						}
					}.start();
				}
			}
		});
		frequencySlider.setMinorTickSpacing(50);
		frequencySlider.setPaintLabels(true);
		frequencySlider.setMajorTickSpacing(1000);
		frequencySlider.setSnapToTicks(true);
		frequencySlider.setMinimum(0);
		frequencySlider.setMaximum(8000);
		frequencySlider.setValue(8000);
		frequencySlider.setOrientation(SwingConstants.VERTICAL);
		frequencySettingsPanel.add(frequencySlider);
		
		JPanel spacerPanel2 = new JPanel();
		spacerPanel2.setPreferredSize(new Dimension(10, spacerPanel2.getHeight()));
		settingsPanel.add(spacerPanel2);
		
		JPanel amplitudeSettingsPanel = new JPanel();
		settingsPanel.add(amplitudeSettingsPanel);
		amplitudeSettingsPanel.setLayout(new BoxLayout(amplitudeSettingsPanel, BoxLayout.Y_AXIS));
		
		JLabel lblMaxAmplitude = new JLabel("Amplitude (%)");
		lblMaxAmplitude.setAlignmentX(Component.CENTER_ALIGNMENT);
		amplitudeSettingsPanel.add(lblMaxAmplitude);

		JSlider amplitudeSlider = new JSlider();
		amplitudeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double amplitude = Math.pow(10, (amplitudeSlider.getValue() / 100.0 - 0.5) * 4.0);
				fourierGraphPanel.setAmplitude(amplitude);
			}
		});
		amplitudeSlider.setMinorTickSpacing(1);
		amplitudeSlider.setPaintLabels(true);
		amplitudeSlider.setMajorTickSpacing(10);
		amplitudeSlider.setSnapToTicks(true);
		amplitudeSlider.setMinimum(0);
		amplitudeSlider.setMaximum(100);
		amplitudeSlider.setValue(50);
		amplitudeSlider.setOrientation(SwingConstants.VERTICAL);
		amplitudeSettingsPanel.add(amplitudeSlider);
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
