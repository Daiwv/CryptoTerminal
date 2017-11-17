package exchange;

import controller.Main;
import javafx.beans.property.SimpleStringProperty;

public class Task {
	public enum TypeValue { VALUE, PERCENT };
	public enum TypePrice { PRICE_LESS, PRICE_MORE };
	public enum TypeTrigger { SET_VALUE, HISTORICAL_MAXIMUM, LAST_24_HOURS_MAXIMUM, LAST_24_HOURS_MINIMUM };
static long indi = 0;
	public Exchange exchange;
	public Action action;
	public TypeValue typeValue;
	public TypePrice typePrice;
	public TypeTrigger typeTrigger;
	public Double value;

	public Task(Exchange exchange, Action action, TypePrice typePrice, TypeTrigger typeTrigger, TypeValue typeValue, Double value) {
		this.value = new Double(0);
		try {
			switch (typeTrigger) {
				case SET_VALUE:
					if (typeValue.equals(TypeValue.PERCENT))
						throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Конкретное значение не может быть процентным");
			}
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log(e, this.getClass().getName() + " | Неизвестная ошибка во время создания события");
		}
		this.exchange = exchange;
		this.action = action;
		this.typePrice = typePrice;
		this.typeTrigger = typeTrigger;
		this.typeValue = typeValue;
		this.value = value;
	}

	public boolean update () {
		try {
			String namePair = action.getOrder().getInstrument().getName();
			Double currentRate = getCurrentRate(namePair);
			Double valueTmp = value, percent = 0d;
			switch (typeTrigger) {
				case SET_VALUE:
					if (typeValue.equals(TypeValue.PERCENT))
						throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Конкретное значение не может быть процентным");
					break;
				case HISTORICAL_MAXIMUM:
					value = getHistoricalMaximum(namePair);
					break;
				case LAST_24_HOURS_MAXIMUM:
					valueTmp = get24hMax(namePair);
					break;
				case LAST_24_HOURS_MINIMUM:
					valueTmp = get24hMin(namePair);
					break;
				default:
					throw new Log(LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Несуществующий триггер события");
			}
			switch (typeValue) {
				case VALUE:
					break;
				case PERCENT:
					percent = (valueTmp * value) / 100;
					break;
			}
			System.out.println("\nIndicator: " + indi++ + " | " + currentRate + " / " + valueTmp);
			switch (typePrice) {
				case PRICE_LESS:
					if (currentRate < valueTmp - percent) {
						new Log(LogUnit.TypeLog.MESSAGE, "Событие задачи достигнуто: ");
						return action.performTask(exchange);
					}
					break;
				case PRICE_MORE:
					if (currentRate > valueTmp + percent) {
						new Log(LogUnit.TypeLog.MESSAGE, "Событие задачи достигнуто: ");
						return action.performTask(exchange);
					}
					break;
				default:
					throw new Log(LogUnit.TypeLog.ERROR, "Несуществующий триггер изменения цены");
			}
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log (e, "Ошибка во время обработки события: ");
		}
		return false;
	}

	public boolean removeTask () throws Log, Exception {
		return Main.taskBook.remove(this);
	}
	public boolean replaceTask (Task newTask) throws Log, Exception {
		return Main.taskBook.replaceTask(this, newTask);
	}

	public Double getHistoricalMaximum (String namePair) {
		return exchange.getTickerBook().getTicker(namePair).buyMaxPriceTrade().doubleValue();
	}
	public Double getCurrentRate (String namePair)  {
		return exchange.getTickerBook().getTicker(namePair).lastCurrPriceTrade().doubleValue();
	}
	public Double get24hMax (String namePair) {
		return exchange.getTickerBook().getTicker(namePair).highPrice24hTrade().doubleValue();
	}
	public Double get24hMin (String namePair) {
		return exchange.getTickerBook().getTicker(namePair).lowPrice24hTrade().doubleValue();
	}

	public SimpleStringProperty getExchangeSSP () { return new SimpleStringProperty(exchange.getExchangeName()); }
	public SimpleStringProperty getActionSSP () { return new SimpleStringProperty(action.typeAction.toString()); }
	public SimpleStringProperty getIdOrderSSP () { return new SimpleStringProperty(action.orderId.toString()); }
	public SimpleStringProperty getTypeValueSSP () { return new SimpleStringProperty(typeValue.toString()); }
	public SimpleStringProperty getTypePriceSSP () { return new SimpleStringProperty(typePrice.toString()); }
	public SimpleStringProperty getTypeTriggerSSP () { return new SimpleStringProperty(typeTrigger.toString()); }
	public SimpleStringProperty getValueSSP () { return new SimpleStringProperty(value.toString()); }

	public SimpleStringProperty getInstrumentSSP() {
		return action.order.getInstrumentSSP();
	}
	public SimpleStringProperty getDateSSP() {
		return action.order.getDateSSP();
	}
	public SimpleStringProperty getTypeSSP() {
		return action.order.getTypeSSP();
	}
	public SimpleStringProperty getPriceSSP() {
		return action.order.getPriceSSP();
	}
	public SimpleStringProperty getQuantitySSP() {
		return action.order.getQuantitySSP();
	}
	public SimpleStringProperty getAmountSSP() {
		return action.order.getAmountSSP();
	}
}
