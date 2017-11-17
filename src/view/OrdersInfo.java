package view;

import model.UpdateOrdersTable;
import controller.Window;
import exchange.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.UpdateTable;

public class OrdersInfo extends TableInfo {
	private TableColumn<Order, String> typeColumn, priceColumn, quantityColumn, amountColumn;

	public OrdersInfo(Window.TypeWindow typeWindow, ObservableList dataTable, Exchange exchange, Instrument instrument) {
		this.typeWindow = typeWindow;
		this.dataTable = dataTable;
		this.exchange = exchange;
		this.instrument = instrument;
	}

	public TableView<Order> createTable() {
		try {
			tableView = new TableView<Order>();
			typeColumn = new TableColumn<Order, String>("Тип сделки");
			priceColumn = new TableColumn<Order, String>("Цена");
			quantityColumn = new TableColumn<Order, String>("Количество");
			amountColumn = new TableColumn<Order, String>("Сумма");
		} catch (Exception e) {
			new Log(e, "Ошибка при создании таблицы");
		}

		try {
			if (exchange == null)
				throw new Log (LogUnit.TypeLog.ERROR, "Биржа не выбрана");
			if (dataTable == null)
				throw new Log (LogUnit.TypeLog.ERROR, "Данные отсутствуют");
			if (instrument == null)
				throw new Log (LogUnit.TypeLog.ERROR, "Инструмент не выбран");

			updateTable = new UpdateOrdersTable(typeWindow, exchange, dataTable, instrument);
			updateTableThread = new Thread(updateTable);
			updateTableThread.start();
		}
		catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, "Ошибка при запуске потока обновления данных");
		}

		try {
			typeColumn.setCellValueFactory(cell -> cell.getValue().getTypeSSP());
			priceColumn.setCellValueFactory(cell -> cell.getValue().getPriceSSP());
			quantityColumn.setCellValueFactory(cell -> cell.getValue().getQuantitySSP());
			amountColumn.setCellValueFactory(cell -> cell.getValue().getAmountSSP());
			tableView.setItems(dataTable);

			tableView.getColumns().addAll(typeColumn, priceColumn, quantityColumn, amountColumn);
		} catch (Exception e) {
			new Log(e, "Ошибка при связывании столбцов таблицы с обновляемыми данными");
		}
		return tableView;
	}
}
