import javafx.application.Application;
import javafx.stage.Stage;
import vanzari.client.ClientCtrl;
import vanzari.client.gui.VanzariWindow;
import vanzari.domain.Produs;
import vanzari.services.rmi.IServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by elisei on 01.12.2017.
 */
public class StartClient extends Application {

    private static void main(String [] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try{
            String name = "vanzariServer";
            Registry registry = LocateRegistry.getRegistry("localhost");
            IServer server = (IServer) registry.lookup(name);
            System.out.println("Obtained a reference to remote avioane server");
            ClientCtrl ctrl = new ClientCtrl(server);
            VanzariWindow vanzariWindow = new VanzariWindow("Vanzari", ctrl);
            vanzariWindow.show();
        }
        catch (Exception e){
            System.err.println("Initialization exception");
            e.printStackTrace();
        }
    }
}
