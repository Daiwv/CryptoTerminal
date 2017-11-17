package view;

import controller.DeleteTask;
import exchange.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.UpdateTaskTable;

public class TaskInfo extends TableInfo {
	@FXML
	public TableColumn<Task, String> exchangeTableCol, actionTableCol, priceTableCol, triggerTableCol, typeValueTableCol, valueTableCol, typeOrderTableCol, instrumentOrderTableCol, priceOrderTableCol, quantityOrderTableCol, amountOrderTableCol;

	public TaskInfo(ObservableList observableList, Exchange exchange, Instrument instrument) {
		this.dataTable = observableList;
		this.exchange = exchange;
		this.instrument = instrument;
	}

	public TableView<LogUnit> createTable() {
		try {
			tableView = new TableView<Task>();

			exchangeTableCol = new TableColumn<Task, String>("Биржа");
			actionTableCol = new TableColumn<Task, String>("Действие");
			priceTableCol = new TableColumn<Task, String>("Цена");
			triggerTableCol = new TableColumn<Task, String>("Тригер");
			valueTableCol = new TableColumn<Task, String>("Значение");
			typeValueTableCol = new TableColumn<Task, String>("Тип значения");

			typeOrderTableCol = new TableColumn<Task, String>("Тип");
			instrumentOrderTableCol = new TableColumn<Task, String>("Инструмент");
			priceOrderTableCol = new TableColumn<Task, String>("Цена");
			quantityOrderTableCol = new TableColumn<Task, String>("Количество");
			amountOrderTableCol = new TableColumn<Task, String>("Сумма");
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при создании таблицы");
		}

		try {
			updateTable = new UpdateTaskTable(dataTable);
			updateTableThread = new Thread(updateTable);
			updateTableThread.start();
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при запуске потока обновления данных");
		}

		try {
			exchangeTableCol.setCellValueFactory(cell -> cell.getValue().getExchangeSSP());
			actionTableCol.setCellValueFactory(cell -> cell.getValue().getActionSSP());
			priceTableCol.setCellValueFactory(cell -> cell.getValue().getTypePriceSSP());
			triggerTableCol.setCellValueFactory(cell -> cell.getValue().getTypeTriggerSSP());
			valueTableCol.setCellValueFactory(cell -> cell.getValue().getValueSSP());
			typeValueTableCol.setCellValueFactory(cell -> cell.getValue().getTypeValueSSP());

			typeOrderTableCol.setCellValueFactory(cell -> cell.getValue().getTypeSSP());
			instrumentOrderTableCol.setCellValueFactory(cell -> cell.getValue().getInstrumentSSP());
			priceOrderTableCol.setCellValueFactory(cell -> cell.getValue().getPriceSSP());
			quantityOrderTableCol.setCellValueFactory(cell -> cell.getValue().getQuantitySSP());
			amountOrderTableCol.setCellValueFactory(cell -> cell.getValue().getAmountSSP());
			tableView.setItems(dataTable);
			tableView.setEditable(true);

			tableView.getColumns().addAll(exchangeTableCol, instrumentOrderTableCol, actionTableCol, priceTableCol, triggerTableCol, valueTableCol, typeValueTableCol, typeOrderTableCol, priceOrderTableCol, quantityOrderTableCol, amountOrderTableCol);

			tableView.getSelectionModel().selectedItemProperty().addListener(
					(observable, oldValue, newValue) -> onSelectRow(newValue)
			);
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка при связывании столбцов таблицы с обновляемыми данными");
		}
		return tableView;
	}

	private void onSelectRow(Object newValue) {
		try {
			Task task = (Task) newValue;
			if (task == null)
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Задача не выбрана");
			deleteTaskWindow(task);
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log (e, this.getClass().getName() + " | Ошибка при выборе задачи");
		}
	}
	private void deleteTaskWindow (Task task) throws Exception, Log {
		FXMLLoader fxmlLoader = new FXMLLoader();
		Stage stageDeleteTask = new Stage();

		Parent parentDeleteTask = fxmlLoader.load(getClass().getResource("/view/DeleteTask.fxml").openStream());
		stageDeleteTask.setScene(new Scene(parentDeleteTask, 250, 120));
		stageDeleteTask.setTitle("Удаление задачи");
		DeleteTask deleteTask = fxmlLoader.getController();
		deleteTask.init(task, stageDeleteTask);
		stageDeleteTask.show();
	}
}
