package exchange;

import view.Table;

import java.math.BigDecimal;

public class Trade extends Table {
	private Long tradeId, orderId;

	public Trade(Instrument pair, Order.TypeOrder typeOrder, String tradeId, String quantity, String price, String amount, String date) {
		this.instrument = pair;
		this.typeOrder = typeOrder;
		this.tradeId = Long.parseLong(tradeId);
		this.date = Long.parseLong(date);
		this.quantity = new BigDecimal(quantity.replace(",","."));
		this.price = new BigDecimal(price.replace(",","."));
		this.amount = new BigDecimal(amount.replace(",","."));
	}
	public void setOrderId (long orderId) { this.orderId = orderId; }
	public Long getOrderId () { return this.orderId; }
	public Long getTradeId() {
		return tradeId;
	}
}
