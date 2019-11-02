package ca.ulaval.glo4002.trading.suites;

import ca.ulaval.glo4002.trading.TradingServer;
import ca.ulaval.glo4002.trading.context.LargeTradingModule;
import ca.ulaval.glo4002.trading.large.AccountResourceLargeTest;
import ca.ulaval.glo4002.trading.large.TransactionResourceLargeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses( {AccountResourceLargeTest.class,
        TransactionResourceLargeTest.class})
public class ApiLargeITestSuite {

    public static final int TEST_SERVER_PORT = 9090;
    public static Long investorIdCount = 0L;

    private static TradingServer tradingServer;

    @BeforeClass
    public static void setUpClass() {
        tradingServer = new TradingServer();
        LargeTradingModule guiceModule = new LargeTradingModule();
        tradingServer.start(TEST_SERVER_PORT, guiceModule);
    }

    @AfterClass
    public static void tearDownClass() {
        if (tradingServer != null) {
            tradingServer.stop();
        }
    }

}

