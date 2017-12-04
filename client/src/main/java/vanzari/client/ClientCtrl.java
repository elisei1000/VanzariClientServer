package vanzari.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vanzari.domain.Produs;
import vanzari.domain.Vanzare;
import vanzari.services.rmi.IClient;
import vanzari.services.rmi.IServer;
import vanzari.services.rmi.VanzariServiceException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Created by elisei on 01.12.2017.
 */
public class ClientCtrl extends UnicastRemoteObject implements IClient, Serializable {
    private final IServer server;


    private transient ObservableList<Produs> produsesModel;
    private transient ObservableList<Vanzare> vanzariModel;

    public ClientCtrl(IServer server) throws RemoteException {
        super();

        this.server = server;
        this.produsesModel = FXCollections.observableArrayList();
        this.vanzariModel = FXCollections.observableArrayList();
        try {
            server.subscribe(this);
        } catch (VanzariServiceException e) {
            System.out.println("Cannot subscribe to server");
            e.printStackTrace();
        }

        try{
            UnicastRemoteObject.exportObject(this, 0);
        } catch (RemoteException e) {
            System.out.println("Error exporting object! " + e.getMessage() );
        }
    }

    public void disconect(){
        try {
            server.unsubscribe(this);
        } catch (RemoteException | VanzariServiceException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Produs> getAllProduses() throws VanzariServiceException{
        List<Produs> list = null;
        try{
            list = server.getProduse();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new VanzariServiceException("Error gettign all produses!");
        }
        produsesModel.setAll(list);
        return produsesModel;
    }

    public ObservableList<Vanzare> getAllVanzari() throws VanzariServiceException{
        List<Vanzare> list = null;
        try{
            list = server.getVanzari();
        } catch (RemoteException e) {
            e.printStackTrace();
            throw new VanzariServiceException("Error gettign all produses!");
        }
        vanzariModel.setAll(list);
        return vanzariModel;
    }

    public void addVanzare(Vanzare vanzare) throws VanzariServiceException {
        try{
            server.addVanzare(vanzare);
        } catch (RemoteException e) {
            e.printStackTrace();
            throw  new VanzariServiceException("Error saving Vanzare!");
        }
    }

    @Override
    public void ProduseChanged(List<Produs> produses) throws VanzariServiceException, RemoteException {
        Platform.runLater(()-> {
            try {
                getAllProduses();
            } catch (VanzariServiceException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public void VanzareChanged(List<Vanzare> vanzari) throws VanzariServiceException, RemoteException {
        Platform.runLater(()->{
            try {
                getAllVanzari();
            } catch (VanzariServiceException e) {
                e.printStackTrace();
            }

        });
    }
}
