import org.hibernate.SessionFactory;
import org.junit.Test;
import vanzari.domain.Produs;
import vanzari.persistence.Exceptions.RepositoryException;
import vanzari.persistence.ProduseHibernateRepository;
import vanzari.persistence.Util;


/**
 * Created by elisei on 30.11.2017.
 */
public class TestHibernateClass {



    @Test
    public  void testRepository(){

        SessionFactory sessionFactory = Util.getSessionFactory();
        ProduseHibernateRepository repository =
                new ProduseHibernateRepository(sessionFactory);

        Produs p = new Produs(-1, "Cartofi", 1.2f,
                "kg");

        try {
            p = repository.add(p);
        } catch (RepositoryException e) {
            e.printStackTrace();
            assert (false);
        }

        try {
            p = repository.add(p);
            assert (false);
        } catch (RepositoryException e) {

        }

        p.setUnitateDeMasura("Masa");
        try{
            repository.update(p);
        } catch (RepositoryException e) {
            assert(false);
        }

        try {
            assert(repository.get(p.getId()).getUnitateDeMasura().equals("Masa"));
        } catch (RepositoryException e) {
            e.printStackTrace();
        }


        try{
            repository.update(new Produs(-1, "Maria", (float) 0.2, "M"));
            assert(false);
        } catch (RepositoryException e) {

        }

        try {
            repository.remove(p.getId());

        } catch (RepositoryException e) {
            assert(false);
        }


        try {
            repository.remove(p.getId());
            assert(false);
        } catch (RepositoryException e) {

        }

        try {
            assert(repository.get(p.getId()) == null);
        } catch (RepositoryException e) {

        }
    }
}
