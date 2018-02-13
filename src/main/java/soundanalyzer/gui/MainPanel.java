package soundanalyzer.gui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.audio.AudioListener;
import soundanalyzer.config.ApplicationContextProvider;
import org.apache.commons.lang3.ArrayUtils;
import soundanalyzer.model.RawPoint;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Arrays;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class MainPanel extends JPanel implements AudioListener{
    private static final long serialVersionUID = 2949687444255909471L;

    private MainWindow mainWindow;

    private AnalyzerService analyzerService;
    private AudioInput audioInput;
    private RawGraphPanel rawGraphPanel;
    private FourierGraphPanel fourierGraphPanel;

    public MainPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        analyzerService = ApplicationContextProvider.getApplicationContext().getBean(AnalyzerService.class);
        audioInput = ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class);

        setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel rawPanel = new JPanel();
        rawPanel.setLayout(new BoxLayout(rawPanel, BoxLayout.X_AXIS));
        add(rawPanel);

        rawGraphPanel = new RawGraphPanel();
        rawGraphPanel.setMaximumSize(new Dimension(400, 200));
        rawPanel.add(rawGraphPanel);

        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(10, spacerPanel.getHeight()));
        rawPanel.add(spacerPanel);

        JPanel speedSettingsPanel = new JPanel();
        rawPanel.add(speedSettingsPanel);
        speedSettingsPanel.setLayout(new BoxLayout(speedSettingsPanel, BoxLayout.Y_AXIS));

        JLabel lblSpeed = new JLabel("Speed");
        lblSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
        speedSettingsPanel.add(lblSpeed);

        JSlider speedSlider = new JSlider();
        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                rawGraphPanel.setSpeed(speedSlider.getValue() / 20.0);
            }
        });
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintLabels(false);
        speedSlider.setMajorTickSpacing(5);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMinimum(1);
        speedSlider.setMaximum(21);
        speedSlider.setValue(8);
        speedSlider.setOrientation(SwingConstants.VERTICAL);
        speedSettingsPanel.add(speedSlider);

        JPanel spacerPanel2 = new JPanel();
        spacerPanel2.setPreferredSize(new Dimension(10, spacerPanel2.getHeight()));
        rawPanel.add(spacerPanel2);

        JPanel rawAmplitudeSettingsPanel = new JPanel();
        rawPanel.add(rawAmplitudeSettingsPanel);
        rawAmplitudeSettingsPanel.setLayout(new BoxLayout(rawAmplitudeSettingsPanel, BoxLayout.Y_AXIS));

        JLabel lblRawMaxAmplitude = new JLabel("Amplitude (%)");
        lblRawMaxAmplitude.setAlignmentX(Component.CENTER_ALIGNMENT);
        rawAmplitudeSettingsPanel.add(lblRawMaxAmplitude);

        JSlider rawAmplitudeSlider = new JSlider();
        rawAmplitudeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double amplitude = Math.pow(10, (rawAmplitudeSlider.getValue() / 100.0 - 0.5) * 4.0);
                rawGraphPanel.setAmplitude(amplitude);
            }
        });
        rawAmplitudeSlider.setMinorTickSpacing(1);
        rawAmplitudeSlider.setPaintLabels(true);
        rawAmplitudeSlider.setMajorTickSpacing(10);
        rawAmplitudeSlider.setSnapToTicks(true);
        rawAmplitudeSlider.setMinimum(0);
        rawAmplitudeSlider.setMaximum(100);
        rawAmplitudeSlider.setValue(50);
        rawAmplitudeSlider.setOrientation(SwingConstants.VERTICAL);
        rawAmplitudeSettingsPanel.add(rawAmplitudeSlider);

        JPanel verticalSpacerPanel = new JPanel();
        verticalSpacerPanel.setPreferredSize(new Dimension(verticalSpacerPanel.getWidth(), 10));
        add(verticalSpacerPanel);

        JPanel fourierPanel = new JPanel();
        fourierPanel.setLayout(new BoxLayout(fourierPanel, BoxLayout.X_AXIS));
        add(fourierPanel);

        fourierGraphPanel = new FourierGraphPanel();
        fourierGraphPanel.setMaximumSize(new Dimension(400, 200));
        fourierPanel.add(fourierGraphPanel);

        JPanel spacerPanel3 = new JPanel();
        spacerPanel3.setPreferredSize(new Dimension(10, spacerPanel3.getHeight()));
        fourierPanel.add(spacerPanel3);

        JPanel frequencySettingsPanel = new JPanel();
        fourierPanel.add(frequencySettingsPanel);
        frequencySettingsPanel.setLayout(new BoxLayout(frequencySettingsPanel, BoxLayout.Y_AXIS));

        JLabel lblMaxFrequency = new JLabel("Max Frequency (Hz)");
        lblMaxFrequency.setAlignmentX(Component.CENTER_ALIGNMENT);
        frequencySettingsPanel.add(lblMaxFrequency);

        JSlider frequencySlider = new JSlider();
        frequencySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fourierGraphPanel.setMaxFrequency(frequencySlider.getValue());
            }
        });
        frequencySlider.setMinorTickSpacing(50);
        frequencySlider.setPaintLabels(true);
        frequencySlider.setMajorTickSpacing(1000);
        frequencySlider.setSnapToTicks(true);
        frequencySlider.setMinimum(1000);
        frequencySlider.setMaximum(8000);
        frequencySlider.setValue(8000);
        frequencySlider.setOrientation(SwingConstants.VERTICAL);
        frequencySettingsPanel.add(frequencySlider);

        JPanel spacerPanel4 = new JPanel();
        spacerPanel4.setPreferredSize(new Dimension(10, spacerPanel4.getHeight()));
        fourierPanel.add(spacerPanel4);

        JPanel amplitudeSettingsPanel = new JPanel();
        fourierPanel.add(amplitudeSettingsPanel);
        amplitudeSettingsPanel.setLayout(new BoxLayout(amplitudeSettingsPanel, BoxLayout.Y_AXIS));

        JLabel lblMaxAmplitude = new JLabel("Amplitude (%)");
        lblMaxAmplitude.setAlignmentX(Component.CENTER_ALIGNMENT);
        amplitudeSettingsPanel.add(lblMaxAmplitude);

        JSlider fourierAmplitudeSlider = new JSlider();
        fourierAmplitudeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double amplitude = Math.pow(10, (fourierAmplitudeSlider.getValue() / 100.0 - 0.5) * 4.0);
                fourierGraphPanel.setAmplitude(amplitude);
            }
        });
        fourierAmplitudeSlider.setMinorTickSpacing(1);
        fourierAmplitudeSlider.setPaintLabels(true);
        fourierAmplitudeSlider.setMajorTickSpacing(10);
        fourierAmplitudeSlider.setSnapToTicks(true);
        fourierAmplitudeSlider.setMinimum(0);
        fourierAmplitudeSlider.setMaximum(100);
        fourierAmplitudeSlider.setValue(50);
        fourierAmplitudeSlider.setOrientation(SwingConstants.VERTICAL);
        amplitudeSettingsPanel.add(fourierAmplitudeSlider);
    }

    public void recalculatePositions() {
        fourierGraphPanel.recalculatePositions();
    }

    @Override
    public void readData(RawPoint[] data) {
        if (data.length > 0) {
            rawGraphPanel.addPoints(Arrays.asList(data));
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
