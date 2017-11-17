package controller;

import exchange.Exchange;
import exchange.Instrument;
import exchange.Log;
import exchange.LogUnit;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import view.*;

public class Table extends ControllerSubWindow {
	//Определяет тип таблицы и инициализирует ее в tableInfo
	protected TableInfo tableInfo;

	@FXML
	public GridPane generalGridPane;

	@Override
	public void init() {
		try {
			if (exchange == null)
				exchange = Main.exchangeList.entrySet().iterator().next().getValue();
			if (instrument == null && exchange != null) {
				String namePairTmp = exchange.getCurrencyBook().getNamePairList().get(0);
				instrument = exchange.getCurrencyBook().getListPair().get(namePairTmp);
			}
			if (exchange == null || instrument == null)
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Не выбрана биржа или инструмент" + "("+ typeWindow.toString()+")");
			switch (typeWindow) {
				case globalTrade:
					tableInfo = new TradesInfo(typeWindow, dataTable, exchange, instrument);
					break;
				case myTrade:
					tableInfo = new TradesInfo(typeWindow, dataTable, exchange, instrument);
					break;
				case myOrder:
					tableInfo = new OrdersInfo(typeWindow, dataTable, exchange, instrument);
					break;
				case orderBuy:
					tableInfo = new OrdersInfo(typeWindow, dataTable, exchange, instrument);
					break;
				case orderSell:
					tableInfo = new OrdersInfo(typeWindow, dataTable, exchange, instrument);
					break;
				case log:
					tableInfo = new LogInfo (dataTable);
					break;
				case taskList:
					tableInfo = new TaskInfo (dataTable, exchange, instrument);
					generalGridPane.setMinWidth(900);
					generalGridPane.setPrefWidth(900);
					break;
			}
			TableView table = tableInfo.createTable();
			if (table == null)
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Ошибка инициализации таблицы");
			generalGridPane.getChildren().add(table);
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при инициализации контроллера окна инструмента");
		}
	}

	public void closeTable () {
		if (tableInfo != null)
			tableInfo.stopThread();
	}
	public void setExchange (Exchange exchange) { this.exchange = exchange; tableInfo.setExchange(exchange); }
	public void setInstrument(Instrument instrument) { this.instrument = instrument; tableInfo.setInstrument(instrument); }
}
