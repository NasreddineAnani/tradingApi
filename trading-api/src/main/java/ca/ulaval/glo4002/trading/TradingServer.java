package ca.ulaval.glo4002.trading;

import ca.ulaval.glo4002.trading.interfaces.configuration.GuiceFeature;
import ca.ulaval.glo4002.trading.interfaces.configuration.ProdTradingModule;
import ca.ulaval.glo4002.trading.interfaces.rest.filters.EntityManagerContextFilter;
import com.google.inject.AbstractModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;


public class TradingServer implements Runnable {

    private static final int PORT = 8181;
    private static final AbstractModule DEFAULT_MODULE = new ProdTradingModule();

    private Server server;

    public static void main(String[] args) {
        TradingServer server = new TradingServer();
        server.start(PORT, DEFAULT_MODULE);
        server.join();
    }

    public void start(int httpPort, AbstractModule module) {
        server = new Server(httpPort);
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/");
        configureJersey(servletContextHandler, module);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void join() {
        try {
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryStopServer();
        }
    }

    private void tryStopServer() {
        try {
            server.destroy();
        } catch (Exception e) {
            return;
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        server = new Server(PORT);

        ServletContextHandler contextHandler = new ServletContextHandler(server, "/");
        configureJersey(contextHandler, DEFAULT_MODULE);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }

    private void configureJersey(ServletContextHandler contextHandler, AbstractModule module) {
        ResourceConfig packageConfig = new ResourceConfig()
                .packages("ca.ulaval.glo4002.trading.interfaces").register(GuiceFeature.class);

        GuiceFeature.setGuiceModule(module);

        ServletContainer container = new ServletContainer(packageConfig);
        ServletHolder servletHolder = new ServletHolder(container);
        contextHandler.addServlet(servletHolder, "/*");

        contextHandler.addFilter(EntityManagerContextFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
    }
}
