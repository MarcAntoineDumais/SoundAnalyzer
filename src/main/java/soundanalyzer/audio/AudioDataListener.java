package soundanalyzer.audio;

import soundanalyzer.model.RawPoint;

public interface AudioDataListener {
    public void readData(RawPoint[] data);
}
