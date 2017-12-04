package vanzari.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import vanzari.domain.Produs;
import vanzari.domain.Stoc;
import vanzari.persistence.Exceptions.RepositoryException;
import vanzari.persistence.interfaces.IStocuriRepository;

/**
 * Created by elisei on 30.11.2017.
 */
public class StocuriHibernateRepository
    extends AbstractHibernateCrudRepository<Stoc, Integer>
    implements IStocuriRepository {
    public StocuriHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Stoc.class);
    }

    @Override
    public Stoc get(Produs produs, Session session) throws RepositoryException {
        Stoc stoc = null;
        for (Stoc s : getAll(session)) {
            if (s.getProdus().getId().equals(produs.getId())) {
                stoc = s;
                break;
            }
        }
        return stoc;
    }
}
