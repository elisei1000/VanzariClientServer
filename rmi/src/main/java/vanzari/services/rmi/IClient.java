package vanzari.services.rmi;

import vanzari.domain.Produs;
import vanzari.domain.Vanzare;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by elisei on 01.12.2017.
 */
public interface IClient extends Remote, Serializable {
    void ProduseChanged(List<Produs> produses) throws VanzariServiceException, RemoteException;

    void VanzareChanged(List<Vanzare> vanzari) throws VanzariServiceException, RemoteException;

}
