package ca.ulaval.glo4002.trading.suites;

import ca.ulaval.glo4002.trading.TradingServer;
import ca.ulaval.glo4002.trading.application.service.MarketApiCaller;
import ca.ulaval.glo4002.trading.application.service.StockApiCaller;
import ca.ulaval.glo4002.trading.context.MediumTradingModule;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.interfaces.rest.AccountsResourceRestITest;
import ca.ulaval.glo4002.trading.interfaces.rest.TransactionResourceRestITest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import static org.mockito.Mockito.mock;


@RunWith(Suite.class)
@SuiteClasses( {AccountsResourceRestITest.class,
        TransactionResourceRestITest.class})
public class ApiITestSuite {

    public static final int TEST_SERVER_PORT = 9090;
    public static AccountRepository repositoryMock;
    public static StockApiCaller stockApiCallerMock;
    public static MarketApiCaller marketApiCallerMock;

    private static TradingServer tradingServer;

    @BeforeClass
    public static void setUpClass() {
        tradingServer = new TradingServer();

        repositoryMock = mock(AccountRepository.class);
        stockApiCallerMock = mock(StockApiCaller.class);
        marketApiCallerMock = mock(MarketApiCaller.class);

        MediumTradingModule guiceModule = new MediumTradingModule();
        guiceModule.setAccountRepository(repositoryMock);
        guiceModule.setStockApiCaller(stockApiCallerMock);
        guiceModule.setMarketApiCallerMock(marketApiCallerMock);

        tradingServer.start(TEST_SERVER_PORT, guiceModule);
    }

    @AfterClass
    public static void tearDownClass() {
        if (tradingServer != null) {
            tradingServer.stop();
        }
    }

}
