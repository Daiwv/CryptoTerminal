package exchange;

import java.math.BigDecimal;
import java.util.Date;

public class Ticker {
	private String currencyPair;
	private BigDecimal highPrice, lowPrice, avgPrice, amount, quantity, lastPrice, buyMaxPrice, sellMinPrice;
	private long date;

	public Ticker () {}
	public Ticker(String currencyPair, String highPrice, String lowPrice, String avgPrice, String amount, String quantity, String lastPrice, String buyMaxPrice, String sellMinPrice, String date) {
		this.currencyPair = currencyPair;
		this.highPrice = new BigDecimal(highPrice);
		this.lowPrice = new BigDecimal(lowPrice);
		this.avgPrice = new BigDecimal(avgPrice);
		this.amount = new BigDecimal(amount);
		this.quantity = new BigDecimal(quantity);
		this.lastPrice = new BigDecimal(lastPrice);
		this.buyMaxPrice = new BigDecimal(buyMaxPrice);
		this.sellMinPrice = new BigDecimal(sellMinPrice);
		this.date = Long.parseLong(date);
	}

	public String getName() { return currencyPair; }
	public BigDecimal highPrice24hTrade() { return highPrice; }
	public BigDecimal lowPrice24hTrade() { return lowPrice; }
	public BigDecimal avgPrice24hTrade() { return avgPrice; }
	public BigDecimal vol24hTrade() { return amount; }
	public BigDecimal volSum24hTrade() { return quantity; }
	public BigDecimal lastCurrPriceTrade() { return lastPrice; }
	public BigDecimal buyMaxPriceTrade() { return buyMaxPrice; }
	public BigDecimal sellMinPriceTrade() { return sellMinPrice; }
	public long date() { return date; }
	public String dateFormat() {
		String dateTime = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(date*1000));
		return new String(dateTime); }
}