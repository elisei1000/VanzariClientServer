package vanzari.persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import vanzari.domain.HasId;
import vanzari.persistence.Exceptions.RepositoryException;

import java.io.Serializable;
import java.util.List;

/**
 * Created by elisei on 30.11.2017.
 */
public interface AbstractCrudRepository<E extends HasId<ID>, ID extends Serializable> {

    E add(E e, Session session) throws RepositoryException;
    void update(E e, Session session) throws RepositoryException;
    E remove(ID id, Session session) throws RepositoryException;
    int size(Session session) throws RepositoryException;
    List<E> getAll(Session session) throws RepositoryException;
    E get(ID id, Session session) throws RepositoryException;
}
