package vanzari.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import vanzari.domain.*;

/**
 * Created by elisei on 30.11.2017.
 */
public  class Util {

    private static SessionFactory sessionFactory = null;
    public static SessionFactory getSessionFactory(){
        if(sessionFactory == null) {
            Configuration configuration= new Configuration();
            configuration.configure("hibernate.cfg.xml");
            sessionFactory =  configuration.buildSessionFactory();
        }
        return sessionFactory;
    }
}
