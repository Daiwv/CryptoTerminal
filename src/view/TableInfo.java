package view;

import model.UpdateOrdersTable;
import model.UpdateTable;
import controller.Window;
import exchange.Instrument;
import exchange.Exchange;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public abstract class TableInfo {
	protected Exchange exchange;
	protected Instrument instrument;
	protected Window.TypeWindow typeWindow;
	protected UpdateTable updateTable;
	protected Thread updateTableThread;
	protected TableView tableView;
	protected ObservableList dataTable;

	public abstract TableView createTable();
	public void setInstrument(Instrument instrument) {
		stopThread ();
		this.instrument = instrument;
		updateTable = new UpdateOrdersTable(typeWindow, exchange, dataTable, instrument);
		updateTableThread = new Thread(updateTable);
		updateTableThread.start();
	}
	public void setExchange (Exchange exchange) {
		stopThread ();
		this.exchange = exchange;
	}
	public void stopThread () {
		dataTable.clear();
		updateTable.disable();
	}
}
