package view;

import model.UpdateTable;
import model.UpdateTradesTable;
import controller.Window;
import exchange.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TradesInfo extends TableInfo {
	protected TableColumn<Trade, String> dateColumn, typeColumn, priceColumn, quantityColumn, amountColumn;

	public TradesInfo(Window.TypeWindow typeWindow, ObservableList dataTable, Exchange exchange, Instrument instrument) {
		this.typeWindow = typeWindow;
		this.dataTable = dataTable;
		this.exchange = exchange;
		this.instrument = instrument;
	}

	public TableView<Trade> createTable() {
		try {
			tableView = new TableView<Trade>();
			dateColumn = new TableColumn<Trade, String>("Дата");
			typeColumn = new TableColumn<Trade, String>("Тип сделки");
			priceColumn = new TableColumn<Trade, String>("Цена");
			quantityColumn = new TableColumn<Trade, String>("Количество");
			amountColumn = new TableColumn<Trade, String>("Сумма");
		} catch (Exception e) {
			new Log(e, "Ошибка при создании таблицы");
		}

		try {
			if (exchange == null)
				throw new Log(LogUnit.TypeLog.ERROR, "Биржа не выбрана");
			if (dataTable == null)
				throw new Log(LogUnit.TypeLog.ERROR, "Данные отсутствуют");
			if (instrument == null)
				throw new Log(LogUnit.TypeLog.ERROR, "Инструмент не выбран");

			updateTable = new UpdateTradesTable(typeWindow, exchange, dataTable, instrument);
			updateTableThread = new Thread(updateTable);
			updateTableThread.start();
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, "Ошибка при запуске потока обновления данных");
		}

		try {
			dateColumn.setCellValueFactory(cell -> cell.getValue().getDateSSP());
			typeColumn.setCellValueFactory(cell -> cell.getValue().getTypeSSP());
			priceColumn.setCellValueFactory(cell -> cell.getValue().getPriceSSP());
			quantityColumn.setCellValueFactory(cell -> cell.getValue().getQuantitySSP());
			amountColumn.setCellValueFactory(cell -> cell.getValue().getAmountSSP());
			tableView.setItems(dataTable);

			tableView.getColumns().addAll(dateColumn, typeColumn, priceColumn, quantityColumn, amountColumn);
		} catch (Exception e) {
			new Log(e, "Ошибка при связывании столбцов таблицы с обновляемыми данными");
		}
		return tableView;
	}
	public void setInstrument(Instrument instrument) {
		stopThread();
		this.instrument = instrument;
		updateTable = new UpdateTradesTable(typeWindow, exchange, dataTable, instrument);
		updateTableThread = new Thread(updateTable);
		updateTableThread.start();
	}
	public void setExchange (Exchange exchange) {
		stopThread ();
		this.exchange = exchange;
	}
}