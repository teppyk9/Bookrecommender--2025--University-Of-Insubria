package bookrecommender.client.util;

import bookrecommender.client.enums.IMGtype;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public abstract class PasswordEngine {
    private boolean showPassword1 = false;
    private boolean showPassword2 = false;

    protected abstract PasswordField getPasswordField1();
    protected abstract PasswordField getPasswordField2();
    protected abstract TextField getVisiblePasswordField1();
    protected abstract TextField getVisiblePasswordField2();
    protected abstract Button getButton1();
    protected abstract Button getButton2();

    protected void initP1() {
        getVisiblePasswordField1().textProperty().bindBidirectional(getPasswordField1().textProperty());
        getVisiblePasswordField1().setVisible(false);
        getVisiblePasswordField1().setManaged(false);
        ImageView iv = new ImageView(IMGtype.VISIBLE_PASSWORD.getImage());
        iv.setPreserveRatio(true);
        iv.setFitWidth(24);
        iv.setFitHeight(24);
        getButton1().setGraphic(iv);
        CliUtil.getInstance().styleIconControl(getButton1());
    }

    protected void initP2() {
        getVisiblePasswordField2().textProperty().bindBidirectional(getPasswordField2().textProperty());
        getVisiblePasswordField2().setVisible(false);
        getVisiblePasswordField2().setManaged(false);
        ImageView iv = new ImageView(IMGtype.VISIBLE_PASSWORD.getImage());
        iv.setPreserveRatio(true);
        iv.setFitWidth(24);
        iv.setFitHeight(24);
        getButton2().setGraphic(iv);
        CliUtil.getInstance().styleIconControl(getButton2());
    }

    @FXML
    private void showP1() {
        if(showPassword1){
            ImageView iv = new ImageView(IMGtype.VISIBLE_PASSWORD.getImage());
            iv.setPreserveRatio(true);
            iv.setFitWidth(24);
            iv.setFitHeight(24);
            getButton1().setGraphic(iv);
            getPasswordField1().setVisible(true);
            getPasswordField1().setManaged(true);
            getVisiblePasswordField1().setVisible(false);
            getVisiblePasswordField1().setManaged(false);
            showPassword1 = false;
        } else {
            ImageView iv = new ImageView(IMGtype.INVISIBLE_PASSWORD.getImage());
            iv.setPreserveRatio(true);
            iv.setFitWidth(24);
            iv.setFitHeight(24);
            getButton1().setGraphic(iv);
            getPasswordField1().setVisible(false);
            getPasswordField1().setManaged(false);
            getVisiblePasswordField1().setVisible(true);
            getVisiblePasswordField1().setManaged(true);
            showPassword1 = true;
        }
    }

    @FXML private void showP2() {
        if(showPassword2){
            ImageView iv = new ImageView(IMGtype.VISIBLE_PASSWORD.getImage());
            iv.setPreserveRatio(true);
            iv.setFitWidth(24);
            iv.setFitHeight(24);
            getButton2().setGraphic(iv);
            getPasswordField2().setVisible(true);
            getPasswordField2().setManaged(true);
            getVisiblePasswordField2().setVisible(false);
            getVisiblePasswordField2().setManaged(false);
            showPassword2 = false;
        } else {
            ImageView iv = new ImageView(IMGtype.INVISIBLE_PASSWORD.getImage());
            iv.setPreserveRatio(true);
            iv.setFitWidth(24);
            iv.setFitHeight(24);
            getButton2().setGraphic(iv);
            getPasswordField2().setVisible(false);
            getPasswordField2().setManaged(false);
            getVisiblePasswordField2().setVisible(true);
            getVisiblePasswordField2().setManaged(true);
            showPassword2 = true;
        }
    }
}
