package model;

import controller.Window;
import exchange.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;

import java.util.ArrayList;

public abstract class UpdateTable implements Runnable {
	protected Exchange exchange;
	protected ObservableList observableList;
	protected Instrument instrument;
	protected Window.TypeWindow typeWindow;
	protected boolean isActive;

	public abstract ArrayList getUpdateMethod ();
	public void disable () {
		isActive = false;
	}
	public void run () {
		ArrayList infoBook;
		try {
			while (isActive) {
				infoBook = getUpdateMethod();
				ArrayList dataListTmp = new ArrayList();
				if (infoBook == null)
					throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствуют данные для обновления");
				for (Object tableRow : infoBook) {
					if (tableRow == null)
						throw new Log(LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Поток обновления не получил данных");
					dataListTmp.add(tableRow);
				}
				observableList.clear();
				observableList.addAll(dataListTmp);
				Thread.sleep(1000);
			}
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Ошибка в потоке обновления данных");
		}
	}
}
