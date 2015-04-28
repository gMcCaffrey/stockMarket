package pkg.trader;

import java.util.ArrayList;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.order.Order;
import pkg.order.BuyOrder;
import pkg.order.SellOrder;
import pkg.order.OrderType;

public class Trader {
	// Name of the trader
	String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Cash left in the trader's hand
	double cashInHand;
	// Stocks owned by the trader
	ArrayList<Order> position;
	// Orders placed by the trader
	ArrayList<Order> ordersPlaced;

	public double getCashInHand() {
		return cashInHand;
	}

	public void setCashInHand(double cashInHand) {
		this.cashInHand = cashInHand;
	}

	public ArrayList<Order> getPosition() {
		return position;
	}

	public void setPosition(ArrayList<Order> position) {
		this.position = position;
	}

	public ArrayList<Order> getOrdersPlaced() {
		return ordersPlaced;
	}

	public void setOrdersPlaced(ArrayList<Order> ordersPlaced) {
		this.ordersPlaced = ordersPlaced;
	}

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.position = new ArrayList<Order>();
		this.ordersPlaced = new ArrayList<Order>();
	}

	// Buy stock straight from the bank
	// Need not place the stock in the order list
	// Add it straight to the user's position
	// If the stock's price is larger than the cash possessed, then an
	// exception is thrown
	// Adjust cash possessed since the trader spent money to purchase a
	// stock.
	public void buyFromBank(Market m, String symbol, int volume)
			throws StockMarketExpection {
		if((m.getStockForSymbol(symbol).getPrice() * volume) > cashInHand)
		{
			throw new StockMarketExpection("Cannot place order for stock: " + symbol + " since there is not enough money. Trader: \n" + this.name);
		}
		else
		{
			Order buy = new BuyOrder(symbol, volume, m.getStockForSymbol(symbol).getPrice(), this);
			this.position.add(buy);
			cashInHand -= m.getStockForSymbol(symbol).getPrice() * volume;
		}
	}

	// Place a new order and add to the orderlist
	// Also enter the order into the orderbook of the market.
	// Note that no trade has been made yet. The order is in suspension
	// until a trade is triggered.
	//
	// If the stock's price is larger than the cash possessed, then an
	// exception is thrown
	// A trader cannot place two orders for the same stock, throw an
	// exception if there are multiple orders for the same stock.
	// Also a person cannot place a sell order for a stock that he does not
	// own. Or he cannot sell more stocks than he possesses. Throw an
	// exception in these cases.

	public void placeNewOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		if (orderType == OrderType.BUY)
		{
			if(m.getStockForSymbol(symbol).getPrice() > cashInHand)
			{
				throw new StockMarketExpection("Cannot place order for stock: " + symbol + " since there is not enough money. Trader: \n" + this.name);
			}
			Order order = new BuyOrder(symbol, volume, m.getStockForSymbol(symbol).getPrice(), this);
			for (Order o : ordersPlaced)
			{
				if (o.getStockSymbol() == symbol)
					throw new StockMarketExpection("Duplicate orders");
			}

			m.addOrder(order);	
			ordersPlaced.add(order);

		}

		if (orderType == OrderType.SELL)
		{
			Order order = new SellOrder(symbol, volume, price, this);
			if (position.contains(order) == false)
			{
				throw new StockMarketExpection("Can't sell stocks not owned");
			}

			Order temp;

			for (Order o: position)
			{
				if (o.getStockSymbol().compareTo(symbol) == 0)
				{
					temp = o;
					if (volume > temp.getSize())
					{
						throw new StockMarketExpection("Can't sell more than own");
					}
					else
					{
						m.addOrder(order);
						ordersPlaced.add(order);
					}
				}
			}
		}	
	}

	public void placeNewMarketOrder(Market m, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		if (orderType == OrderType.BUY)
		{
			if(m.getStockForSymbol(symbol).getPrice() > cashInHand)
			{
				throw new StockMarketExpection("Cannot place order for stock: " + symbol + " since there is not enough money. Trader: \n" + this.name);
			}
			Order order = new BuyOrder(symbol, volume, true, this);
			for (Order o : ordersPlaced)
			{
				if (o.getStockSymbol() == symbol)
					throw new StockMarketExpection("Duplicate orders");
			}

			m.addOrder(order);	
			ordersPlaced.add(order);
		}

		if (orderType == OrderType.SELL)
		{
			Order order = new SellOrder(symbol, volume, true, this);
			if (position.contains(order) == false)
			{
				throw new StockMarketExpection("Can't sell stocks not owned");
			}

			Order temp;

			for (Order o: position)
			{
				if (o.getStockSymbol().compareTo(symbol) == 0)
				{
					temp = o;
					if (volume > temp.getSize())
					{
						throw new StockMarketExpection("Can't sell more than own");
					}
					else
					{
						m.addOrder(order);
						ordersPlaced.add(order);
					}
				}
			}
		}	
	}

	// Notification received that a trade has been made, the parameters are
	// the order corresponding to the trade, and the match price calculated
	// in the order book. Note than a trader can sell some of the stocks he
	// bought, etc. Or add more stocks of a kind to his possession. Handle
	// these situations.

	// Update the trader's orderPlaced, position, and cashInHand members
	// based on the notification.
	public void tradePerformed(Order o, double matchPrice)
			throws StockMarketExpection 
			{
		
		if(ordersPlaced.indexOf(o) >= 0)
		{
			Order completedOrder = ordersPlaced.get(ordersPlaced.indexOf(o));

			if(completedOrder instanceof BuyOrder)
			{
				boolean hasOrder = false;
				for(Order current : position)
				{
					if(current.getStockSymbol().compareTo(o.getStockSymbol()) == 0)
					{
						current.setSize(current.getSize() + o.getSize());	
						hasOrder = true;
					}
				}
				if(!hasOrder)
				{
					position.add(o);
				}
				cashInHand -= matchPrice * o.getSize();
				for(Order orderToRemove : ordersPlaced)//assumed that if we were able to fulfill a portion of our buy order we are satisfied and 
					//take down our listing.
				{
					if(orderToRemove.getStockSymbol().compareTo(o.getStockSymbol()) == 0)
					{
						ordersPlaced.remove(orderToRemove);
						break;
					}
				}

			}
			if(completedOrder instanceof SellOrder)
			{
				for(Order current : position)
				{
					if(current.getStockSymbol().compareTo(o.getStockSymbol()) == 0)
					{
						current.setSize(current.getSize() - o.getSize());	
						cashInHand += o.getSize() * matchPrice; 
						if(current.getSize() == 0)
						{
							position.remove(current);
						}
					}
					for(Order orderToRemove : ordersPlaced)
					{
						if(orderToRemove.getStockSymbol().compareTo(o.getStockSymbol()) == 0)
						{
							ordersPlaced.remove(orderToRemove);
							break;
						}
					}
				}
			}
		}

			}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");
		for (Order o : position) {
			o.printStockNameInOrder();
		}
		System.out.println("Stocks Desired: ");
		for (Order o : ordersPlaced) {
			o.printOrder();
		}
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
	}
}
