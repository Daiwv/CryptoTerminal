package exchange.exmo;

import exchange.Log;
import exchange.LogUnit;
import exchange.Ticker;
import exchange.TickerBook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import exchange.Client;

import java.math.BigDecimal;
import java.util.HashMap;

public class TickerBookExmo implements TickerBook {
	private final String apiUrl = "api.exmo.com/v1/ticker/";
	private HashMap<String, Ticker> tickerBook = new HashMap<>();

	public TickerBookExmo () {
		try {
			BigDecimal buyMaxPrice, sellMinPrice, lastPrice, highPrice, lowPrice, avgPrice, quantity, amount;
			Long date;
			Client client = new Client(Client.Protocol.HTTPS, apiUrl);
			JSONParser parserJSON = new JSONParser();
			JSONObject tickersJSON = (JSONObject) parserJSON.parse(client.getData());
			for (Object pairJSON : tickersJSON.keySet()) {
				JSONObject pairTmp = (JSONObject) tickersJSON.get(pairJSON);
				buyMaxPrice = new BigDecimal(pairTmp.get("buy_price").toString());
				sellMinPrice = new BigDecimal(pairTmp.get("sell_price").toString());
				lastPrice = new BigDecimal(pairTmp.get("last_trade").toString());
				highPrice = new BigDecimal(pairTmp.get("high").toString());
				lowPrice = new BigDecimal(pairTmp.get("low").toString());
				avgPrice = new BigDecimal(pairTmp.get("avg").toString());
				quantity = new BigDecimal(pairTmp.get("vol").toString());
				amount = new BigDecimal(pairTmp.get("vol_curr").toString());
				date = Long.parseLong(pairTmp.get("updated").toString());
				tickerBook.put(pairJSON.toString(), new Ticker(pairJSON.toString(), highPrice.toString(), lowPrice.toString(), avgPrice.toString(), amount.toString(), quantity.toString(), lastPrice.toString(), buyMaxPrice.toString(), sellMinPrice.toString(), date.toString()));
			}
		} catch (Exception e) {
			new Log(e, "Ошибка парсинга котировок, биржа Exmo");
		}
	}

	public Ticker getTicker(String pairName) {
		Ticker ticker = new Ticker();
		try {
			if (pairName == null || pairName.isEmpty())
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
			if (tickerBook.containsKey(pairName))
				ticker = tickerBook.get(pairName);
		} catch (Log log) {
			new Log(log);
		}
		return ticker;
	}
}
