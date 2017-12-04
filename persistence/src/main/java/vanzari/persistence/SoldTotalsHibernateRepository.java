package vanzari.persistence;

import org.hibernate.SessionFactory;
import vanzari.domain.SoldTotal;

/**
 * Created by elisei on 30.11.2017.
 */
class SoldTotalsHibernateRepository
    extends AbstractHibernateCrudRepository<SoldTotal, Integer>
{
    public SoldTotalsHibernateRepository(SessionFactory sessionFactory) {
        super(sessionFactory, SoldTotal.class);
    }
}
