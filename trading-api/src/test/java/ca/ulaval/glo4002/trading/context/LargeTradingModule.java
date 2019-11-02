package ca.ulaval.glo4002.trading.context;

import ca.ulaval.glo4002.trading.application.service.*;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.persistence.H2InMemoryAccountRepository;
import com.google.inject.AbstractModule;

public class LargeTradingModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(ExchangeRateConverter.class).to(StaticExchangeRateConverter.class);
        bind(AccountRepository.class).to(H2InMemoryAccountRepository.class);
        bind(StockApiCaller.class).to(FakeStockApiCaller.class);
        bind(MarketApiCaller.class).to(FakeMarketApiCaller.class);

    }
}
