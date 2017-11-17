package controller;

import controller.Window.TypeWindow;
import exchange.Instrument;
import exchange.Exchange;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class ControllerSubWindow {
	//Описывает содержимое окна: Графики, Таблицы и т.д., что содержит данные
	protected Exchange exchange;
	protected Instrument instrument;
	protected TypeWindow typeWindow;
	protected ObservableList dataTable = FXCollections.observableArrayList();

	public void init() {}
	public void setData (TypeWindow typeWindow, Exchange exchange, Instrument instrument) {
		this.typeWindow = typeWindow;
		this.exchange = exchange;
		this.instrument = instrument;
	}
	public Exchange getExchange() {
		return exchange;
	}
	public Instrument getInstrument() {
		return instrument;
	}
	public TypeWindow getTypeWindow () { return typeWindow; }
	public abstract void closeTable ();
	public abstract void setExchange (Exchange exchange);
	public abstract void setInstrument(Instrument instrument);
}
