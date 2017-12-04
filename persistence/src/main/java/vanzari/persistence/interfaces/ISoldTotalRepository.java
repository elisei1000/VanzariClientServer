package vanzari.persistence.interfaces;

import org.hibernate.Session;
import org.hibernate.Transaction;
import vanzari.persistence.Exceptions.RepositoryException;

/**
 * Created by elisei on 30.11.2017.
 */
public interface ISoldTotalRepository {
    void addValue(float value, Session session) throws RepositoryException;
    void substractValue(float value, Session session) throws RepositoryException;
    float getTotal(Session session) throws  RepositoryException;
}
