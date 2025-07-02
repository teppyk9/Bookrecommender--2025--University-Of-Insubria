package bookrecommender.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

public class GestioneLibrerieController {
    @FXML private TreeTableView treeTableView;
    @FXML private TreeTableColumn nameColumn;
    @FXML private TreeTableColumn countColumn;
    @FXML private TreeTableColumn dateColumn;
    @FXML private TextField NomeLibreria;
    @FXML private Button BottoneCambiaNome;
    @FXML private Button BottoneSalvaLibreria;
    @FXML private Button ExitButton;

    @FXML private void cambiaNome(ActionEvent actionEvent) {
    }

    @FXML private void SalvaLibreria(ActionEvent actionEvent) {
    }

    @FXML private void ExitApplication(ActionEvent actionEvent) {
    }
}
