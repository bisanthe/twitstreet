package com.twitstreet.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.twitstreet.config.ConfigMgr;
import com.twitstreet.db.data.Stock;
import com.twitstreet.db.data.TrendyStock;
import com.twitstreet.market.PortfolioMgr;
import com.twitstreet.market.StockMgr;
import com.twitstreet.session.UserMgr;
import com.twitstreet.twitter.TwitstreetAnnouncer;
import com.twitstreet.twitter.TwitterProxyFactory;
import com.twitstreet.twitter.TwitterProxyImpl;

@Singleton
public class StockUpdateTask implements Runnable {

	@Inject
	PortfolioMgr portfolioMgr;
	@Inject
	TwitstreetAnnouncer twitstreetAnnouncer;
	@Inject
	StockMgr stockMgr;
	@Inject
	ConfigMgr configMgr;
	@Inject
	UserMgr userMgr;
	@Inject
	TwitterProxyFactory twitterProxyFactory = null;
	private static Logger logger = Logger.getLogger(StockUpdateTask.class);
	public static int LAST_UPDATE_DIFF_MINUTES = 10;// minutes
	public static int LAST_UPDATE_DIFF_MILISECONDS = LAST_UPDATE_DIFF_MINUTES * 60 * 1000;

	@Override
	public void run() {

		while (true) {
			long startTime = System.currentTimeMillis();

			try {

				logger.info("\n\n************* Stock Update Task ****************\n\n");
				
				logger.info("Twitter trends update - begin.");
				stockMgr.updateTwitterTrends();
				logger.info("Twitter trends update - end.");

				logger.info("Stock list update - begin.");
				updateStocks();
				logger.info("Stock list update - end. ");

				logger.info("Reset speed of stocks - begin.");
				stockMgr.resetSpeedOfOldStocks();
				logger.info("Reset speed of stocks - end.");

				logger.info("Re-rank begin.");
				userMgr.rerank();
				logger.info("Re-rank end.");

				logger.info("Rank history update - begin.");
				userMgr.updateRankingHistory();
				logger.info("Rank history update - end. "); 

				logger.info("Mention trendy stock - begin.");
				mentionTopGrossingStocks();
				logger.info("Mention trendy stock - end. "); 
				
				logger.info("Remove old records - begin.");
				twitstreetAnnouncer.removeOldRecords(60 * 24);
				logger.info("Remove old records - end.");

				logger.info("\n\n************************************************\n\n");
			} catch (Throwable ex) {
				logger.error("Someone tried to kill our precious StockUpdateTask. He says: ", ex);
			}
		
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;

			if (diff < LAST_UPDATE_DIFF_MILISECONDS/2) {
				try {
					Thread.sleep(LAST_UPDATE_DIFF_MILISECONDS/2 - diff);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}

	private void mentionTopGrossingStocks() {
		try{
            if(stockMgr.getTopGrossingStocks(24).size() > 0 && !configMgr.isDev()){  
            	TrendyStock ts = stockMgr.getTopGrossingStocks(24).get(0);
            	twitstreetAnnouncer.mention(ts, ts.getAnnouncement(ts.getLanguage())+ "www.twitstreet.com/#!stock="+ts.getId());
            }
		}
		catch(Exception ex){
			logger.error("Cannot mention top grossing stock", ex);
		}
	}

	public void updateStocks() {
		List<Stock> updatedStocks = new ArrayList<Stock>();
		while (true) {
			List<Stock> stockList = stockMgr.getUpdateRequiredStocks(TwitterProxyImpl.USER_COUNT_FOR_UPDATE);

			if (stockList.size() == 0)
				break;
			ArrayList<Long> idList = new ArrayList<Long>();
			for (Stock stock : stockList) {
				idList.add(stock.getId());

			}

			stockMgr.updateStockListData(idList);
			
			updatedStocks.addAll(stockList);
		}
		
		logger.info("Updated "+updatedStocks.size()+ " stocks. \n");
		
		for (Stock stock : updatedStocks) {

			logger.debug(stock.toString()+"\n");
		}
		
	}

}
