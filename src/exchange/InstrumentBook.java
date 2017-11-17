package exchange;

import java.util.ArrayList;
import java.util.HashMap;

public interface InstrumentBook {
	public HashMap<String, Instrument> getListPair ();
	public ArrayList<String> getNamePairList ();
	public Instrument getPair (String namePair);
}
