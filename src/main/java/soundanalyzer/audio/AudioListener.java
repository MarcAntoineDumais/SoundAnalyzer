package soundanalyzer.audio;

import soundanalyzer.model.RawPoint;

public interface AudioListener {
	public void readData(RawPoint[] data);
	public void lineOpened();
	public void lineClosed();
}
