package vanzari.server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.jcp.xml.dsig.internal.dom.Utils;
import vanzari.domain.Produs;
import vanzari.domain.SoldTotal;
import vanzari.domain.Stoc;
import vanzari.domain.Vanzare;
import vanzari.persistence.Exceptions.RepositoryException;
import vanzari.persistence.Util;
import vanzari.persistence.interfaces.*;
import vanzari.services.rmi.IClient;
import vanzari.services.rmi.IServer;
import vanzari.services.rmi.VanzariServiceException;

import javax.security.auth.callback.Callback;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by elisei on 01.12.2017.
 */
public class Server implements IServer {
    private final int NR_THREADS = 10;

    private final IFacturiRepository facturiRepository;
    private final ISoldTotalRepository soldTotalRepository;
    private final IStocuriRepository stocuriRepository;
    private final IVanzariRepository vanzariRepository;
    private final IProduseRepository produseRepository;
    private Set<IClient> clientSet;
    private FileWriter fileWrite;
    private  Lock printerLock;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Stoc> stocs;
    private List<Produs> produsList;
    private float soldTotal;
    private Date lastCheck;
    private Session session;


    private final ExecutorService executorService;

    public Server(IProduseRepository produseRepository, IVanzariRepository vanzariRepository,
                  IStocuriRepository stocuriRepository, ISoldTotalRepository soldTotalRepository,
                  IFacturiRepository facturiRepository){
        this.produseRepository = produseRepository;
        this.vanzariRepository = vanzariRepository;
        this.stocuriRepository = stocuriRepository;
        this.soldTotalRepository = soldTotalRepository;
        this.facturiRepository = facturiRepository;
        this.clientSet = new LinkedHashSet<>();
        this.executorService = Executors.newFixedThreadPool(NR_THREADS);
        printerLock = new ReentrantLock();
        try {
            fileWrite = new FileWriter("log.log");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Thread randomSeller = new Thread(this::randomSellerFunction);
        randomSeller.start();

        Thread sellsSheriff = new Thread(this::sellsSheriffFunction);
        sellsSheriff.start();
    }

    private void log(String msg, String prefix){
        Date date = new Date();
        printerLock.lock();
        try{
            fileWrite.write(String.format(
                    "[%s] [%s] %s\n",
                    simpleDateFormat.format(date),
                    prefix,
                    msg
            ));
            fileWrite.flush();

        } catch (IOException e) {
            System.out.println(String.format(
                    "Failed to log into file!\n[%s] [%s] %s\n\n",
                    prefix, msg));
            e.printStackTrace();
        } finally {
            printerLock.unlock();
        }
    }

    private List<Vanzare> getAllVanzare() throws  RepositoryException{
        log("getAllVanzare call", "Client");
        return vanzariRepository.getAll(null);
    }

    private List<Produs> getAllProduse() throws  RepositoryException{
        log("getAllProduse call", "Client");
        return produseRepository.getAll(null);
    }

    private Vanzare addVanzare(Produs p, float cantitate, Date date, String prefix) throws RepositoryException {
        Stoc stoc;
        SessionFactory sessionFactory = Util.getSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Vanzare vanzare = null;
        log(
                String.format(
                        "addVanzare called: Produs:(id: %s, nume: %s, pretUnitar: %s, unitateDeMasura: %s) cantitate: %s",
                        p.getId(), p.getNume(), p.getPretUnitar(), p.getUnitateDeMasura(), cantitate
                ), prefix);

        try{
            stoc = stocuriRepository.get(p, session);
            if(stoc == null)
                throw  new RepositoryException(String.format("No stock for produs %s", p.getId()));

            if(stoc.getCantitate() < cantitate)
                throw  new RepositoryException(String.format("There isn't suffient stock for %s for produs %s",
                        cantitate, p.getId()));

            vanzare = vanzariRepository.add(new Vanzare(1, date, p, cantitate), session);
            stoc.setCantitate(stoc.getCantitate() - cantitate);
            stocuriRepository.update(stoc, session);
            soldTotalRepository.addValue(cantitate  * p.getPretUnitar(), session);

            tx.commit();
        }
        catch (RepositoryException e){
            tx.rollback();
            throw  e;
        }
        finally {
            session.close();
        }
        return vanzare;

    }


    private boolean initVerifyStocks(){
        session = Util.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            stocs = stocuriRepository.getAll(session);
            produsList = produseRepository.getAll(session);
            soldTotal = soldTotalRepository.getTotal(session);
            lastCheck = new Date();
            session.getTransaction().commit();

        } catch (RepositoryException e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Integer verifyStocks(){
        boolean ok = true;
        session.beginTransaction();
        try{
            Date newLastCheck = new Date();
            List<Vanzare> vanzareList = vanzariRepository.getAllNotOldThan(lastCheck, session);
            List<Stoc> newStocs = stocuriRepository.getAll(session);
            float newSoldTotal = soldTotalRepository.getTotal(session);
            session.getTransaction().commit();

            float x = newSoldTotal;
            for(Stoc newStoc : newStocs){
                float delta = 0;
                boolean gasit = false;
                for(Stoc found : stocs){
                    if(found.getId().equals(newStoc.getId())){
                        delta = found.getCantitate() - newStoc.getCantitate();
                        gasit = true;
                        break;
                    }
                }

                if(!gasit){
                    System.out.println("Invalid stock check: stock not found!");
                    ok = false;
                    break;
                }

                float actValue = 0;
                for(Vanzare vanzare : vanzareList){
                    if(vanzare.getProdus().getId().equals(newStoc.getProdus().getId())){
                        actValue+= vanzare.getCantitate();
                    }
                }

                if(actValue != delta){
                    System.out.println("Invalid stock check: actual value from vanzari doesn't match for produs: #" +
                            newStoc.getProdus().getId() +
                            " ! actualValue: " + actValue +"; old: "+delta);
                    ok = false;
                    break;
                }
                x -= actValue * newStoc.getProdus().getPretUnitar();
            }

            if(x != soldTotal){
                System.out.println("Invalid stock check: Soldul gasit nu este acelasi cu cel calculat!");
                ok = false;
            }
            soldTotal = newSoldTotal;
            stocs = newStocs;
            lastCheck = newLastCheck;
            if(ok)
            {
                System.out.println("Ok stock check!");
                log("Check verification successfully finished with OK Status!", "SellSherif");
            }
            else
                log("Check verification finished with FAILED STATUS!", "SellSherif");

        }
        catch (RepositoryException e){
            session.getTransaction().rollback();
            log("Check verification error : " +e.getMessage(), "SellSherif");
            e.printStackTrace();
        }
        return 0;
    }

    private void sellsSheriffFunction(){

        try{
            Callable<Boolean> initCallable = this::initVerifyStocks;
            Future<Boolean> submit = executorService.submit(initCallable);
            if (submit.get()){

                while(true){
                    Thread.sleep(5000);

                    Callable<Integer> callable = this::verifyStocks;
                    Future<Integer> res = executorService.submit(callable);
                    try{
                        res.get();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                        break;
                    }
                    catch (ExecutionException e){
                        e.printStackTrace();
                    }
                }
            }
            else{
                System.out.println("Failed to init sellsSheriffFunction");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            System.out.println("Sells sheriff stoped");
        }


    }

    private void randomSellerFunction(){
        List<Produs> produsList;
        Random random = new Random();
        int size;
        try {
            Callable<List<Produs>> callable = () ->produseRepository.getAll(null);
            Future<List<Produs>> res = executorService.submit(callable);
            produsList = res.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }
        size = produsList.size();

        while(true){
            int index = random.nextInt(size);
            Produs p = produsList.get(index);
            Float cantitate;
            cantitate = random.nextFloat() * 10;

            try {
                Callable<Vanzare> callable = () -> this.addVanzare(p, cantitate, new Date(), "randomSeller");
                Future<Vanzare> submit = executorService.submit(callable);
                submit.get();

                notifyChangedVanzari();
                Thread.sleep(5000);
            }catch (InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Secondary thread finished!");
    }

    @Override
    public void subscribe(IClient client) throws RemoteException, VanzariServiceException {
        this.clientSet.add(client);
    }

    @Override
    public void unsubscribe(IClient client) throws RemoteException, VanzariServiceException {
        this.clientSet.remove(client);
    }


    @Override
    public List<Vanzare> getVanzari() throws RemoteException, VanzariServiceException {
        Callable<List<Vanzare>> callable = this::getAllVanzare;
        Future<List<Vanzare>> submit = executorService.submit(callable);
        try {
            return submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw  new VanzariServiceException("Can't get data!");
        }
        catch (ExecutionException e){
            e.printStackTrace();
            throw  new VanzariServiceException("Can't communicate with repository");
        }
    }

    @Override
    public List<Produs> getProduse() throws RemoteException, VanzariServiceException {
        Callable<List<Produs>> callable = this::getAllProduse;
        Future<List<Produs>> submit = executorService.submit(callable);
        try {
            return submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw  new VanzariServiceException("Can't get data!");
        }
        catch (ExecutionException e){
            e.printStackTrace();
            throw  new VanzariServiceException("Can't communicate with repository");
        }
    }

    @Override
    public void addVanzare(Vanzare vanzare) throws RemoteException, VanzariServiceException {
        Callable<Vanzare> callable = ()-> this.addVanzare(vanzare.getProdus(), vanzare.getCantitate(), vanzare.getDate(), "Client");
        Future<Vanzare> submit = executorService.submit(callable);
        try {
            submit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw  new VanzariServiceException("Can't get data!");
        }
        catch (ExecutionException e) {
            e.printStackTrace();
            throw new VanzariServiceException(e.getMessage());
        }

        notifyChangedVanzari();
    }

    private void notifyChangedVanzari() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<IClient> toRemove = new ArrayList<>();
        for(IClient client :clientSet){
            try{
                client.VanzareChanged(new ArrayList<>());
            }
            catch (ConnectException e){
                toRemove.add(client);
            }
            catch (RemoteException | VanzariServiceException e) {
                e.printStackTrace();
            }
        }
        if(toRemove.size() > 0)
            clientSet.removeAll(toRemove);
    }
}

