package vanzari.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import vanzari.domain.SoldTotal;
import vanzari.persistence.Exceptions.RepositoryException;
import vanzari.persistence.interfaces.ISoldTotalRepository;

import java.util.List;

/**
 * Created by elisei on 30.11.2017.
 */
public class SoldTotalHibernateRepository
        implements ISoldTotalRepository{
    private SoldTotalsHibernateRepository repository;
    private SoldTotal soldTotal = null;

    public SoldTotalHibernateRepository(SessionFactory sessionFactory) throws RepositoryException {
        repository = new SoldTotalsHibernateRepository(sessionFactory);

        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            List<SoldTotal> soldTotalList = repository.getAll(session);
            if(soldTotalList.size() == 0){
                soldTotal = new SoldTotal(1, 0);
                soldTotal = repository.add(soldTotal, session);
            }
            else{
                soldTotal = soldTotalList.get(soldTotalList.size() - 1);
            }
            tx.commit();
        } catch (RepositoryException e) {
            tx.rollback();
            throw e;
        }
        finally {

        }
    }

    @Override
    public void addValue(float value, Session session) throws RepositoryException {
        float total = value +  soldTotal.getTotalSum();
        soldTotal = repository.get(soldTotal.getId(), session);
        soldTotal.setTotalSum(total);
        repository.update(soldTotal, session);
    }

    @Override
    public void substractValue(float value, Session session) throws RepositoryException {
        float total = soldTotal.getTotalSum() - value;
        soldTotal = repository.get(soldTotal.getId(), session);
        soldTotal.setTotalSum(total);
        repository.update(soldTotal, session);
    }

    @Override
    public float getTotal(Session session) throws RepositoryException {
        return repository.get(soldTotal.getId(), session).getTotalSum();
    }

}
