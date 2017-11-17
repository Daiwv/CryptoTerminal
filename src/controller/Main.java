package controller;

import exchange.*;
import exchange.exmo.ExchangeExmo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends Application {
    public static HashMap<String, Exchange> exchangeList = new HashMap<>();
    private Thread taskBookThread;
    public static TaskBook taskBook = new TaskBook();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Exchange exchange = new ExchangeExmo();
        exchangeList.put(exchange.getExchangeName(), exchange);

        taskBookThread = new Thread(taskBook);
        taskBookThread.start();

        Pane root = FXMLLoader.load(getClass().getResource("/view/Window.fxml"));
        primaryStage.setScene(new Scene(root, 450, 320));
        primaryStage.setTitle("Выберете биржу и инструмент");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
