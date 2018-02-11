package soundanalyzer.audio;

public interface AudioListener {
	public void readData(double[] data);
	public void lineOpened();
	public void lineClosed();
}
