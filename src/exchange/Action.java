package exchange;

public class Action {
	public enum TypeAction { CREATE_ORDER, CANCEL_ORDER }

	public TypeAction typeAction;
	public Order order;
	public Long orderId;

	public Action (TypeAction typeAction, Order order) {
		orderId = new Long (0);
		try {
			if (order == null)
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует ордер");
			if (typeAction.equals(TypeAction.CREATE_ORDER)) {
				this.typeAction = typeAction;
				this.order = order;
			} else
				throw new Log(LogUnit.TypeLog.ERROR, "Неверная задача на создание ордера ("+typeAction.toString()+")");
		} catch (Log log) {
			new Log(log);
		}
	}
	public Action (TypeAction typeAction, Long orderId) {
		try {
			if (orderId == null || orderId.equals(0))
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Отсутствует ID ордера");
			if (typeAction.equals(TypeAction.CANCEL_ORDER)) {
				this.typeAction = typeAction;
				this.orderId = orderId;
			} else
				throw new Log(LogUnit.TypeLog.ERROR, "Неверная задача на отмену ордера ("+typeAction.toString()+")");
		} catch (Log log) {
			new Log(log);
		}
	}

	public boolean performTask (Exchange exchange) {
		try {
			if (typeAction.equals(TypeAction.CREATE_ORDER)) {
				return exchange.orderCreate(order);
			}
			if (typeAction.equals(TypeAction.CANCEL_ORDER)) {
				return exchange.orderCancel(orderId);
			}
			else
				throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Неизвестный тип действия");
		} catch (Log log) {
			new Log(log);
		}
		return false;
	}
	public Order getOrder() {
		return order;
	}
}
