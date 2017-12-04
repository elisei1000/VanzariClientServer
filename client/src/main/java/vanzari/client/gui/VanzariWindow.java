package vanzari.client.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vanzari.client.ClientCtrl;

import java.io.IOException;

/**
 * Created by elisei on 01.12.2017.
 */
public class VanzariWindow extends Stage {

    private ClientCtrl ctrl;
    private String title;

    private void initLayout(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(VanzariWindow.class.getResource("VanzariWindow.fxml"));

            BorderPane root = loader.load();
            VanzariWindowController controller = loader.getController();
            controller.setController(ctrl);
            controller.setView(root);
            Scene scene = new Scene(root, 1200, 700);
            this.setTitle(title);
            this.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VanzariWindow(String title, ClientCtrl ctrl){
        this.ctrl = ctrl;
        this.title = title;
        initLayout();
        this.setOnCloseRequest(windowEvent -> ctrl.disconect());
    }
}
