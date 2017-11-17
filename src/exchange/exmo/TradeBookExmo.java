package exchange.exmo;

import exchange.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import exchange.Client;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TradeBookExmo implements TradeBook {
	public final String apiUrl = "api.exmo.com/v1/trades/?pair=";
	private HashMap<String, ArrayList<Trade>> tradeBook = new HashMap<>();

	TradeBookExmo (InstrumentBookExmo currencyBook) {
		try {
			if (currencyBook == null)
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует список инструментов");
			for (Map.Entry entry : currencyBook.getListPair().entrySet()) {
				ArrayList<Trade> tradeList = new ArrayList<>();
				Trade trade;
				Instrument pair = currencyBook.getPair((String) entry.getKey());
				//Обработка ошибок, если подключение не пройдет
				Client client = new Client(Client.Protocol.HTTPS, apiUrl + pair.getName());
				JSONParser parserJSON = new JSONParser();
				JSONObject pairNameJSON = (JSONObject) parserJSON.parse(client.getData());
				JSONArray infoPairJSON = (JSONArray) pairNameJSON.get(pair.getName());

				for (Object tradeJSON : infoPairJSON) {
					JSONObject pairData = (JSONObject) tradeJSON;
					String tradeId = pairData.get("trade_id").toString();
					String type = pairData.get("type").toString();
					String price = pairData.get("price").toString();
					String quantity = pairData.get("quantity").toString();
					String amount = pairData.get("amount").toString();
					String date = pairData.get("date").toString();
					trade = new Trade(pair, Order.TypeOrder.valueOf(type.toUpperCase()), tradeId, quantity, price, amount, date);
					tradeList.add(trade);
				}
				tradeBook.put(pair.getName(), tradeList);
			}
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log(e, "Ошибка парсинга сделок, биржа Exmo");
		}
	}

	public ArrayList<Trade> getAllTradeList (String pairName) throws Throwable {
		ArrayList arrayTMP = new ArrayList();
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		if (tradeBook.containsKey(pairName))
			arrayTMP = tradeBook.get(pairName);
		return arrayTMP;
	}
	public ArrayList<Trade> getBuyTradeList (String pairName) throws Throwable {
		ArrayList arrayTMP = new ArrayList();
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		if (tradeBook.containsKey(pairName)) {
			for (Trade tradeTmp : tradeBook.get(pairName)) {
				if (tradeTmp.getType().equals(Order.TypeOrder.BUY))
					arrayTMP.add(tradeTmp);
			}
		}
		return arrayTMP;
	}
	public ArrayList<Trade> getSellTradeList (String pairName) throws Throwable {
		ArrayList arrayTMP = new ArrayList();
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		if (tradeBook.containsKey(pairName)) {
			for (Trade tradeTmp : tradeBook.get(pairName)) {
				if (tradeTmp.getType().equals(Order.TypeOrder.SELL))
					arrayTMP.add(tradeTmp);
			}
		}
		return arrayTMP;
	}
	public BigDecimal getBuyQuantity (String pairName) throws Throwable {
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		BigDecimal sumBuy = new BigDecimal(0);
		for (Trade tradeTmp : tradeBook.get(pairName)) {
			if (tradeTmp.getType().equals(Order.TypeOrder.BUY))
				sumBuy = sumBuy.add(tradeTmp.getQuantity());
		}
		return sumBuy;
	}
	public BigDecimal getBuyAmount(String pairName) throws Throwable {
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		BigDecimal sumBuy = new BigDecimal(0);
		for (Trade tradeTmp : tradeBook.get(pairName)) {
			if (tradeTmp.getType().equals(Order.TypeOrder.BUY))
				sumBuy = sumBuy.add(tradeTmp.getAmount());
		}
		return sumBuy;
	}
	public BigDecimal getSellQuantity (String pairName) throws Throwable {
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		BigDecimal sumBuy = new BigDecimal(0);
		for (Trade tradeTmp : tradeBook.get(pairName)) {
			if (tradeTmp.getType().equals(Order.TypeOrder.SELL))
				sumBuy = sumBuy.add(tradeTmp.getQuantity());
		}
		return sumBuy;
	}
	public BigDecimal getSellAmount(String pairName) throws Throwable {
		if (pairName == null || pairName.isEmpty())
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название инструмента");
		BigDecimal sumBuy = new BigDecimal(0);
		for (Trade tradeTmp : tradeBook.get(pairName)) {
			if (tradeTmp.getType().equals(Order.TypeOrder.SELL))
				sumBuy = sumBuy.add(tradeTmp.getAmount());
		}
		return sumBuy;
	}
}
