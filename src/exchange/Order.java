package exchange;

import view.Table;

import java.math.BigDecimal;

public class Order extends Table {
	public enum TypeOrder {BUY, SELL, MARKET_BUY, MARKET_SELL, MARKET_BUY_TOTAL, MARKET_SELL_TOTAL}
	public enum TypeCreateOrder { USER_ORDER, EXCHANGE_ORDER }
	private Long orderId, createDate;

	public Order (TypeCreateOrder typeOrder, Instrument pair, TypeOrder type, String price, String quantity, String amount) {
		orderId = new Long (0);
		createDate = new Long(0);
		try {
			//Сделать проверку на правильность введенных знчений (пров. на число)
			this.instrument = pair;
			this.typeOrder = type;
			this.price = new BigDecimal(price.replace(",", "."));
			this.quantity = new BigDecimal(quantity.replace(",", "."));
			this.amount = new BigDecimal(amount.replace(",", "."));

			if (typeOrder.equals(TypeCreateOrder.USER_ORDER)) {
				if (this.price.equals(0) || this.quantity.equals(0) || this.amount.equals(0))
					throw new Log(LogUnit.TypeLog.ERROR, "Одно из значений ордера равняется нулю");
				if (this.price.compareTo(instrument.getMaxPrice()) == 1)
					throw new Log(LogUnit.TypeLog.ERROR, "Цена ордера больше максимальной для этой валютной пары");
				if (this.price.compareTo(instrument.getMinPrice()) == -1)
					throw new Log(LogUnit.TypeLog.ERROR, "Цена ордера меньше минимальной для этой валютной пары");
				if (this.quantity.compareTo(instrument.getMaxQuantity()) == 1)
					throw new Log(LogUnit.TypeLog.ERROR, "Кол-во больше максимального для этой валютной пары");
				if (this.quantity.compareTo(instrument.getMinQuantity()) == -1)
					throw new Log(LogUnit.TypeLog.ERROR, "Кол-во меньше минимального для этой валютной пары");
			}
		} catch (Log log) {
			new Log(log);
		}
	}
	public void setOrderId (String orderId) { this.orderId = Long.parseLong(orderId); }
	public void setCreateDate (String createDate) { this.createDate = Long.parseLong(createDate); }
	public Long getOrderId () {
		if (orderId == null)
			return orderId = new Long(0);
		return orderId;
	}
	public Long getCreateDate () {
		if (createDate == null)
			return createDate = new Long(0);
		return createDate;
	}
	public boolean isNullObject () {
		if ((price.intValue() == 0 && quantity.intValue() == 0) || (price.intValue() == 0 && amount.intValue() == 0))
			return true;
		return false;
	}
}
