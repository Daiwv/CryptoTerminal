package exchange;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface TradeBook {
	public ArrayList<Trade> getAllTradeList (String pairName) throws Throwable;
	public ArrayList<Trade> getBuyTradeList (String pairName) throws Throwable;
	public ArrayList<Trade> getSellTradeList (String pairName) throws Throwable;
	public BigDecimal getBuyQuantity (String pairName) throws Throwable;
	public BigDecimal getBuyAmount(String pairName) throws Throwable;
	//public BigDecimal getBuyTop(String pairName) throws Throwable;
	public BigDecimal getSellQuantity (String pairName) throws Throwable;
	public BigDecimal getSellAmount(String pairName) throws Throwable;
	//public BigDecimal getSellTop(String pairName) throws Throwable;
}
