package bookrecommender.client;

import bookrecommender.common.Libro;
import javafx.animation.ScaleTransition;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GestioneLibrerieController {
    @FXML private TreeTableView<Object> treeTableView;
    @FXML private TreeTableColumn<Object,String> nameColumn;
    @FXML private TreeTableColumn<Object,Integer> countColumn;
    @FXML private TreeTableColumn<Object, LocalDate> dateColumn;
    @FXML private TreeTableColumn<Object, Void> azioniColumn;
    @FXML private TextField NomeLibreria;
    @FXML private Button BottoneCambiaNome;

    private TreeItem<Object> rootItem;
    private final Map<String,Integer> libCounts = new HashMap<>();
    private final Map<String,LocalDate> libDates = new HashMap<>();

    @FXML
    public void initialize() {
        rootItem = new TreeItem<>();
        treeTableView.setRoot(rootItem);
        treeTableView.setShowRoot(false);

        nameColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String) {
                return new ReadOnlyStringWrapper((String)v);
            }
            if (v instanceof Libro) {
                return new ReadOnlyStringWrapper(((Libro)v).getTitolo());
            }
            return new ReadOnlyStringWrapper("");
        });

        countColumn.setStyle("-fx-alignment: CENTER;");
        countColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String nome) {
                Integer cnt = libCounts.get(nome);
                return new ReadOnlyObjectWrapper<>(cnt);
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        dateColumn.setStyle("-fx-alignment: CENTER;");
        dateColumn.setCellValueFactory(c -> {
            Object v = c.getValue().getValue();
            if (v instanceof String nome) {
                return new ReadOnlyObjectWrapper<>(libDates.get(nome));
            }
            return new ReadOnlyObjectWrapper<>(null);
        });

        azioniColumn.setCellFactory(col-> new TreeTableCell<>() {
            private final MenuButton menuLibrerie = new MenuButton();
            {
                MenuItem modifica = new MenuItem("Modifica Libreria");
                MenuItem rimuovi = new MenuItem("Elimina Libreria");
                menuLibrerie.getItems().addAll(modifica, rimuovi);
                menuLibrerie.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 16, 16, true, true)));
                menuLibrerie.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
                );
                ScaleTransition enlarge = new ScaleTransition(Duration.millis(100), menuLibrerie);
                enlarge.setToX(1.1);
                enlarge.setToY(1.1);

                ScaleTransition shrink = new ScaleTransition(Duration.millis(100), menuLibrerie);
                shrink.setToX(1.0);
                shrink.setToY(1.0);

                menuLibrerie.setOnMouseEntered(e -> {
                    shrink.stop();
                    enlarge.playFromStart();
                });

                menuLibrerie.setOnMouseExited(e -> {
                    enlarge.stop();
                    shrink.playFromStart();
                });

                modifica.setOnAction(evt -> {
                    Object o = getTableRow().getItem();
                    if(o instanceof String) {
                        CliUtil.getInstance().buildStage(FXMLtype.MODIFICALIBRERIA, getTableRow().getItem());
                    }else
                        CliUtil.getInstance().createAlert("Errore", "Modifica button on a " + o.getClass() + " instead of String").showAndWait();
                });

                rimuovi.setOnAction(evt -> {
                    Object o = getTableRow().getItem();
                    if(o instanceof String) {
                        if(CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler eliminare la libreria " + o + "?", true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES){
                            try {
                                if(CliUtil.getInstance().getLibService().deleteLib(CliUtil.getInstance().getCurrentToken(), (String) o)){
                                    CliUtil.getInstance().createConfirmation("Successo", "Libreria eliminata con successo!",false).showAndWait();
                                    caricaLibrerie();
                                }else
                                    CliUtil.getInstance().createAlert("Errore", "Errore nell'eliminazione della librerie").showAndWait();
                            } catch (RemoteException e) {
                                CliUtil.getInstance().createAlert("Failed to delete " + o.getClass().getSimpleName(), e.getMessage()).showAndWait();
                            }
                        }
                    }else
                        CliUtil.getInstance().createAlert("Errore", "Elimina button on a " + o.getClass() + " instead of String").showAndWait();

                });
            }
            private final MenuButton menuLibri = new MenuButton();
            {
                MenuItem valuta = new MenuItem("Aggiungi Valutazione");
                MenuItem consiglia = new MenuItem("Aggiungi Consigli");
                MenuItem rimuovi = new MenuItem("Rimuovi dalla libreria");

                menuLibri.getItems().addAll(valuta, consiglia, rimuovi);
                menuLibri.setGraphic(new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/bookrecommender/client/icons/arrow_down_icon.png")), 16, 16, true, true)));
                menuLibri.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-border-color: transparent;" +
                                "-fx-padding: 0;" +
                                "-fx-cursor: hand;" +
                                "-fx-focus-color: transparent;" +
                                "-fx-faint-focus-color: transparent;"
                );
                ScaleTransition enlarge = new ScaleTransition(Duration.millis(100), menuLibri);
                enlarge.setToX(1.1);
                enlarge.setToY(1.1);

                ScaleTransition shrink = new ScaleTransition(Duration.millis(100), menuLibri);
                shrink.setToX(1.0);
                shrink.setToY(1.0);

                menuLibri.setOnMouseEntered(e -> {
                    shrink.stop();
                    enlarge.playFromStart();
                });

                menuLibri.setOnMouseExited(e -> {
                    enlarge.stop();
                    shrink.playFromStart();
                });

                valuta.setOnAction(evt -> {
                    Object o = getTableRow().getItem();
                    if(o instanceof Libro) {
                        CliUtil.getInstance().buildStage(FXMLtype.CREAVALUTAZIONE, o);
                    }else{
                        CliUtil.getInstance().createAlert("Errore","ValutaButton needs object Libro, provided " + o.getClass()).showAndWait();
                    }
                });

                consiglia.setOnAction(evt -> {
                    Object o = getTableRow().getItem();
                    if(o instanceof Libro) {
                        CliUtil.getInstance().buildStage(FXMLtype.CREACONSIGLIO, o);
                    }else{
                        CliUtil.getInstance().createAlert("Errore","ValutaButton needs object Libro, provided " + o.getClass()).showAndWait();
                    }
                });

                rimuovi.setOnAction(evt -> {
                    TreeItem<Object> thisItem = getTableRow().getTreeItem();
                    if (thisItem == null)
                        return;

                    TreeItem<Object> libNode = thisItem.getParent();
                    if (libNode == null)
                        return;

                    Object libValue = libNode.getValue();
                    if (!(libValue instanceof String nomeLibreria))
                        return;

                    List<Libro> libMod = libNode.getChildren().stream().map(TreeItem::getValue).filter(v -> v instanceof Libro).map(v -> (Libro) v).collect(Collectors.toList());

                    Libro l = (Libro) thisItem.getValue();
                    libMod.removeIf(b -> b.equals(l));
                    if(libMod.size() < 3){
                        CliUtil.getInstance().createAlert("Errore","Non puoi rimuovere questo libro!\nLa libreria deve contenere almeno 3 libri").showAndWait();
                        return;
                    }

                    if(CliUtil.getInstance().createConfirmation("Conferma", "Sei sicuro di voler rimuovere questo libro dalla libreria?",true).showAndWait().orElse(ButtonType.YES) == ButtonType.YES) {
                        try {
                            List<Integer> risultati = CliUtil.getInstance().getLibService().updateLib(CliUtil.getInstance().getCurrentToken(), nomeLibreria, libMod);
                            if (!risultati.isEmpty() && risultati.get(0) == 1) {
                                CliUtil.getInstance().createConfirmation("Aggiornamento riuscito", "Il libro è stato rimosso correttamente alla libreria '" + nomeLibreria + "'.", false).showAndWait();
                                caricaLibrerie();
                            } else {
                                StringBuilder sb = new StringBuilder("Impossibile aggiornare la libreria:\n");
                                for (int i = 1; i < risultati.size(); i += 2) {
                                    int idLibro = risultati.get(i);
                                    int codice = risultati.get(i + 1);
                                    switch (codice) {
                                        case 0:
                                            sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" ha valutazioni associate.");
                                            break;
                                        case 1:
                                            sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" è stato utilizzato come consiglio.");
                                            break;
                                        case 2:
                                            sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" ha libri consigliati ad esso associati.");
                                            break;
                                        default:
                                            sb.append("Il libro con titolo ").append(CliUtil.getInstance().getSearchService().getLibro(idLibro)).append(" ha codice errore sconosciuto: ").append(codice).append(".");
                                    }
                                    if (i + 2 < risultati.size()) sb.append(System.lineSeparator());
                                }
                                CliUtil.getInstance().createAlert("Errore Aggiornamento", sb.toString()).showAndWait();
                            }
                        } catch (RemoteException ex) {
                            CliUtil.getInstance().createAlert("Errore", ex.getMessage()).showAndWait();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Object o = getTableRow().getItem();
                if (o instanceof String) {
                    setGraphic(menuLibrerie);
                    setAlignment(Pos.CENTER);
                } else {
                    setGraphic(menuLibri);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        treeTableView.setRowFactory(tv -> {
            TreeTableRow<Object> row = new TreeTableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Object v = row.getItem();
                    if (v instanceof String nomeLib) {
                        CliUtil.getInstance().buildStage(FXMLtype.MODIFICALIBRERIA, nomeLib);
                    }else if (v instanceof Libro) {
                        CliUtil.getInstance().buildStage(FXMLtype.DETTAGLIOLIBRO,v);
                    }
                }
            });
            return row;
        });

        treeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> BottoneCambiaNome.setDisable(newSel == null || !(newSel.getValue() instanceof String)));
        caricaLibrerie();
    }

    private void caricaLibrerie() {
        rootItem.getChildren().clear();
        try {
            List<String> libs = CliUtil.getInstance().getLibService().getLibs(CliUtil.getInstance().getCurrentToken());
            for (String nome : libs) {
                List<Libro> libri = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nome);
                libCounts.put(nome, libri.size());
                libDates.put(nome, CliUtil.getInstance().getLibService().getCreationDate(CliUtil.getInstance().getCurrentToken(), nome));

                TreeItem<Object> libNode = new TreeItem<>(nome);
                libNode.getChildren().add(new TreeItem<>());
                libNode.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
                    TreeItem<Object> item = e.getTreeItem();
                    if (item.getChildren().size() == 1 && item.getChildren().get(0).getValue() == null) {
                        item.getChildren().clear();
                        caricaFigliLibri(item, nome);
                    }
                });
                rootItem.getChildren().add(libNode);
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore caricamento librerie", e.getMessage()).showAndWait();
        }
    }

    private void caricaFigliLibri(TreeItem<Object> libNode, String nomeLib) {
        try {
            List<Libro> libri = CliUtil.getInstance().getLibService().getLib(CliUtil.getInstance().getCurrentToken(), nomeLib);
            for (Libro l : libri) {
                TreeItem<Object> libroNode = new TreeItem<>(l);
                libNode.getChildren().add(libroNode);
            }
        } catch (RemoteException e) {
            CliUtil.getInstance().createAlert("Errore caricamento libri", e.getMessage()).showAndWait();
        }
    }

    @FXML private void cambiaNome() {
        Object o = treeTableView.getSelectionModel().getSelectedItem().getValue();
        if(o instanceof String) {
            if(NomeLibreria.getText().trim().length() < 5 || NomeLibreria.getText().trim().length() > 50) {
                CliUtil.getInstance().createAlert("Errore","Il titolo deve essere compreso tra 5 e 50 caratteri!").showAndWait();
            }else if(NomeLibreria.getText().trim().equals(o)) {
                CliUtil.getInstance().createAlert("Errore","Il titolo deve essere diverso dal precedente!").showAndWait();
            }else{
                try {
                    if(CliUtil.getInstance().getLibService().modifyLibName(CliUtil.getInstance().getCurrentToken(), (String) o, NomeLibreria.getText().trim())) {
                        CliUtil.getInstance().createConfirmation("Successo", "Nome Libreria modificato correttamente", false).showAndWait();
                        NomeLibreria.clear();
                        caricaLibrerie();
                    }
                    else
                        CliUtil.getInstance().createAlert("Errore", "Modifica del nome della libreria fallito!").showAndWait();
                }catch(RemoteException e){
                    CliUtil.getInstance().createAlert("Errore", "Errore nella connessione al server").showAndWait();
                }
            }
        }else CliUtil.getInstance().createAlert("Errore", "CambiaNomeButton needs String type, provided " + o.getClass().getSimpleName()).showAndWait();
    }


    @FXML private void ExitApplication() {
        CliUtil.getInstance().buildStage(FXMLtype.AREARISERVATA,null);
    }

    public void creaLibreria() {
        CliUtil.getInstance().buildStage(FXMLtype.CREALIBRERIA,null);
    }
}
