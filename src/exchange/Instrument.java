package exchange;

import java.math.BigDecimal;

public class Instrument {
	private String namePair;
	private BigDecimal minQuantity, maxQuantity, minPrice, maxPrice, maxAmount, minAmount;

	public Instrument(String namePair, String minQuantity, String maxQuantity, String minPrice, String maxPrice, String maxAmount, String minAmount)
	{
		this.namePair = namePair;
		this.minQuantity = new BigDecimal(minQuantity);
		this.maxQuantity = new BigDecimal(maxQuantity);
		this.minPrice = new BigDecimal(minPrice);
		this.maxPrice = new BigDecimal(maxPrice);
		this.maxAmount = new BigDecimal(maxAmount);
		this.minAmount = new BigDecimal(minAmount);
		}
	public String getName() { return namePair; }
	public BigDecimal getMinQuantity () { return minQuantity; }
	public BigDecimal getMaxQuantity () { return maxQuantity; }
	public BigDecimal getMinPrice () { return minPrice; }
	public BigDecimal getMaxPrice () { return maxPrice; }
	public BigDecimal getMaxAmount () { return maxAmount; }
	public BigDecimal getMinAmount () { return minAmount; }
}
