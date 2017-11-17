package model;

import controller.Main;
import controller.Window;
import exchange.Log;
import exchange.LogUnit;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class UpdateTaskTable extends UpdateTable {

	public UpdateTaskTable(ObservableList observableList) {
		try {
			if (observableList == null)
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Данные для обновления не переданы");
			this.typeWindow = Window.TypeWindow.taskList;
			this.observableList = observableList;
			isActive = true;
		} catch (Log log) {
			new Log(log);
		}
		catch (Exception e) {
			new Log(e, "Ошибка в конструкторе класса обновления списка задач");
		}
	}

	@Override
	public ArrayList getUpdateMethod () {
		ArrayList arrayList = Main.taskBook.getTaskList();
		if (arrayList == null)
			arrayList = new ArrayList();
		return arrayList;
	}
}
