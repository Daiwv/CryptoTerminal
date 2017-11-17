package model;

import controller.Window;
import exchange.Instrument;
import exchange.Exchange;
import exchange.Log;
import exchange.LogUnit;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class UpdateTradesTable extends UpdateTable {

	public UpdateTradesTable (Window.TypeWindow typeWindow, Exchange exchange, ObservableList observableList, Instrument instrument) {
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
			case globalTrade:
				arrayTmp = exchange.getGlobalTrade(instrument);
				break;
			case myTrade:
				arrayTmp = exchange.getTradeList(instrument, 1000);
				break;
		}
		return arrayTmp;
	}
}

