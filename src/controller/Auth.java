package controller;

import exchange.Exchange;
import exchange.Log;
import exchange.LogUnit;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.Map;

public class Auth {
	@FXML
	public AnchorPane includeAuthForm;
	public ChoiceBox selectExchange;

	@FXML
	private void initialize() {
		addExchangeListToChoiceBox();
	}

	private void addExchangeListToChoiceBox () {
		for (Map.Entry entry : Main.exchangeList.entrySet())
			selectExchange.getItems().add(entry.getKey());

		selectExchange.setOnAction(event -> includeFormAuth(selectExchange.getValue()));
	}

	private void includeFormAuth(Object value) {
		try {
			String nameExchange = value.toString();
			if (nameExchange == null || nameExchange.isEmpty())
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует название биржи");
			includeAuthForm.getChildren().clear();
			Parent parent = FXMLLoader.load(getClass().getResource("/view/"+nameExchange+"Auth.fxml"));
			includeAuthForm.getChildren().add(parent);
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log (e, "Ошибка во время выбора биржи для аутентификации");
		}
	}
}
