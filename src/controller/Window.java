package controller;

import exchange.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Map;

public class Window {
	private Stage newStage;
	private Scene newScene;
	private Parent newWindow;
	private int width, height;

	private static Stage authStage = new Stage();
	private static Pane authPane;
	private static boolean authWinIsOpen = false;
	private static boolean createTaskWinB = false;

	public enum TypeWindow { globalTrade, orderSell, orderBuy, myTrade, myOrder, log, taskList }
	private TypeWindow typeWindow;
	private Exchange exchange;
	private Instrument instrument;

	@FXML
	private void initialize() {
		try {
			width = 450;
			height = 320;
			menuItemExchangeListInit();
			menuItemWindowChangeInit();
		} catch (Exception e) {
			new Log(e, "Ошибка при инициализации контроллера окна инструмента");
		}
	}
	@FXML
	private Menu menuItemExchangeList, menuItemToolList;
	private void menuItemExchangeListInit () {
		try {
			if (Main.exchangeList == null)
				throw new Log(LogUnit.TypeLog.ERROR, "Список бирж пуст" + "("+ typeWindow.toString()+")");
			for (Map.Entry<String, Exchange> entry : Main.exchangeList.entrySet()) {
				MenuItem miTmp = new MenuItem(entry.getKey());
				miTmp.setOnAction(event -> menuItemExchangeListEvent(entry.getValue()));
				menuItemExchangeList.getItems().add(miTmp);
			}
		} catch (Log log) {
			new Log(log);
		}
	}

	private void menuItemExchangeListEvent (Exchange exchange) {
		this.exchange = exchange;
		menuItemToolList ();
		setNameWindow();
		if (controllerSubWindow != null)
			controllerSubWindow.setExchange(exchange);
	}

	private void menuItemToolList () {
		try {
			if (exchange != null) {
				for (String namePair : exchange.getCurrencyBook().getListPair().keySet()) {
					MenuItem miTmp = new MenuItem(namePair.replace("_", "/"));
					miTmp.setOnAction(event -> menuItemToolListEvent(exchange.getCurrencyBook().getListPair().get(namePair)));
					menuItemToolList.getItems().add(miTmp);
				}
			}
		} catch (Exception e) {
			new Log(e, "Ошибка при инициализации списка инструментов в меню");
		}
	}

	private void menuItemToolListEvent (Instrument instrument) {
		this.instrument = instrument;
		if (controllerSubWindow != null)
			controllerSubWindow.setInstrument(instrument);
		setNameWindow();
	}

	@FXML
	private Menu menuItemWindowChange;
	private void menuItemWindowChangeInit() {
		MenuItem globalTradeItem = new MenuItem("История торгов");
		globalTradeItem.setOnAction(event -> setTypeWindow(TypeWindow.globalTrade));

		MenuItem orderSellItem = new MenuItem("Ордера на продажу");
		orderSellItem.setOnAction(event -> setTypeWindow(TypeWindow.orderSell));

		MenuItem orderBuyItem = new MenuItem("Ордера на покупку");
		orderBuyItem.setOnAction(event -> setTypeWindow(TypeWindow.orderBuy));

		MenuItem myTradeItem = new MenuItem("Мои сделки");
		myTradeItem.setOnAction(event -> setTypeWindow(TypeWindow.myTrade));

		MenuItem myOrderItem = new MenuItem("Мои ордера");
		myOrderItem.setOnAction(event -> setTypeWindow(TypeWindow.myOrder));

		SeparatorMenuItem separator = new SeparatorMenuItem();

		MenuItem logItem = new MenuItem("Лог");
		logItem.setOnAction(event -> setTypeWindow(TypeWindow.log));

        menuItemWindowChange.getItems().addAll(globalTradeItem, orderSellItem, orderBuyItem, myTradeItem, myOrderItem, separator, logItem);
	}

	@FXML
	public GridPane typeWindowInclude;

	ControllerSubWindow controllerSubWindow;
	public void setTypeWindow(TypeWindow typeWindow) {
		try {
			if (typeWindow == null)
				throw new Log (LogUnit.TypeLog.ERROR, "Отсутствует тип таблицы" + "("+ typeWindow.toString()+")");

			this.typeWindow = typeWindow;

			if (controllerSubWindow != null)
				controllerSubWindow.closeTable();
			typeWindowInclude.getChildren().clear();

			FXMLLoader fxmlLoader = new FXMLLoader();
			Parent parent = fxmlLoader.load(getClass().getResource("/view/Table.fxml").openStream());
			controllerSubWindow = fxmlLoader.getController();
			controllerSubWindow.setData(typeWindow, exchange, instrument);
			controllerSubWindow.init();

			typeWindowInclude.getChildren().add(parent);
			parent.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controllerSubWindow.closeTable();
				}
			});
		} catch (Log log) {
			new Log(log);
		} catch (Exception e) {
			new Log(e, "Ошибка при изменении типа окна");
		}
	}

	@FXML
	public void taskListWin(ActionEvent actionEvent) {
		setTypeWindow(TypeWindow.taskList);
		typeWindowInclude.getScene().getWindow().setWidth(900);
	}
	public void taskCreateWin(ActionEvent actionEvent) {
		try {
			if (!createTaskWinB) {
				FXMLLoader fxmlLoader = new FXMLLoader();
				Parent createTaskParent = fxmlLoader.load(getClass().getResource("/view/CreateTask.fxml").openStream());
				Stage createTaskStage = new Stage();
				createTaskStage.setScene(new Scene(createTaskParent, 500, 260));
				createTaskStage.setTitle("Создать задачу");
				createTaskStage.show();

				CreateTask createTask = fxmlLoader.getController();
				createTask.init(createTaskStage);
				createTaskParent.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
					public void handle(WindowEvent we) {
						createTaskWinB = false;
					}
				});
				createTaskWinB = true;
			}
		} catch (Exception e) {
			new Log(e, "Ошибка при оздании окна создания задач");
		} catch (Log log) {
			new Log(log);
		}
	}
	public void openAuthWin(ActionEvent actionEvent) {
		try {
			if (authWinIsOpen)
				throw new Log(LogUnit.TypeLog.MESSAGE, "Окно авторизации уже открто");
			authPane = FXMLLoader.load(getClass().getResource("/view/Auth.fxml"));
			authStage.setScene(new Scene(authPane, 250, 200));
			authStage.setTitle("Авторизация");
			authStage.show();
			authPane.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					authWinIsOpen = false;
				}
			});
			authWinIsOpen = true;
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log(e, "Ошибка при открытии окна авторизации");
		}
	}

	@FXML
	public GridPane generalGrid;
	private void setNameWindow () {
		try {
			String nameExchange = new String(), namePair = new String();
			if (exchange != null)
				nameExchange = exchange.getExchangeName();
			if (instrument != null)
				namePair = instrument.getName();
			Stage thisStage = (Stage) generalGrid.getScene().getWindow();
			thisStage.setTitle(nameExchange + " - " + namePair);
		} catch (Exception e) {
			new Log (e, "Ошибка при изменении имени окна");
		}
	}
	public void createNewWindow() {
		try {
			newStage = new Stage();
			newWindow = FXMLLoader.load(getClass().getResource("/view/Window.fxml"));
			newScene = new Scene(newWindow, width, height);
			newStage.setScene(newScene);
			newStage.setTitle("Выберете биржу и инструмент");
			newStage.show();
		} catch (Exception e) {
			new Log(e, "Ошибка при инициализации контроллера окна инструмента");
		}
	}
	public void closeAllWindow() {
		Main.taskBook.stopThread();
		System.exit(0);
	}
}
