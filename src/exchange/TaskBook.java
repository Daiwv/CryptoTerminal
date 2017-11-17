package exchange;

import java.util.ArrayList;

public class TaskBook implements Runnable {
	private static ArrayList<Task> taskList = new ArrayList<>();
	public static synchronized ArrayList<Task> getTaskList () { return taskList; }
	private boolean isActive = true;

	@Override
	public void run () {
		try {
			while (isActive) {
				for (Task task : getTaskList()) {
					if (task == null)
						throw new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Задача не сущетсвует");
					if (task.update()) {
						taskList.remove(task);
						break;
					}
				}
				Thread.sleep(1000);
			}
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log(e, "Ошибка в потоке обработки задач");
		}
	}

	public void stopThread () {
		isActive = false;
	}
	public boolean add (Task task) {
		if (task != null)
			if (getTaskList().add(task))
				return true;
		return false;
	}
	public boolean remove (Task task) throws Log, Exception {
		if (task == null)
			new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно удалить задачу, она не выбрана");
		if (getTaskList().contains(task)) {
			getTaskList().remove(task);
			return true;
		}
		new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Ошибка при удалении задачи");
		return false;
	}
	public boolean replaceTask (Task oldTask, Task newTask) throws Log, Exception {
		if (oldTask == null)
			new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно заменить задачу, отсутствует старая");
		if (newTask == null)
			new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Невозможно заменить задачу, отсутствует новая");
		if (getTaskList().contains(oldTask)) {
			getTaskList().remove(oldTask);
			getTaskList().add(newTask);
			return true;
		}
		new Log (LogUnit.TypeLog.ERROR, this.getClass().getName() + " | Ошибка при редактировании задачи");
		return false;
	}
}
