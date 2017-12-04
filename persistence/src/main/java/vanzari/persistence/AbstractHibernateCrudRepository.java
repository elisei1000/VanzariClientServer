package vanzari.persistence;

import org.hibernate.*;
import vanzari.domain.HasId;
import vanzari.domain.ICloneable;
import vanzari.domain.ICloneableHasId;
import vanzari.persistence.Exceptions.RepositoryException;

import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by elisei on 30.11.2017.
 */
public abstract class AbstractHibernateCrudRepository<E
        //extends ICloneableHasId<E, ID>,
        extends HasId<ID>,
        ID extends Serializable>
        implements AbstractCrudRepository<E, ID> {


    protected final SessionFactory sessionFactory;
    private final Class<E> eClass;

    public AbstractHibernateCrudRepository(SessionFactory sessionFactory,
                                           Class<E> eClass){
        this.sessionFactory = sessionFactory;
        this.eClass = eClass;
    }

    @Override
    public E add(E entity, Session session) throws RepositoryException {
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

        ID id;
        try {

            E saved = session.get(eClass, entity.getId());
            if(saved != null)
                throw new EntityExistsException("Entity exists!");

            id = (ID)session.save(entity);
            if(txNull)
                tx.commit();
        } catch (EntityExistsException e) {
            //tx.rollback();
            throw new RepositoryException("Entity exists!");
        } catch (Exception e) {
            //if (tx != null)
                //tx.rollback();
            e.printStackTrace();
            throw new RepositoryException("Unexpected exception!" + e.getMessage());
        }
        finally {
            if(txNull)
                session.close();
        }
        entity.setId(id);
        return entity;
    }

    @Override
    public void update(E entity, Session session) throws RepositoryException {
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

            E e = session.load(eClass, entity.getId());
            e.getId();
            session.merge(entity);
            if(txNull)
                tx.commit();
        } catch (ObjectNotFoundException e) {
            throw new RepositoryException("Entity not exists! ");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RepositoryException("Unexpected exception!" + e.getMessage());
        }
        finally {
            if(txNull)
                session.close();
        }
    }

    @Override
    public E remove(ID id, Session session) throws RepositoryException {
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
        E entity = null;
        try {

            entity = session.load(eClass, id);
            entity.getId();
            session.delete(entity);
            if(txNull)
                tx.commit();
        } catch (ObjectNotFoundException e) {
            throw new RepositoryException("Entity not exists! " + e.getMessage());
        } catch (Exception e) {
            throw new RepositoryException("Unexpected exception!" + e.getMessage());
        }
        finally {
            if(txNull){
                session.close();
            }
        }
        return entity;
    }

    @Override
    public int size(Session  session) throws RepositoryException {
        return getAll(session).size();
    }

    @Override
    public List<E> getAll(Session session) throws RepositoryException {
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
            List<E> list =  session.createQuery(
                    String.format(
                            "from %s " +
                                    "as p order by p.id asc",
                            eClass.getName()),
                    eClass).list();
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

    @Override
    public E get(ID id, Session session) throws RepositoryException {
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
        E  entity = null;
        try {

            entity = session.get(eClass, id);
            if(txNull)
                tx.commit();
        } catch (ObjectNotFoundException ignored) {
        } catch (Exception e) {
            throw new RepositoryException("Unexpected exception!" + e.getMessage());
        }
        finally {
            if(txNull)
                session.close();
        }
        return entity;
    }

}
