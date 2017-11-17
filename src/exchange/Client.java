package exchange;

import exchange.Log;
import exchange.LogUnit;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {

    public enum Protocol { HTTP, HTTPS }
    private Protocol protocol;
    private String url;

    public Client (Protocol protocol, String url) {
    	url = symbolToCode(url);
        this.protocol = protocol;
        this.url = url;
    }

    public String getData() {
        return getBodyDocument(connectToSite());
    }

    private URLConnection connectToSite () {
        URL urlNew;
        try {
            switch (protocol) {
                case HTTPS:
                    url = "https://" + url;
                    urlNew = new URL(url);

                    HttpsURLConnection connectHttps = (HttpsURLConnection)urlNew.openConnection();

                    if (connectHttps.getResponseCode() == 200)
                        return connectHttps;
                    break;
                case HTTP:
                    url = "http://" + url;
                    urlNew = new URL(url);

                    HttpURLConnection connectHttp = (HttpURLConnection)urlNew.openConnection();

                    if (connectHttp.getResponseCode() == 200)
                        return connectHttp;
                    break;
            }
        } catch (IOException e) {
            new Log(e, this.getClass().getName() + " | Ошибка сетевого подключения");
        }
        return null;
    }

    private static String symbolToCode (String str) {
	    Character[] symbols = {' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
	    String[] code = {"%20","%D0%B0","%D0%B1","%D0%B2","%D0%B3","%D0%B4","%D0%B5","%D1%91","%D0%B6","%D0%B7","%D0%B8","%D0%B9","%D0%BA","%D0%BB","%D0%BC","%D0%BD","%D0%BE","%D0%BF","%D1%80","%D1%81","%D1%82","%D1%83","%D1%84","%D1%85","%D1%86","%D1%87","%D1%88","%D1%89","%D1%8A","%D1%8B","%D1%8C","%D1%8D","%D1%8E","%D1%8F","%D0%90","%D0%91","%D0%92","%D0%93","%D0%94","%D0%95","%D0%81","%D0%96","%D0%97","%D0%98","%D0%99","%D0%9A","%D0%9B","%D0%9C","%D0%9D","%D0%9E","%D0%9F","%D0%A0","%D0%A1","%D0%A2","%D0%A3","%D0%A4","%D0%A5","%D0%A6","%D0%A7","%D0%A8","%D0%A9","%D0%AA","%D0%AB","%D0%AC","%D0%AD","%D0%AE","%D0%AF"};
	    List<Character> symList = Arrays.asList(symbols);
	    List<String> codeList = Arrays.asList(code);
	    String newStr = new String();
	    for (int i = 0; i < str.length(); i++) {
	    	int pos = 0;
	    	if ((pos = symList.indexOf(str.charAt(i))) != -1) {
			    newStr += code[pos];
		    } else
			    newStr += str.charAt(i);
	    }
	    return newStr;
    }

    private String searchCharset (String charsetRegex, String whereToLook, int posGroup) {
        Matcher conMatch = Pattern.compile(charsetRegex).matcher(whereToLook);
        if (conMatch.find()) {
            String charset = conMatch.group(posGroup);
            if (charset.length() > 3)
                return charset;
        }
        return null;
    }

    private String getCharset (String contentType, ByteArrayOutputStream byteArray) {
        String charset = null;

        //Search for encoding in headers
        charset = searchCharset ("(?i)(charset=)(.{3,16})", contentType.replaceAll("(\")", ""), 2);

        if (charset == null) {
            //Search for encoding in the body of the document
            byte[] lineBuf = new byte[1024];
            byte[] byteArrayNew = byteArray.toByteArray();
            if (byteArrayNew.length > lineBuf.length) {
                System.arraycopy(byteArrayNew, 0, lineBuf, 0, lineBuf.length);
                charset = searchCharset ("(?i)(encoding=\")(.{3,16})(\")", new String(lineBuf), 2);
            }
        }

        return charset;
    }

    private String getBodyDocument(URLConnection con){
        if(con != null){
            try {
                byte[] byteLineBuf = new byte[16384];
                int countByteInLine = 0;
                InputStream stream = con.getInputStream();
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

                while ((countByteInLine = stream.read(byteLineBuf)) > 0) {
                    byteArray.write(byteLineBuf, 0, countByteInLine);
                }

                return new String(byteArray.toByteArray());
            } catch (IOException e) {
                new Log(e, "Ошибка чтения тела документа, полученного из сети");
            }
        }
        return null;
    }
}