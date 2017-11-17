package exchange.exmo;

import exchange.*;
import exchange.Instrument;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import view.Table;

import java.math.BigDecimal;
import java.util.ArrayList;

public class OrderBookExmo implements OrderBook {
	public final String apiUrl = "api.exmo.com/v1/order_book/?pair=";
	private InstrumentBookExmo currencyBook;

	OrderBookExmo (InstrumentBookExmo currencyBook) {
		this.currencyBook = currencyBook;
	}

	public ArrayList<Table> getBuyOrderList (String pairName) {
		ArrayList<Table> orderList = new ArrayList<>();
		try {
			Instrument pair = currencyBook.getPair(pairName);
			Client client = new Client(Client.Protocol.HTTPS, apiUrl + pair.getName() + "&limit=1000");
			JSONParser parserJSON = new JSONParser();
			JSONObject pairNameJSON = (JSONObject) parserJSON.parse(client.getData());
			JSONObject infoPairJSON = (JSONObject) pairNameJSON.get(pair.getName());
			JSONArray buyOrdersListJSON = (JSONArray) infoPairJSON.get("bid");

			for (Object orderJSON : buyOrdersListJSON) {
				JSONArray arrData = (JSONArray) orderJSON;
				Order order = new Order(
						Order.TypeCreateOrder.EXCHANGE_ORDER,
						pair,
						Order.TypeOrder.BUY,
						(String) arrData.get(0),
						(String) arrData.get(1),
						(String) arrData.get(2)
				);
				if (!order.isNullObject())
					orderList.add(order);
			}
		} catch (Exception e) {
			new Log (e, "Ошибка во время получения списка ордеров на продажу ("+pairName+")");
		}
		return orderList;
	}
	public ArrayList<Table> getSellOrderList (String pairName) {
		ArrayList<Table> orderList = new ArrayList<>();
		try {
			Instrument pair = currencyBook.getPair(pairName);
			//Обработка ошибок, если подключение не пройдет
			Client client = new Client(Client.Protocol.HTTPS, apiUrl + pair.getName() + "&limit=1000");
			JSONParser parserJSON = new JSONParser();
			JSONObject pairNameJSON = (JSONObject) parserJSON.parse(client.getData());
			JSONObject infoPairJSON = (JSONObject) pairNameJSON.get(pair.getName());
			JSONArray sellOrdersListJSON = (JSONArray) infoPairJSON.get("ask");

			for (Object orderJSON : sellOrdersListJSON) {
				JSONArray arrData = (JSONArray) orderJSON;
				Order order = new Order(
						Order.TypeCreateOrder.EXCHANGE_ORDER,
						pair,
						Order.TypeOrder.SELL,
						(String) arrData.get(0),
						(String) arrData.get(1),
						(String) arrData.get(2)
				);
				orderList.add(order);
			}
		} catch (Exception e) {
			new Log (e, "Ошибка во время получения списка ордеров на продажу ("+pairName+")");
		}
		return orderList;
	}
	//Получать эти значения с биржи, а потом со своего сервера
	public BigDecimal getBuyQuantity (String pairName) {
		BigDecimal sumBuy = new BigDecimal(0);
		/*for (Order orderTmp : orderBook.get(pairName)) {
			if (orderTmp.getType().equals(Order.TypeOrder.BUY))
				sumBuy = sumBuy.add(orderTmp.getQuantity());
		}*/
		return sumBuy;
	}
	public BigDecimal getBuyAmount(String pairName){
		BigDecimal sumBuy = new BigDecimal(0);
		/*for (Order orderTmp : orderBook.get(pairName)) {
			if (orderTmp.getType().equals(Order.TypeOrder.BUY))
				sumBuy = sumBuy.add(orderTmp.getAmount());
		}*/
		return sumBuy;
	}
	public BigDecimal getSellQuantity (String pairName){
		BigDecimal sumBuy = new BigDecimal(0);
		/*for (Order orderTmp : orderBook.get(pairName)) {
			if (orderTmp.getType().equals(Order.TypeOrder.SELL))
				sumBuy = sumBuy.add(orderTmp.getQuantity());
		}*/
		return sumBuy;
	}
	public BigDecimal getSellAmount(String pairName){
		BigDecimal sumBuy = new BigDecimal(0);
		/*for (Order orderTmp : orderBook.get(pairName)) {
			if (orderTmp.getType().equals(Order.TypeOrder.SELL))
				sumBuy = sumBuy.add(orderTmp.getAmount());
		}*/
		return sumBuy;
	}
}
