package vanzari.persistence.interfaces;

import org.hibernate.Session;
import org.hibernate.Transaction;
import vanzari.domain.Produs;
import vanzari.domain.Stoc;
import vanzari.persistence.AbstractCrudRepository;
import vanzari.persistence.Exceptions.RepositoryException;

/**
 * Created by elisei on 30.11.2017.
 */
public interface IStocuriRepository extends AbstractCrudRepository<Stoc, Integer> {

    Stoc get(Produs produs, Session session) throws RepositoryException;
}
