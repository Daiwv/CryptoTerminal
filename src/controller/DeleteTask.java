package controller;

import exchange.Log;
import exchange.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DeleteTask {
	Task task;
	Stage stage;

	public void init (Task task, Stage stage) {
		this.task = task;
		this.stage = stage;

		taskText.setText(
				task.exchange.getExchangeName()
				+ " | " + task.action.order.getInstrument().getName()
				+ " | " + task.typePrice.toString()
				+ " | " + task.typeTrigger.toString()
				+ " | " + task.value.toString()
		);
	}

	@FXML
	public Label taskText;

	@FXML
	public void deleteTask(ActionEvent actionEvent) throws Exception, Log {
		task.removeTask();
		stage.close();
	}
	public void cancelDeleteTask(ActionEvent actionEvent) {
		task = null;
		stage.close();
	}
}
