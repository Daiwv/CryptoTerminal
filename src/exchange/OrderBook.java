package exchange;

import view.Table;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface OrderBook {
	public ArrayList<Table> getBuyOrderList (String pairName);
	public ArrayList<Table> getSellOrderList (String pairName);
	public BigDecimal getBuyQuantity (String pairName);
	public BigDecimal getBuyAmount(String pairName);
	public BigDecimal getSellQuantity (String pairName);
	public BigDecimal getSellAmount(String pairName);
}
