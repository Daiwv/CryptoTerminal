package exchange;

import javafx.beans.property.SimpleStringProperty;

public class LogUnit {
	private TypeLog typeLog;
	private String message;
	public enum TypeLog {ERROR, MESSAGE};

	public LogUnit (TypeLog typeLog, String message) {
		this.typeLog = typeLog;
		this.message = message;
	}

	public TypeLog getTypeLog() {
		return typeLog;
	}
	public String getMessage() {
		return message;
	}
	public SimpleStringProperty getTypeSSP() {
		return new SimpleStringProperty (typeLog.toString());
	}
	public SimpleStringProperty getMessSSP() {
		return new SimpleStringProperty (message);
	}
}
