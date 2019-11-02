package ca.ulaval.glo4002.trading.context;

import ca.ulaval.glo4002.trading.application.service.*;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.persistence.H2InMemoryAccountRepository;
import com.google.inject.AbstractModule;


public class MediumTradingModule extends AbstractModule {

    private static AccountRepository accountRepository;
    private static MarketApiCaller marketApiCaller;
    private static StockApiCaller stockApiCaller;

    public void setAccountRepository(AccountRepository accountRepo) {
        accountRepository = accountRepo;
    }

    public void setStockApiCaller(StockApiCaller stockCaller) {
        stockApiCaller = stockCaller;
    }

    public void setMarketApiCallerMock(MarketApiCaller marketCaller) {
        marketApiCaller = marketCaller;
    }

    @Override
    protected void configure() {

        bind(ExchangeRateConverter.class).to(StaticExchangeRateConverter.class);

        if (accountRepository == null) {
            bind(AccountRepository.class).to(H2InMemoryAccountRepository.class);
        } else {
            bind(AccountRepository.class).toInstance(accountRepository);
        }

        if (stockApiCaller == null) {
            bind(StockApiCaller.class).to(FakeStockApiCaller.class);
        } else {
            bind(StockApiCaller.class).toInstance(stockApiCaller);
        }

        if (marketApiCaller == null) {
            bind(MarketApiCaller.class).to(FakeMarketApiCaller.class);
        } else {
            bind(MarketApiCaller.class).toInstance(marketApiCaller);
        }
    }
}
