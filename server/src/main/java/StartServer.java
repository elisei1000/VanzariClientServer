import org.hibernate.Session;
import vanzari.domain.Produs;
import vanzari.domain.Stoc;
import vanzari.domain.Vanzare;
import vanzari.persistence.*;
import vanzari.persistence.Exceptions.RepositoryException;
import vanzari.persistence.interfaces.*;
import vanzari.server.Server;
import vanzari.services.rmi.IServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

/**
 * Created by elisei on 30.11.2017.
 */
public class StartServer {

    public static void main(String[] args){
        IProduseRepository produseRepository;
        IVanzariRepository vanzariRepository;
        IFacturiRepository facturiRepository;
        IStocuriRepository stocuriRepository;
        ISoldTotalRepository soldTotalRepository;


        produseRepository = new ProduseHibernateRepository(Util.getSessionFactory());
        vanzariRepository =  new VanzariHibernateRepository(Util.getSessionFactory());
        facturiRepository = new FacturiHibernateRepository(Util.getSessionFactory());
        stocuriRepository = new StocuriHibernateRepository(Util.getSessionFactory());


        try {
            Session session = Util.getSessionFactory().openSession();
            session.beginTransaction();
            for(Produs produs : produseRepository.getAll(session)){
                if(stocuriRepository.get(produs, session) == null){
                    stocuriRepository.add(new Stoc(0, produs, 100000), session);
                }
            }

            for(Vanzare vanzare: vanzariRepository.getAllNotOldThan(new Date(), session)){
                System.out.println("Vanzare");
            }

            session.close();
        } catch (RepositoryException e) {
            e.printStackTrace();
        }

        try {
            soldTotalRepository = new SoldTotalHibernateRepository(Util.getSessionFactory());
        } catch (RepositoryException e) {
            e.printStackTrace();
            return;
        }

        IServer serverImp = new Server(produseRepository, vanzariRepository, stocuriRepository,
                soldTotalRepository , facturiRepository);
        try{
            String name = "vanzariServer";
            IServer stub = (IServer) UnicastRemoteObject.exportObject(serverImp, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("Server binded");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
