package soundanalyzer.audio;

public interface MicrophoneListener {
	public void readData(double[] data);
	public void lineOpened();
	public void lineClosed();
}
