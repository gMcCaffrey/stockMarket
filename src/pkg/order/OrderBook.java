package pkg.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;

//Maintains the data for buy and sell orders for a market
public class OrderBook {
	Market market;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market m) {
		this.market = m;
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
			ArrayList<Order> buyOrdersCopy = buyOrders.get(order.getStockSymbol());
			if(buyOrdersCopy != null)
			{
				buyOrdersCopy.add(order);
			}
			else
			{
				buyOrdersCopy = new ArrayList<Order>();
				buyOrdersCopy.add(order);
			}
			buyOrders.put(order.getStockSymbol(), buyOrdersCopy);
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

	public void trade() 
	{
		ArrayList<Order> fulfilledSells = new ArrayList<Order>();
		ArrayList<Order> fulfilledBuys = new ArrayList<Order>();

		for(String key : buyOrders.keySet())
		{
			ArrayList<Order> pendingBuyOrders = buyOrders.get(key);
			for(Order findMatchOrder : pendingBuyOrders)
			{
				Order match = null;
				try{
					match = findMatchingSellOrder(findMatchOrder);
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
					if(findMatchOrder.getSize() > match.getSize())//buy is greater than sell
					{
						updateOrderSize(findMatchOrder, match, false);
						fulfilledSells.add(match);
						//removeSellOrder(match);
					}
					else if(findMatchOrder.getSize() < match.getSize())//sell is greater than buy
					{
						updateOrderSize(match, findMatchOrder, true);
						fulfilledBuys.add(findMatchOrder);
						;
					}
					else//buy and sell are equal
					{
						fulfilledBuys.add(findMatchOrder);
						fulfilledSells.add(match);
					}
					try{
						findMatchOrder.getTrader().tradePerformed(findMatchOrder, match.getPrice());
						match.getTrader().tradePerformed(match, match.getPrice());
					}
					catch (StockMarketExpection e)
					{
						e.printStackTrace();
					}
				}
			}

		}
		
		deleteOrders(fulfilledBuys, fulfilledSells);
	}
	
	public void deleteOrders(ArrayList<Order> fulfilledBuys, ArrayList<Order> fulfilledSells){
		
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

	public Order findMatchingSellOrder(Order o) throws StockMarketExpection
	{
		if(o instanceof BuyOrder)
		{
			ArrayList<Order> pendingSellOrders = sellOrders.get(o.getStockSymbol());
			Collections.sort(pendingSellOrders);
			Order pendingSale = pendingSellOrders.get(0);
			if(pendingSale.getPrice() <= o.getPrice())
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
