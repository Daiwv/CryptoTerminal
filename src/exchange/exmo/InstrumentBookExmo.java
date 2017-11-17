package exchange.exmo;

import exchange.*;
import exchange.Instrument;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;

public class InstrumentBookExmo implements InstrumentBook {
	private final static String apiUrl = "api.exmo.com/v1/pair_settings/";
	private static ArrayList<String> namePairList;
	private static HashMap<String, Instrument> listPair;

	public InstrumentBookExmo() {
		try {
			String apiData = new Client(Client.Protocol.HTTPS, apiUrl).getData();
			namePairList = new ArrayList<>();
			listPair = new HashMap<>();
			JSONParser parser = new JSONParser();
			JSONObject currency = (JSONObject) parser.parse(apiData);

			for (Object pairName : currency.keySet().toArray()) {
				namePairList.add(pairName.toString());
				JSONObject pair = (JSONObject) currency.get(pairName);
				Instrument cpTmp = new Instrument(
						pairName.toString(),
						pair.get("min_quantity").toString(),
						pair.get("max_quantity").toString(),
						pair.get("min_price").toString(),
						pair.get("max_price").toString(),
						pair.get("max_amount").toString(),
						pair.get("min_amount").toString()
				);
				listPair.put(pairName.toString(), cpTmp);
			}
		} catch (Exception e) {
			new Log(e, "Ошибка получения списка валют, биржа Exmo");
		}
	}
	public HashMap<String, Instrument> getListPair () {
		return listPair;
	}
	public ArrayList<String> getNamePairList () {
		return namePairList;
	}
	public Instrument getPair (String namePair) {
		try {
			namePair = namePair.toUpperCase();
			if (listPair.containsKey(namePair)) {
				return listPair.get(namePair);
			} else {
				throw new Log(LogUnit.TypeLog.ERROR, "Такой валютной пары не существует " + "("+namePair+")");
			}
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, "Ошибка при доступе к валютной паре по имени");
		}
		return null;
	}
}