package view;

import javafx.fxml.FXML;
import model.UpdateLogTable;
import controller.Window;
import exchange.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.UpdateTable;

public class LogInfo extends TableInfo {
	@FXML
	protected TableColumn<LogUnit, String> typeColumn, messColumn;

	public LogInfo(ObservableList observableList) {
		this.dataTable = observableList;
	}

	public TableView<LogUnit> createTable() {
		try {
			tableView = new TableView<LogUnit>();
			typeColumn = new TableColumn<LogUnit, String>("Тип");
			messColumn = new TableColumn<LogUnit, String>("Сообщение");
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при создании таблицы");
		}

		try {
			updateTable = new UpdateLogTable(Window.TypeWindow.log, dataTable);
			updateTableThread = new Thread(updateTable);
			updateTableThread.start();
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при запуске потока обновления данных");
		}

		try {
			typeColumn.setCellValueFactory(cell -> cell.getValue().getTypeSSP());
			messColumn.setCellValueFactory(cell -> cell.getValue().getMessSSP());
			tableView.setItems(dataTable);

			tableView.getColumns().addAll(typeColumn, messColumn);
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при связывании столбцов таблицы с обновляемыми данными");
		}
		return tableView;
	}
}