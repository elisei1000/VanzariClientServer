package vanzari.persistence.interfaces;

import org.hibernate.Session;
import vanzari.domain.Vanzare;
import vanzari.persistence.AbstractCrudRepository;
import vanzari.persistence.Exceptions.RepositoryException;

import java.util.Date;
import java.util.List;

/**
 * Created by elisei on 30.11.2017.
 */
public interface IVanzariRepository extends AbstractCrudRepository<Vanzare, Integer> {

    List<Vanzare> getAllNotOldThan(Date date, Session session) throws RepositoryException;
}
