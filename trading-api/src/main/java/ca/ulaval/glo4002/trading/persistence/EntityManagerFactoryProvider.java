package ca.ulaval.glo4002.trading.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class EntityManagerFactoryProvider {

    private final static String PERSISTENCE_UNIT_NAME = "h2-InMemory";

    private static EntityManagerFactory instance;

    public static EntityManagerFactory getFactory() {
        if (instance == null) {
            instance = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return instance;
    }

}