package controller;

import exchange.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class CreateTask {
	public Task task;
	public Stage stage;
	private Action newAction;
	private Order newOrder;

	private Exchange exchange;
	private Action.TypeAction typeAction;
	private Long orderId;
	private Order.TypeOrder typeOrder;
	private Task.TypeValue typeValue;
	private Task.TypePrice typePrice;
	private Task.TypeTrigger typeTrigger;
	private Double value;

	private Instrument instrument;
	private String priceOrder, quantityOrder, amountOrder;

	public void init (Stage stage) throws Exception, Log {
		this.stage = stage;

		exchangeInit();
		actionInit ();
		typePriceInit ();
		typeTriggerInit ();
		typeValueInit ();
		typeOrderInit ();
	}
	public void createTask(ActionEvent actionEvent) {
		priceOrder = priceOrderText.getText();
		quantityOrder = quantityOrderText.getText();
		amountOrder = amountOrderText.getText();
		value = Double.parseDouble(valueTextField.getText());

		newOrder = new Order(Order.TypeCreateOrder.USER_ORDER, instrument, typeOrder, priceOrder, quantityOrder, amountOrder);
		newAction = new Action(Action.TypeAction.CREATE_ORDER, newOrder);
		task = new Task(exchange, newAction, typePrice, typeTrigger, typeValue, value);

		if (Main.taskBook.add(task))
			stage.close();
	}

	private void exchangeInit () {
		Main.exchangeList.entrySet().forEach(entry -> exchangeChoice.getItems().add(entry.getKey()));
	}
	private void actionInit () {
		typeAction = Action.TypeAction.CREATE_ORDER;
		/*for (Action.TypeAction typeAction : Action.TypeAction.values()) {
			actionChoice.getItems().add(typeAction.toString());
		}*/
	}
	private void typePriceInit () {
		for (Task.TypePrice typePrice : Task.TypePrice.values()) {
			typePriceChoice.getItems().add(typePrice.toString());
		}
	}
	private void typeTriggerInit () {
		for (Task.TypeTrigger typeTrigger : Task.TypeTrigger.values()) {
			typeTriggerChoice.getItems().add(typeTrigger.toString());
		}
	}
	private void typeValueInit () {
		for (Task.TypeValue typeValue : Task.TypeValue.values()) {
			typeValueChoice.getItems().add(typeValue.toString());
		}
	}

	private void instrumentOrderInit () throws Exception, Log {
		if (exchange == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно инициализировать список инструментов - не выбрана биржа");
		for (String instrumentStr : exchange.getCurrencyBook().getNamePairList()) {
			instrumentOrderChoice.getItems().add(instrumentStr);
		}
	}
	private void typeOrderInit () throws Exception, Log {
		for (Order.TypeOrder typeOrder : Order.TypeOrder.values()) {
			typeOrderChoice.getItems().add(typeOrder);
		}
	}

	@FXML
	public ChoiceBox exchangeChoice, typePriceChoice, typeTriggerChoice, typeValueChoice;
	public ChoiceBox instrumentOrderChoice, typeOrderChoice;
	public TextField valueTextField, priceOrderText, quantityOrderText, amountOrderText;
	public Label instrumentOrderLabel, typeOrderLabel, priceOrderLabel, quantityOrderLabel, amountOrderLabel;

	@FXML
	public void exchangeChange(ActionEvent actionEvent) throws Exception, Log {
		String nameExchange = new String();
		nameExchange = exchangeChoice.getSelectionModel().getSelectedItem().toString();
		exchange = Main.exchangeList.get(nameExchange);
		if (nameExchange.isEmpty() || exchange == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно изменить биржу");
		instrumentOrderInit();
	}
	public void typePriceChange(ActionEvent actionEvent) throws Exception, Log {
		String typePriceName = new String();
		typePriceName = typePriceChoice.getSelectionModel().getSelectedItem().toString();
		typePrice = Task.TypePrice.valueOf(typePriceName);
		if (typePriceName.isEmpty() || typePrice == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно изменить тип цены");
	}
	public void typeTriggerChange(ActionEvent actionEvent) throws Exception, Log {
		String typeTriggerName = new String();
		typeTriggerName = typeTriggerChoice.getSelectionModel().getSelectedItem().toString();
		typeTrigger = Task.TypeTrigger.valueOf(typeTriggerName);
		if (typeTriggerName.isEmpty() || typeTriggerName == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно изменить событие");
	}
	public void typeValueChange(ActionEvent actionEvent) throws Exception, Log {
		String typeValueName = new String();
		typeValueName = typeValueChoice.getSelectionModel().getSelectedItem().toString();
		typeValue = Task.TypeValue.valueOf(typeValueName);
		if (typeValueName.isEmpty() || typeValue == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно изменить тип значения");
	}

	@FXML
	public void instrumentOrderChange(ActionEvent actionEvent) throws Exception, Log {
		String instrumentName = new String();
		instrumentName = instrumentOrderChoice.getSelectionModel().getSelectedItem().toString();
		instrument = exchange.getCurrencyBook().getPair(instrumentName);
		if (instrumentName.isEmpty() || instrument == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно изменить инструмент");
	}
	public void typeOrderChange(ActionEvent actionEvent) throws Exception, Log {
		String typeOrderName = new String();
		typeOrderName = typeOrderChoice.getSelectionModel().getSelectedItem().toString();
		typeOrder = Order.TypeOrder.valueOf(typeOrderName);
		if (typeOrderName.isEmpty() || typeOrder == null)
			throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно изменить тип ордера");
	}
}
