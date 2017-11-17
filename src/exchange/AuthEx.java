package exchange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class AuthEx implements Serializable {
	private String apiKey, secretKey;
	public transient static final String path = "authex";
	public transient static final String formatFile = ".authex";
	private static final long serialVersionUID = 1L;

	public AuthEx () {
		this.apiKey = new String();
		this.secretKey = new String();
	}
	public AuthEx (String apiKey, String secretKey) {
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}

	public String getApiKey() {
		return apiKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void auth (String nameExchange, String apiKey, String secretKey) {
		try {
			if (apiKey.isEmpty() || secretKey.isEmpty())
				throw new Log(LogUnit.TypeLog.ERROR, nameExchange + " | Отсутствует один из параметров авторизации");
			AuthEx authEx = new AuthEx(apiKey, secretKey);

			File createDir = new File (AuthEx.path);
			createDir.mkdir();
			File file = new File (AuthEx.path + "/" + nameExchange + AuthEx.formatFile);
			file.createNewFile();

			if (file.exists()) {
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(authEx);
			}
		} catch (Log log) {
			new Log (log);
		} catch (Exception e) {
			new Log (e, "Ошибка во время создания файла авторизации");
		}
	}
	public boolean isEmpty () {
		if (apiKey.isEmpty() || secretKey.isEmpty())
			return true;
		return false;
	}
}
