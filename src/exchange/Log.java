package exchange;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Stack;

public class Log extends Throwable {
	private final static String urlErrorLog = "";
	private PrintWriter errorTrace;
	public static ArrayDeque<LogUnit> logList = new ArrayDeque<>();

	public Log (LogUnit.TypeLog typeLog, String message) {
		LogUnit logUnit = new LogUnit(typeLog, message);
		logList.addFirst(logUnit);
	}
	public Log (Log log) {
		//Сделать принудительный вывод ошибок?
		/*while (!logList.empty()) {
			LogUnit logUnitTmp = logList.pop();
			System.out.println(logUnitTmp.getTypeLog() + " - " + logUnitTmp.getMessage());
		}*/
	}
	public Log (Exception e, String message) {
		try {
			//Прикрутить: Отправка в лог на сервер
			//e.printStackTrace(errorTrace);
			System.out.println(message);
			e.printStackTrace();
			//Прикрутить: вывод сообщения об ошибке в ГУИ

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}