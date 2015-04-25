package pkg.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.IPO;
import pkg.order.BuyOrder;
import pkg.order.OrderBook;
import pkg.order.OrderType;
import pkg.order.SellOrder;
import pkg.trader.Trader;

public class tests {

	@Test
	public void testPlaceNewOrderNormal() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader1.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
			SellOrder correctOrder = new SellOrder("SBUX", 100, 97.0, trader1);
			Assert.assertEquals(trader1.getOrdersPlaced().get(0), correctOrder);
		} 
		catch (StockMarketExpection e) {
			fail("Should not raise an exception");
		}
	}

	@Test
	public void testPlaceNewOrderInadequateFunds() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 1.00);
		try{
			trader1.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.BUY);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewOrderDuplicateOrder() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader1.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
			trader1.placeNewOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewOrderUnownedStock() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader1.placeNewOrder(m, "MSFT", 100, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewOrderInsufficientStocks() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 100);
			trader1.placeNewOrder(m, "SBUX", 102, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewMarketOrderNormal() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader1.placeNewMarketOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
			SellOrder correctOrder = new SellOrder("SBUX", 100, 97.0, trader1);
			Assert.assertEquals(trader1.getOrdersPlaced().get(0), correctOrder);
		} 
		catch (StockMarketExpection e) {
			fail("Should not raise an exception");
		}
	}

	@Test
	public void testPlaceNewMarketOrderInadequateFunds() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 1.00);
		try{
			trader1.placeNewMarketOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewMarketOrderDuplicateOrder() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader1.placeNewMarketOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
			trader1.placeNewMarketOrder(m, "SBUX", 100, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewMarketOrderUnownedStock() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 1600);
			trader1.placeNewMarketOrder(m, "MSFT", 100, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testPlaceNewMarketOrderInsufficientStocks() {
		Market m = new Market("NASDAQ");
		IPO.enterNewStock(m, "SBUX", "Starbucks Corp.", 92.86);
		Trader trader1 = new Trader("Neda", 200000.00);
		try{
			trader1.buyFromBank(m, "SBUX", 100);
			trader1.placeNewMarketOrder(m, "SBUX", 102, 97.0, OrderType.SELL);
		} 
		catch (StockMarketExpection e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testAddToOrderBookNormal() {
		Market m = new Market("NASDAQ");
		OrderBook b = new OrderBook(m);
		Trader trader1 = new Trader("Neda", 200000.00);
		BuyOrder order = new BuyOrder("SBUX", 10, 100.00, trader1);

		try
		{
			b.addToOrderBook(order);
		}
		catch(StockMarketExpection e)
		{
			fail("Should not throw an exception");
		}

		Assert.assertEquals(order, b.getBuyOrders().get("SBUX").get(0));
	}

	@Test
	public void testAddToOrderBookNullParameter() {
		Market m = new Market("NASDAQ");
		OrderBook b = new OrderBook(m);
		try
		{
			b.addToOrderBook(null);
		}
		catch(StockMarketExpection e)
		{
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testFindMatchingSellOrderNormal() {
		Market m = new Market("NASDAQ");
		OrderBook b = new OrderBook(m);
		Trader trader1 = new Trader("Neda", 200000.00);
		Trader trader2 = new Trader("Yoda", 20000.00);
		SellOrder order = new SellOrder("SBUX", 10, 100.00, trader1);
		BuyOrder bo = new BuyOrder("SBUX", 10, 100.00, trader2);
		try
		{
			b.addToOrderBook(order);
			Assert.assertEquals(order, b.findMatchingSellOrder(bo));
		}
		catch(StockMarketExpection e)
		{
			fail("Should not throw an exception");
		}
	}

	@Test
	public void testFindMatchingSellOrderNullParameter() {
		Market m = new Market("NASDAQ");
		OrderBook b = new OrderBook(m);
		Trader trader1 = new Trader("Neda", 200000.00);
		SellOrder order = new SellOrder("SBUX", 10, 100.00, trader1);
		try
		{
			b.addToOrderBook(order);
			b.findMatchingSellOrder(null);
		}
		catch(StockMarketExpection e)
		{
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testFindMatchingSellOrderNoMatch() {
		Market m = new Market("NASDAQ");
		OrderBook b = new OrderBook(m);
		Trader trader1 = new Trader("Neda", 200000.00);
		Trader trader2 = new Trader("Yoda", 20000.00);
		SellOrder order = new SellOrder("SBUX", 10, 10000000.00, trader1);
		BuyOrder bo = new BuyOrder("SBUX", 10, 100.00, trader2);
		try
		{
			b.addToOrderBook(order);
			Assert.assertEquals(null, b.findMatchingSellOrder(bo));
		}
		catch(StockMarketExpection e)
		{
			fail("Should not throw an exception");
		}
	}


}
