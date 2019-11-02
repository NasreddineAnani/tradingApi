package ca.ulaval.glo4002.application;

import ca.ulaval.glo4002.stocks.StocksServer;
import ca.ulaval.glo4002.trading.TradingServer;

public class ApplicationServer {
    public static void main(String[] args) throws InterruptedException {
        Thread stocks = new Thread(new StocksServer(args));
        Thread trading = new Thread(new TradingServer());

        trading.start();
        stocks.start();

        trading.join();
        stocks.join();
    }
}
