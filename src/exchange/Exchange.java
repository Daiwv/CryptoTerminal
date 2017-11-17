package exchange;

import view.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Exchange {
	protected abstract String request(String method, HashMap<String, String> arguments);
	public abstract String getExchangeName ();
	public abstract long getUserId ();
	public abstract long getServerDate ();
	public abstract HashMap<String, BigDecimal> getBalancesBook ();
	public abstract HashMap<String, BigDecimal> getReservedBook ();
	public abstract boolean orderCreate (Order order);
	public abstract boolean orderCancel (Long orderId);
	public abstract ArrayList<Table> getGlobalTrade (Instrument pair);
	public abstract HashMap<String, ArrayList<Table>> getOpenOrderList ();
	public abstract HashMap<String, ArrayList<Table>> getCancelledOrderList (Integer limit);
	public abstract ArrayList<Table> getTradeList (Instrument instrument, Integer limit);
	public abstract ArrayList<Table> getHistoryOrderTrades (Integer orderId);
	public abstract BigDecimal getCommission (BigDecimal quantity, BigDecimal rate);
	public abstract String getDepositAddress (String nameCurrency);
	public abstract String withdrawCrypt (String nameCurrency, BigDecimal amount, String address);
	public abstract HashMap<String, String> withdrawStatus (String task_id);
	public abstract TickerBook getTickerBook();
	public abstract InstrumentBook getCurrencyBook();
	public abstract OrderBook getOrderBook();
}
