package ca.ulaval.glo4002.trading.interfaces.configuration;

import ca.ulaval.glo4002.trading.application.service.*;
import ca.ulaval.glo4002.trading.domain.ExchangeRateConverter;
import ca.ulaval.glo4002.trading.domain.persistence.AccountRepository;
import ca.ulaval.glo4002.trading.persistence.H2InMemoryAccountRepository;
import com.google.inject.AbstractModule;


public class ProdTradingModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountRepository.class).to(H2InMemoryAccountRepository.class);
        bind(ExchangeRateConverter.class).to(StaticExchangeRateConverter.class);
        bind(StockApiCaller.class).to(LocalStockApiCaller.class);
        bind(MarketApiCaller.class).to(LocalMarketApiCaller.class);
    }
}

