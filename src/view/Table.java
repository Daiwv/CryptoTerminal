package view;

import exchange.Instrument;
import exchange.Order;
import javafx.beans.property.SimpleStringProperty;
import java.math.BigDecimal;
import java.util.Date;

public class Table {
	protected Order.TypeOrder typeOrder;
	protected BigDecimal price, quantity, amount;
	protected Long date;
	protected Instrument instrument;

	public Long getDate() {
		return date;
	}
	public String getDateFormat () {
		String dateTime = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(date*1000));
		return dateTime;
	}
	public Instrument getInstrument() {
		return instrument;
	}
	public BigDecimal getPrice () { return price; }
	public BigDecimal getQuantity () { return quantity; }
	public BigDecimal getAmount () { return amount; }
	public Order.TypeOrder getType () { return typeOrder; }
	public SimpleStringProperty getInstrumentSSP() {
		return new SimpleStringProperty (instrument.getName());
	}
	public SimpleStringProperty getDateSSP() {
		return new SimpleStringProperty (getDateFormat());
	}
	public SimpleStringProperty getTypeSSP() {
		return new SimpleStringProperty (typeOrder.toString().toUpperCase());
	}
	public SimpleStringProperty getPriceSSP() {
		return new SimpleStringProperty (price.toString());
	}
	public SimpleStringProperty getQuantitySSP() {
		return new SimpleStringProperty (quantity.toString());
	}
	public SimpleStringProperty getAmountSSP() {
		return new SimpleStringProperty (amount.toString());
	}
}
