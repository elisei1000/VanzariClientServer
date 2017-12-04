package vanzari.persistence;

import org.hibernate.SessionFactory;
import vanzari.domain.Factura;
import vanzari.persistence.interfaces.IFacturiRepository;

/**
 * Created by elisei on 30.11.2017.
 */
public class FacturiHibernateRepository
        extends AbstractHibernateCrudRepository<Factura, Integer>
        implements IFacturiRepository {
    public FacturiHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Factura.class);
    }
}
