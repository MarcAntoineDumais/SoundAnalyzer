package soundanalyzer.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Queue;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.analyzer.FormatConverter;
import soundanalyzer.audio.AudioDataListener;
import soundanalyzer.audio.AudioInput;
import soundanalyzer.config.ApplicationContextProvider;
import soundanalyzer.gui.graph.FourierGraph;
import soundanalyzer.gui.graph.RawGraph;

public class RealTimePanel extends JPanel implements AudioDataListener{
    private static final long serialVersionUID = 2949687444255909471L;

    private AnalyzerService analyzerService;
    private FormatConverter formatConverter;
    private RawGraph rawGraph;
    private FourierGraph fourierGraph;
    
    private Queue<Double> pointsToAnalyze;
    private static final int POINTS_PER_STEP = 1024;
    
    public RealTimePanel() {
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel rawPanel = new JPanel();
        rawPanel.setLayout(new BoxLayout(rawPanel, BoxLayout.X_AXIS));
        add(rawPanel);

        rawGraph = new RawGraph();
        rawGraph.setMinimumSize(new Dimension(400, 200));
        rawGraph.setMaximumSize(new Dimension(400, 200));
        rawPanel.add(rawGraph);

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
                rawGraph.setSpeed(speedSlider.getValue() / 20.0);
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
                double amplitude = Math.log(rawAmplitudeSlider.getValue() / 100.0 + 1.01) * 50;
                rawGraph.setAmplitude(amplitude);
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

        fourierGraph = new FourierGraph();
        fourierGraph.setMinimumSize(new Dimension(400, 200));
        fourierGraph.setMaximumSize(new Dimension(400, 200));
        fourierPanel.add(fourierGraph);

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
                fourierGraph.setMaxFrequency(frequencySlider.getValue());
            }
        });
        frequencySlider.setMinorTickSpacing(50);
        frequencySlider.setPaintLabels(true);
        frequencySlider.setMajorTickSpacing(500);
        frequencySlider.setSnapToTicks(true);
        frequencySlider.setMinimum(500);
        frequencySlider.setMaximum(4000);
        frequencySlider.setValue(4000);
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
                fourierGraph.setAmplitude(amplitude);
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
        
        pointsToAnalyze = new CircularFifoQueue<>(POINTS_PER_STEP);
        
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    synchronized(pointsToAnalyze) {
                        double[] points = new double[POINTS_PER_STEP];
                        Object[] toAnalyze = pointsToAnalyze.toArray();
                        if (toAnalyze.length >= POINTS_PER_STEP) {
                            for (int i = 0; i < POINTS_PER_STEP; i++) {
                                points[i] = (double)toAnalyze[i];
                            }
                            fourierGraph.addWaves(analyzerService.fourierTransform(points, false));
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void readData(byte[] data) {
        analyzerService = ApplicationContextProvider.getApplicationContext().getBean(AnalyzerService.class);
        formatConverter = ApplicationContextProvider.getApplicationContext().getBean(FormatConverter.class);
        double[] samples = formatConverter.rawToSamples(data);
        if (samples.length > 0) {
            for (double d :  samples) {
                pointsToAnalyze.add(d);
            }
            
            rawGraph.addPoints(samples);
        }
    }
    
    public void connect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).subscribeData(this);
    }
    
    public void disconnect() {
        ApplicationContextProvider.getApplicationContext().getBean(AudioInput.class).unsubscribeData(this);
    }
}
