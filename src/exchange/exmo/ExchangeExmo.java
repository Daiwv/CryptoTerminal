package exchange.exmo;

import exchange.*;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import view.Table;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExchangeExmo extends Exchange {
	private final String nameExchange = "Exmo";
	private AuthEx authEx = new AuthEx();
	public static final BigDecimal commissionExchange = new BigDecimal("0.002");
	private static long nonce, uId, serverDate;
	private HashMap<String, BigDecimal> balancesBook = new HashMap<>(), reservedBook = new HashMap<>();
	private TickerBookExmo tickerBookExmo;
	private InstrumentBookExmo currencyBookExmo;
	private OrderBookExmo orderBookExmo;

	public ExchangeExmo() {
		try {
			File file = new File (AuthEx.path + "/" + nameExchange + AuthEx.formatFile);
			if (!file.exists())
				throw new Log (LogUnit.TypeLog.MESSAGE, nameExchange + " | Отсутствует файл авторизации на бирже");
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			authEx = (AuthEx) ois.readObject();
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка во время авторизации на бирже");
		}

		try {
			tickerBookExmo = new TickerBookExmo();
			currencyBookExmo = new InstrumentBookExmo();
			orderBookExmo = new OrderBookExmo(currencyBookExmo);

			String responseJSON = new String();
			responseJSON = request("user_info", new HashMap<>());

			if (!responseJSON.isEmpty()) {
				JSONParser parser = new JSONParser();
				JSONObject infoJSON = (JSONObject) parser.parse(responseJSON);
				uId = Long.parseLong(infoJSON.get("uid").toString());
				serverDate = Long.parseLong(infoJSON.get("server_date").toString());
				JSONObject balancesJsonArray = (JSONObject) infoJSON.get("balances");
				JSONObject reservedJsonArray = (JSONObject) infoJSON.get("reserved");

				for (Object entry : balancesJsonArray.keySet()) {
					balancesBook.put(
							entry.toString(),
							new BigDecimal(balancesJsonArray.get(entry.toString()).toString())
					);
				}

				for (Object entry : reservedJsonArray.keySet()) {
					reservedBook.put(
							entry.toString(),
							new BigDecimal(balancesJsonArray.get(entry.toString()).toString())
					);
				}
			}
		} catch (Exception e) {
			new Log(e, "Ошибка аутентификации на бирже Exmo");
		}
	}

	protected final String request(String method, HashMap<String, String> arguments) {
		String responseStr = new String();
		try {
			if (authEx.isEmpty())
				throw new Log(LogUnit.TypeLog.MESSAGE, this.getClass().getName() + " | Невозможно выполнить запрос. Вы не авторизованы на бирже");

			Mac mac;
			SecretKeySpec key;
			String sign;

			this.nonce = System.nanoTime();
			arguments.put("nonce", "" + ++nonce);  // Add the dummy nonce.

			String postData = "";

			for (Map.Entry<String, String> stringStringEntry : arguments.entrySet()) {
				Map.Entry argument = (Map.Entry) stringStringEntry;

				if (postData.length() > 0) {
					postData += "&";
				}
				postData += argument.getKey() + "=" + argument.getValue();
			}

			// Create a new secret key
			try {
				key = new SecretKeySpec(authEx.getSecretKey().getBytes("UTF-8"), "HmacSHA512");
			} catch (UnsupportedEncodingException uee) {
				System.err.println("Unsupported encoding exception: " + uee.toString());
				return null;
			}

			// Create a new mac
			try {
				mac = Mac.getInstance("HmacSHA512");
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println("No such algorithm exception: " + nsae.toString());
				return null;
			}

			// Init mac with key.
			try {
				mac.init(key);
			} catch (InvalidKeyException ike) {
				System.err.println("Invalid key exception: " + ike.toString());
				return null;
			}


			// Encode the post data by the secret and encode the result as base64.
			try {
				sign = Hex.encodeHexString(mac.doFinal(postData.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException uee) {
				System.err.println("Unsupported encoding exception: " + uee.toString());
				return null;
			}

			// Now do the actual request
			MediaType form = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

			OkHttpClient client = new OkHttpClient.Builder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.writeTimeout(10, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.build();
			try {
				client.connectTimeoutMillis();
				RequestBody body = RequestBody.create(form, postData);
				Request request = new Request.Builder()
						.url("https://api.exmo.com/v1/" + method)
						.addHeader("Key", authEx.getApiKey())
						.addHeader("Sign", sign)
						.post(body)
						.build();

				Response response = client.newCall(request).execute();
				responseStr = response.body().string();
			} catch (IOException e) {
				new Log (LogUnit.TypeLog.ERROR, "Request fail: " + e.toString());
			}
		} catch (Log log) {
			new Log (log);
		}
		return responseStr;
	}
	public String getExchangeName() {
		return nameExchange;
	}
	public long getUserId () { return uId; }
	public long getServerDate () { return serverDate; }
	public HashMap<String, BigDecimal> getBalancesBook () { return balancesBook; }
	public HashMap<String, BigDecimal> getReservedBook () { return reservedBook; }
	public boolean orderCreate (Order order) {
		try {
			if (order.getQuantity().equals(new BigDecimal(0)))
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Ордер не верен");
			HashMap<String, String> dataOrder = new HashMap<>(), resultData = new HashMap<>();
			dataOrder.put("pair", order.getInstrument().getName());
			dataOrder.put("quantity", order.getQuantity().toString());
			dataOrder.put("price", order.getPrice().toString());
			dataOrder.put("type", order.getType().toString().toLowerCase());

			String responseJSON = new String();
			responseJSON = request("order_create", dataOrder);

			if (responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Ответ биржи пуст");
			try {
				JSONParser parser = new JSONParser();
				JSONObject infoJSON = (JSONObject) parser.parse(responseJSON);
				resultData.put("result", infoJSON.get("result").toString());
				resultData.put("error", infoJSON.get("error").toString());
				resultData.put("order_id", infoJSON.get("order_id").toString());
			} catch (Exception e) {
				new Log(e, nameExchange+" | Ошибка при получении ответа от сервера при создании нового ордера");
			}
			if (!resultData.isEmpty()) {
				if (resultData.get("result").equals("true") && !resultData.get("result").isEmpty()) {
					new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | Ордер №" + resultData.get("order_id") + " успешно создан");
					order.setOrderId(resultData.get("order_id"));
					return true;
				}
				if (resultData.get("result").equals("false") || !resultData.get("error").isEmpty())
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | " + resultData.get("error"));
			}
		} catch (Log log) {
			new Log(log);
		}
		return false;
	}
	public boolean orderCancel (Long orderId) {
		try {
			if (orderId == 0)
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | ID Ордера не верен");
			HashMap<String, String> dataOrder = new HashMap<>(), resultData = new HashMap<>();
			dataOrder.put("order_id", orderId.toString());

			String responseJSON = new String();
			responseJSON = request("order_cancel", dataOrder);

			if (responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Ответ биржи пуст");
			try {
				JSONParser parser = new JSONParser();
				JSONObject infoJSON = (JSONObject) parser.parse(responseJSON);
				resultData.put("result", infoJSON.get("result").toString());
				resultData.put("error", infoJSON.get("error").toString());
			} catch (Exception e) {
				new Log(e, nameExchange+" | Ошибка при получении ответа от сервера при отмене ордера " + "№"+orderId);
			}
			if (!resultData.isEmpty()) {
				if (resultData.get("result").equals("true")) {
					new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | Ордер №" + orderId + " успешно отменен");
					return true;
				}
				if (resultData.get("result").equals("false") || !resultData.get("error").isEmpty())
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | " + resultData.get("error"));
			}
		} catch (Log log) {
			new Log(log);
		}
		return false;
	}
	public ArrayList<Table> getGlobalTrade (Instrument instrument) {
		ArrayList<Table> tradeList = new ArrayList<>();
		try {
			String responseJSON = new String();
			HashMap<String, String> dataOrder = new HashMap<>();
			dataOrder.put("pair", instrument.getName());
			responseJSON = request("trades", dataOrder);

			if (responseJSON == null || responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | Нет информации о торговой паре: " + instrument.getName());
			try {
				JSONParser parser = new JSONParser();
				JSONObject dataJSON = (JSONObject) parser.parse(responseJSON);
				for (Object orderListTmp : (JSONArray) dataJSON.get(instrument.getName())) {
					JSONObject infoTmp = (JSONObject) orderListTmp;
					String trade_id = infoTmp.get("trade_id").toString();
					String date = infoTmp.get("date").toString();
					String type = infoTmp.get("type").toString();
					String quantity = infoTmp.get("quantity").toString();
					String price = infoTmp.get("price").toString();
					String amount = infoTmp.get("amount").toString();
					Trade tradeTmp = new Trade(currencyBookExmo.getPair(instrument.getName().toUpperCase()), Order.TypeOrder.valueOf(type.toUpperCase()), trade_id, quantity, price, amount, date);
					tradeList.add(tradeTmp);
				}
			} catch (Exception e) {
				new Log(e, "Ошибка сервра при получении списка сделок");
			}
		} catch (Log log) {
			new Log(log);
		}
		return tradeList;
	}
	public HashMap<String, ArrayList<Table>> getOpenOrderList () {
		HashMap<String, ArrayList<Table>> orderList = new HashMap<>();
		try {
			String responseJSON = new String();
			HashMap<String, String> dataOrder = new HashMap<>(), resultData = new HashMap<>();
			responseJSON = request("user_open_orders", dataOrder);

			if (responseJSON == null)
				throw new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | Список ордеров не получен. Ответ сервера пуст");
			if (responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | У вас нет активных ордеров");
			try {
				JSONParser parser = new JSONParser();
				JSONObject dataJSON = (JSONObject) parser.parse(responseJSON);
				String pairNameTmp = new String();
				for (Object pairName : dataJSON.keySet()) {
					Object pairOrder = dataJSON.get(pairName);
					ArrayList<Table> orderArrayList = new ArrayList<>();
					for (Object orderListTmp : (JSONArray) pairOrder) {
						JSONObject tmp = (JSONObject) orderListTmp;
						String type = tmp.get("type").toString();
						String pair = tmp.get("pair").toString();
						String order_id = tmp.get("order_id").toString();
						String created = tmp.get("created").toString();
						String price = tmp.get("price").toString();
						String quantity = tmp.get("quantity").toString();
						String amount = tmp.get("amount").toString();
						pairNameTmp = pair;

						Order orderTmp = new Order(Order.TypeCreateOrder.EXCHANGE_ORDER, currencyBookExmo.getPair(pair), Order.TypeOrder.valueOf(type.toUpperCase()), price, quantity, amount);
						orderTmp.setOrderId(order_id);
						orderTmp.setCreateDate(created);

						orderArrayList.add(orderTmp);
					}
					orderList.put(pairNameTmp, orderArrayList);
				}
			} catch (Exception e) {
				new Log(e, "Ошибка сервера при получении списка открытых ордеров");
			}
			if (!resultData.isEmpty()) {
				if (resultData.get("result").equals("true") && !resultData.get("result").isEmpty())
					return orderList;
				if (resultData.get("result").equals("false") || !resultData.get("error").isEmpty())
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | " + resultData.get("error"));
			}
		} catch (Log log) {
			new Log(log);
		}
		return orderList;
	}
	public HashMap<String, ArrayList<Table>> getCancelledOrderList (Integer limit) {
		if (limit < 10)
			limit = 10;
		if (limit > 10000)
			limit = 10000;
		HashMap<String, ArrayList<Table>> orderList = new HashMap<>();
		try {
			String responseJSON = new String();
			HashMap<String, String> dataOrder = new HashMap<>();
				dataOrder.put("limit", limit.toString());
			responseJSON = request("user_cancelled_orders", dataOrder);

			if (responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | У вас нет отмененных ордеров");
			try {
				JSONParser parser = new JSONParser();
				JSONArray dataJSON = (JSONArray) parser.parse(responseJSON);
				for (Object order : dataJSON) {
					JSONObject pairOrder = (JSONObject) order;
					ArrayList<Table> orderArrayList = new ArrayList<>();

					String date = pairOrder.get("date").toString();
					String order_id = pairOrder.get("order_id").toString();
					String type = pairOrder.get("order_type").toString();
					String pair = pairOrder.get("pair").toString();
					String price = pairOrder.get("price").toString();
					String quantity = pairOrder.get("quantity").toString();
					String amount = pairOrder.get("amount").toString();

					Order orderTmp = new Order(Order.TypeCreateOrder.EXCHANGE_ORDER, currencyBookExmo.getPair(pair), Order.TypeOrder.valueOf(type.toUpperCase()), price, quantity, amount);
					orderTmp.setOrderId(order_id);
					orderTmp.setCreateDate(date);

					ArrayList pairArrayTmp = orderList.get(pair);
					if (pairArrayTmp == null)
						pairArrayTmp = new ArrayList();
					pairArrayTmp.add(orderTmp);
					orderArrayList.addAll(pairArrayTmp);
					orderList.put(pair, orderArrayList);
				}
			} catch (Exception e) {
				new Log(e, "Ошибка сервера при получении списка отмененных ордеров");
			}
		} catch (Log log) {
			new Log(log);
		}
		return orderList;
	}
	public ArrayList<Table> getTradeList (Instrument instrument, Integer limit) {
		if (limit < 10)
			limit = 10;
		if (limit > 10000)
			limit = 10000;
		ArrayList<Table> tradeList = new ArrayList<>();
		try {
			String responseJSON = new String();
			HashMap<String, String> dataOrder = new HashMap<>();

			dataOrder.put("limit", limit.toString());
			dataOrder.put("pair", instrument.getName());
			responseJSON = request("user_trades", dataOrder);

			if (responseJSON == null || responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | У вас отсутствуют сделки");
			try {
				JSONParser parser = new JSONParser();
				JSONObject dataJSON = (JSONObject) parser.parse(responseJSON);
				Object pairOrder = dataJSON.get(instrument.getName());
				for (Object orderListTmp : (JSONArray) pairOrder) {
					JSONObject tmp = (JSONObject) orderListTmp;
					String type = tmp.get("type").toString();
					String pair = tmp.get("pair").toString();
					String trade_id  = tmp.get("trade_id").toString();
					String order_id = tmp.get("order_id").toString();
					String date = tmp.get("date").toString();
					String price = tmp.get("price").toString();
					String quantity = tmp.get("quantity").toString();
					String amount = tmp.get("amount").toString();

					Trade tradeTmp = new Trade(currencyBookExmo.getPair(pair.toUpperCase()), Order.TypeOrder.valueOf(type.toUpperCase()), trade_id, quantity, price, amount, date);
					tradeTmp.setOrderId(Long.parseLong(order_id));
					tradeList.add(tradeTmp);
				}
			} catch (Exception e) {
				new Log(e, "Ошибка сервра при получении списка сделок");
			}
		} catch (Log log) {
			new Log(log);
		}
		return tradeList;
	}
	public ArrayList<Table> getHistoryOrderTrades (Integer orderId) {
		ArrayList<Table> orderList = new ArrayList<>();
		try {
			String responseJSON = new String();
			HashMap<String, String> dataOrder = new HashMap<>();
			dataOrder.put("order_id", orderId.toString());
			responseJSON = request("order_trades", dataOrder);

			if (responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | Нет информации по этому ордеру №"+orderId);
			try {
				JSONParser parser = new JSONParser();
				JSONObject dataJSON = (JSONObject) parser.parse(responseJSON);
				for (Object orderListTmp : (JSONArray) dataJSON.get("trades")) {
					JSONObject infoTmp = (JSONObject) orderListTmp;
					String trade_id = infoTmp.get("trade_id").toString();
					String date = infoTmp.get("date").toString();
					String type = infoTmp.get("type").toString();
					String pair = infoTmp.get("pair").toString();
					String order_id = infoTmp.get("order_id").toString();
					String quantity = infoTmp.get("quantity").toString();
					String price = infoTmp.get("price").toString();
					String amount = infoTmp.get("amount").toString();
					Trade tradeTmp = new Trade(currencyBookExmo.getPair(pair.toUpperCase()), Order.TypeOrder.valueOf(type.toUpperCase()), trade_id, quantity, price, amount, date);
					tradeTmp.setOrderId(Integer.parseInt(order_id));
					orderList.add(tradeTmp);
				}
			} catch (Exception e) {
				new Log(e, "Ошибка сервра при получении списка сделок");
			}
		} catch (Log log) {
			new Log(log);
		}
		return orderList;
	}
	public BigDecimal getCommission (BigDecimal quantity, BigDecimal rate) {
		try {
			BigDecimal amount = quantity.multiply(rate);
			BigDecimal finalQuantity = quantity.multiply(commissionExchange);
			return finalQuantity;
		} catch (Exception e) {
			new Log(e, nameExchange+" | Ошибка при расчете количества активов с вычетом комиссии ("+quantity+")|("+rate+")");
		}
		return new BigDecimal("0");
	}
	public String getDepositAddress (String nameCurrency) {
		try {
			nameCurrency = nameCurrency.toUpperCase();
			if (nameCurrency.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Отсутствует название валюты");

			String responseJSON = new String();
			responseJSON = request("deposit_address", new HashMap<>());

			if (responseJSON.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Ответ биржи пуст");
			try {
				JSONParser parser = new JSONParser();
				HashMap infoJSON = (HashMap) parser.parse(responseJSON);
				if (infoJSON.containsKey(nameCurrency)) {
					String addrDepo = (String) infoJSON.get(nameCurrency);
					if (addrDepo.isEmpty())
						throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Для валюты " + nameCurrency + " отсутствует адрес пополнения, возможно его нужно сгенерировать на сайте биржи.");
					return addrDepo;
				} else {
					throw new Log (LogUnit.TypeLog.MESSAGE, nameExchange+" | Такой валюты (" + nameCurrency + ") нет в списке на пополнение");
				}
			} catch (Exception e) {
				new Log(e, "Ошибка при получении ответа от сервера при создании нового ордера");
			}
		} catch (Log log) {
			new Log(log);
		}
		return new String("null");
	}
	public String withdrawCrypt (String nameCurrency, BigDecimal amount, String address) {
		try {
			nameCurrency = nameCurrency.toUpperCase();
			if (nameCurrency.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Отсутствует название валюты");
			if (amount.equals(new BigDecimal(0)))
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Отсутствует сумма вывода");
			if (address.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Отсутствует блокчейн-адрес для вывода");

			String responseJSON = new String();
			HashMap<String, String> dataOrder = new HashMap<>();
			dataOrder.put("amount", amount.toString());
			dataOrder.put("currency", nameCurrency);
			dataOrder.put("address", address);
			responseJSON = request("withdraw_crypt", dataOrder);

			try {
				JSONParser parser = new JSONParser();
				JSONObject dataJSON = (JSONObject) parser.parse(responseJSON);
				String result = (String) dataJSON.get("result");
				String error = (String) dataJSON.get("error");
				String task_id = (String) dataJSON.get("task_id");

				if (dataJSON.isEmpty())
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Биржа не отвечает");
				if (task_id == null || result == null) {
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Ошибка при создании задачи на вывод средств: " + error);
				}
				if (result.equals("true")) {
					new Log(LogUnit.TypeLog.MESSAGE, nameExchange+" | Задача на вывод №" + task_id + " создана успешно");
					return task_id;
				}
			} catch (Exception e) {
				new Log(e, "Ошибка при выводе средств");
			}
		} catch (Log log) {
			new Log(log);
		}
		return new String("");
	}
	public HashMap<String, String> withdrawStatus (String taskId) {
		HashMap<String, String> resultData = new HashMap<>(), dataOrder = new HashMap<>();
		try {
			if (taskId == null)
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Отсутствует номер задачи на вывод");
			if (taskId.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Отсутствует номер задачи на вывод");

			String responseJSON = new String();
			dataOrder.put("task_id", taskId);
			responseJSON = request("withdraw_get_txid", dataOrder);

			try {
				JSONParser parser = new JSONParser();
				JSONObject dataJSON = (JSONObject) parser.parse(responseJSON);
				String result = (String) dataJSON.get("result");
				String error = (String) dataJSON.get("error");
				String status = (String) dataJSON.get("status");
				String txid = (String) dataJSON.get("txid");

				if (dataJSON.isEmpty())
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Биржа не отвечает");
				if (result == null || status == null || txid == null) {
					throw new Log(LogUnit.TypeLog.ERROR, nameExchange+" | Ошибка при получении ID транзакции вывода средств: " + error);
				}
				if (result.equals("true")) {
					resultData.put("result", result);
					resultData.put("error", error);
					resultData.put("status", status);
					resultData.put("txid", txid);
					return resultData;
				}
			} catch (Exception e) {
				new Log(e, nameExchange+" | Ошибка при получении ID транзакции вывода средств");
			}
		} catch (Log log) {
			new Log(log);
		}
		return resultData;
	}
	public TickerBook getTickerBook() {
		return tickerBookExmo;
	}
	public InstrumentBook getCurrencyBook() {
		return currencyBookExmo;
	}
	public OrderBook getOrderBook() {
		return orderBookExmo;
	}
}