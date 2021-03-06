package pkg.order;

import pkg.exception.StockMarketExpection;
import pkg.trader.Trader;

public class SellOrder extends Order {
	
	public SellOrder(String stockSymbolIn, int sizeIn, double priceIn, Trader traderIn) {
		stockSymbol = stockSymbolIn;
		size = sizeIn;
		price = priceIn;
		trader = traderIn;
	}

	public SellOrder(String stockSymbolIn, int sizeIn, boolean isMarketOrder,
			Trader traderIn) throws StockMarketExpection 
	{
		if(isMarketOrder)
			isMarketOrder = true;
		else
			throw new StockMarketExpection("An order has been placed without a valid price.");
		stockSymbol = stockSymbolIn;
		size = sizeIn;
		price = 0.0;
		trader = traderIn;
	}
	
	public int compareTo(Order that)
	{
		if(price > that.getPrice())
		{
			return 1;
		}
		else if(price < that.getPrice())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	

	public void printOrder() {
		System.out.println("Stock: " + stockSymbol + " $" + price + " x "
				+ size + " (Sell)");
	}
}
