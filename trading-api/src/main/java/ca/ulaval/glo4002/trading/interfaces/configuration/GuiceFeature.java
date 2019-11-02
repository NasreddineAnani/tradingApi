package ca.ulaval.glo4002.trading.interfaces.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.InjectionManagerProvider;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;


public class GuiceFeature implements Feature {

    private static AbstractModule module;

    public static void setGuiceModule(AbstractModule injectedModule) {
        module = injectedModule;
    }

    @Override
    public boolean configure(FeatureContext featureContext) {
        ServiceLocator locator = InjectionManagerProvider.getInjectionManager(featureContext)
                .getInstance(ServiceLocator.class);
        Injector injector;

        if (module == null) {
            injector = Guice.createInjector(new ProdTradingModule());
        } else {
            injector = Guice.createInjector(module);
        }

        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);
        GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
        return true;
    }
}
