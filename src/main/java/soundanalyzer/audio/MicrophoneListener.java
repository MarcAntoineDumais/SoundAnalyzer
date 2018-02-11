package soundanalyzer.audio;

public interface MicrophoneListener {
	public void readData(double[] data);
	public void lineClosed();
}
