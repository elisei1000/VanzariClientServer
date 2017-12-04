package vanzari.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import vanzari.domain.Vanzare;
import vanzari.persistence.Exceptions.RepositoryException;
import vanzari.persistence.interfaces.IVanzariRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by elisei on 30.11.2017.
 */
public class VanzariHibernateRepository
    extends AbstractHibernateCrudRepository<Vanzare, Integer>
    implements IVanzariRepository {
    public VanzariHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory, Vanzare.class);
    }

    @Override
    public List<Vanzare> getAllNotOldThan(Date date, Session session) throws RepositoryException {
        Transaction tx;
        boolean txNull = false;
        if(session == null) {
            txNull = true;
            session = sessionFactory.openSession();
            tx = session.beginTransaction();
        }
        else{
            tx = session.getTransaction();
        }
        try {
            List<Vanzare> list =  session.createQuery(

                            "from Vanzare " +
                                    "as p where p.date > :date order by p.id asc",
                    Vanzare.class)
                    .setParameter("date", date)
                    .list();
            if(txNull)
                tx.commit();
            return list;
        } catch (Exception e) {
            throw new RepositoryException("Unhandled exception: " + e.getMessage());
        }
        finally {
            if(txNull)
                session.close();
        }
    }
}
