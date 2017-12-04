package vanzari.services.rmi;

import vanzari.domain.Produs;
import vanzari.domain.Vanzare;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by elisei on 01.12.2017.
 */
public interface IServer extends Remote {
    void subscribe(IClient client) throws RemoteException, VanzariServiceException;

    void unsubscribe(IClient client)throws RemoteException, VanzariServiceException;


    List<Vanzare> getVanzari() throws RemoteException, VanzariServiceException;

    List<Produs> getProduse() throws RemoteException, VanzariServiceException;

    void addVanzare(Vanzare vanzare) throws RemoteException, VanzariServiceException;

}
