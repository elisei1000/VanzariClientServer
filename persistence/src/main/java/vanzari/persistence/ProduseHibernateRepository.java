package vanzari.persistence;

import org.hibernate.SessionFactory;
import vanzari.domain.Produs;
import vanzari.persistence.interfaces.IProduseRepository;

/**
 * Created by elisei on 30.11.2017.
 */
public class ProduseHibernateRepository
        extends AbstractHibernateCrudRepository<Produs, Integer>
        implements IProduseRepository {
    public ProduseHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Produs.class);
    }
}
