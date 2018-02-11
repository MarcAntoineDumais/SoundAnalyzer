package soundanalyzer.gui;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import soundanalyzer.analyzer.AnalyzerService;
import soundanalyzer.audio.MicrophoneListener;
import soundanalyzer.config.ApplicationContextProvider;

public class MainPanel extends JPanel implements MicrophoneListener{
	private static final long serialVersionUID = 2949687444255909471L;
	
	private AnalyzerService analyzerService;
	private FourierGraphPanel fourierGraphPanel;
	
	public MainPanel() {
		analyzerService = ApplicationContextProvider.getApplicationContext().getBean(AnalyzerService.class);
		
		setBorder(new EmptyBorder(0, 0, 0, 0));
		fourierGraphPanel = new FourierGraphPanel();
		add(fourierGraphPanel);
	}

	public void recalculatePositions() {
		fourierGraphPanel.recalculatePositions();
	}
	
	@Override
	public void readData(double[] data) {
		fourierGraphPanel.addWaves(analyzerService.fourierTransform(data));
	}

	@Override
	public void lineClosed() {
		((MainWindow) getTopLevelAncestor()).microphoneLineClosed();
	}
}
