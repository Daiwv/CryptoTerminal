package model;

import controller.Window;
import exchange.Instrument;
import exchange.Exchange;
import exchange.Log;
import exchange.LogUnit;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class UpdateOrdersTable extends UpdateTable {

	public UpdateOrdersTable (Window.TypeWindow typeWindow, Exchange exchange, ObservableList observableList, Instrument instrument) {
		try {
			if (exchange == null)
				throw new Log(LogUnit.TypeLog.ERROR, "Отсутствует название биржи" + "("+ typeWindow.toString()+")");
			if (instrument.getName().isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, "Отсутствует название валютной пары" + "("+ typeWindow.toString()+")");
			this.typeWindow = typeWindow;
			this.instrument = instrument;
			this.exchange = exchange;
			this.observableList = observableList;
			isActive = true;
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, "Ошибка в конструкторе класса обновления моих ордеров");
		}
	}

	public ArrayList getUpdateMethod () {
		ArrayList<view.Table> arrayTmp = new ArrayList<>();
		switch (typeWindow) {
			case orderBuy:
				arrayTmp = exchange.getOrderBook().getBuyOrderList(instrument.getName());
				break;
			case orderSell:
				arrayTmp = exchange.getOrderBook().getSellOrderList(instrument.getName());
				break;
			case myOrder:
				arrayTmp = exchange.getOpenOrderList().get(instrument.getName());
				break;
		}
		return arrayTmp;
	}
}
