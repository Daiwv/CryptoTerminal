package model;

import controller.Window;
import exchange.Log;
import exchange.LogUnit;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class UpdateLogTable extends UpdateTable {

	public UpdateLogTable(Window.TypeWindow typeWindow, ObservableList observableList) {
		try {
			this.typeWindow = typeWindow;
			this.observableList = observableList;
			isActive = true;
		} catch (Exception e) {
			new Log(e, "Ошибка в конструкторе класса обновления моих ордеров");
		}
	}

	public ArrayList getUpdateMethod() {
		ArrayList arrayTmp = new ArrayList();
		for (LogUnit logUnit : Log.logList) {
			arrayTmp.add(logUnit);
		}
		return arrayTmp;
	}
}