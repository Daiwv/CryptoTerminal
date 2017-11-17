package controller;

import exchange.AuthEx;
import exchange.Exchange;
import exchange.Log;
import exchange.LogUnit;
import exchange.exmo.ExchangeExmo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import view.AuthForm;

public class ExmoAuth extends AuthForm {
	@FXML
	public TextField keyExmo, secExmo;

	public void entranceMethod(ActionEvent actionEvent) {
		String key = keyExmo.getText();
		String sec = secExmo.getText();

		try {
			if (!Main.exchangeList.containsKey("Exmo"))
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Такой биржи нет в списке");
			if (key == null || sec == null || key.isEmpty() || sec.isEmpty())
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Данные для авторизации не введены");
			AuthEx authEx = new AuthEx();
			authEx.auth("Exmo", key, sec);

			//Reauthorization
			Exchange exchangeExmo = Main.exchangeList.get("Exmo");
			exchangeExmo = new ExchangeExmo();
			if (exchangeExmo.getUserId() != 0) {
				new Log(LogUnit.TypeLog.MESSAGE, "EXMO | Авторизация прошла успешно");
				//Close Window
				Stage stage = (Stage) keyExmo.getScene().getWindow();
				stage.close();
			} else
				new Log(LogUnit.TypeLog.ERROR, "EXMO | Авторизация провалена");
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log(e, "Ошибка во время создания файла авторизации");
		}
	}
}
