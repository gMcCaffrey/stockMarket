package pkg.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;

public class OrderBook {
	Market m;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market m) {
		this.m = m;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
	}

	public HashMap<String, ArrayList<Order>> getBuyOrders() {
		return buyOrders;
	}

	public void setBuyOrders(HashMap<String, ArrayList<Order>> buyOrders) {
		this.buyOrders = buyOrders;
	}

	public HashMap<String, ArrayList<Order>> getSellOrders() {
		return sellOrders;
	}

	public void setSellOrders(HashMap<String, ArrayList<Order>> sellOrders) {
		this.sellOrders = sellOrders;
	}

	// Populate the buyOrders and sellOrders data structures, whichever
	// appropriate
	public void addToOrderBook(Order order) throws StockMarketExpection {
		if(order instanceof BuyOrder)
		{
			ArrayList<Order> temp = buyOrders.get(order.getStockSymbol());
			if(temp != null)
			{
				temp.add(order);
			}
			else
			{
				temp = new ArrayList<Order>();
				temp.add(order);
			}
			buyOrders.put(order.getStockSymbol(), temp);
		}
		else if(order instanceof SellOrder)
		{
			ArrayList<Order> temp = sellOrders.get(order.getStockSymbol());
			if(temp != null)
			{
				temp.add(order);
			}
			else
			{
				temp = new ArrayList<Order>();
				temp.add(order);
			}
			sellOrders.put(order.getStockSymbol(), temp);
		}
		else
		{
			throw new StockMarketExpection("In OrderBook, addToOrderBook: Parameter is not an Order");
		}
	}

	// Complete the trading.
	// 1. Follow and create the orderbook data representation (see spec)
	// 2. Find the matching price
	// 3. Update the stocks price in the market using the PriceSetter.
	// Note that PriceSetter follows the Observer pattern. Use the pattern.
	// 4. Remove the traded orders from the orderbook
	// 5. Delegate to trader that the trade has been made, so that the
	// trader's orders can be placed to his possession (a trader's position
	// is the stocks he owns)
	// (Add other methods as necessary)
	public void trade() 
	{
		ArrayList<Order> fulfilledSells = new ArrayList<Order>();
		ArrayList<Order> fulfilledBuys = new ArrayList<Order>();

		for(String key : buyOrders.keySet())
		{
			ArrayList<Order> pendingBuyOrders = buyOrders.get(key);
			for(Order o : pendingBuyOrders)
			{
				Order match = null;
				try{
					match = findMatchingSellOrder(o);
				}
				catch(StockMarketExpection e)
				{
					e.printStackTrace();
				}
				if(match == null)
				{
					continue;
				}
				else
				{
					if(o.getSize() > match.getSize())//buy is greater than sell
					{
						updateOrderSize(o, match, false);
						fulfilledSells.add(match);
						//removeSellOrder(match);
					}
					else if(o.getSize() < match.getSize())//sell is greater than buy
					{
						updateOrderSize(match, o, true);
						fulfilledBuys.add(o);
						;
					}
					else//buy and sell are equal
					{
						fulfilledBuys.add(o);
						fulfilledSells.add(match);
					}
					try{
						o.getTrader().tradePerformed(o, match.getPrice());
						match.getTrader().tradePerformed(match, match.getPrice());
					}
					catch (StockMarketExpection e)
					{
						e.printStackTrace();
					}
				}
			}

		}
		for (Order deleteMeFromBuyOrders : fulfilledBuys)
		{
			ArrayList<Order> allTheStarbucks = buyOrders.get(deleteMeFromBuyOrders.getStockSymbol());
			for(int i = 0; i < allTheStarbucks.size(); i++)
			{
				if(allTheStarbucks.get(i).getTrader().getName().compareTo(deleteMeFromBuyOrders.getTrader().getName()) == 0)
				{
					allTheStarbucks.remove(i);
				}
			}
			buyOrders.put(deleteMeFromBuyOrders.getStockSymbol(), allTheStarbucks);

		}

		for (Order deleteMeFromSellOrders : fulfilledSells)
		{
			ArrayList<Order> allTheStarbucks = sellOrders.get(deleteMeFromSellOrders.getStockSymbol());
			for(int i = 0; i < allTheStarbucks.size(); i++)
			{
				if(allTheStarbucks.get(i).getTrader().getName().compareTo(deleteMeFromSellOrders.getTrader().getName()) == 0)
				{
					allTheStarbucks.remove(i);
				}
			}
			buyOrders.put(deleteMeFromSellOrders.getStockSymbol(), allTheStarbucks);

		}
	}

	private void updateOrderSize(Order toBeUpdated, Order toBeDeleted, boolean isSellOrder)
	{
		toBeUpdated.setSize(toBeUpdated.getSize() - toBeDeleted.getSize());
		ArrayList<Order> orderList;
		if(isSellOrder)
			orderList = sellOrders.get(toBeUpdated.getStockSymbol());
		else
			orderList = buyOrders.get(toBeUpdated.getStockSymbol());
		for(Order mOrder : orderList)
		{
			if(mOrder.getPrice() == toBeUpdated.getPrice())
			{
				mOrder.setSize(toBeUpdated.getSize() - toBeDeleted.getSize());
				if(isSellOrder)
				{
					sellOrders.put(toBeUpdated.getStockSymbol(), orderList);
				}
				else
				{
					buyOrders.put(toBeUpdated.getStockSymbol(), orderList);
				}
			}
		}
	}

	public Order findMatchingSellOrder(Order o) throws StockMarketExpection//public for testing
	{
		if(o instanceof BuyOrder)
		{
			ArrayList<Order> pendingSellOrders = sellOrders.get(o.getStockSymbol());
			Collections.sort(pendingSellOrders);
			if(pendingSellOrders.get(0).getPrice() <= o.getPrice())
			{
				return pendingSellOrders.get(0);
			}
			return null;
		}
		else
		{
			throw new StockMarketExpection("Parameter must be a Buy Order");
		}
	}

}
