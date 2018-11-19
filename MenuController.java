/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

/**
 *
 * @author LAM
 */
public class MenuController {
    
    @FXML ToggleGroup toggleGroupJeu;
    @FXML RadioMenuItem hackenbush;
    @FXML RadioMenuItem nim;
    
    @FXML ToggleGroup toggleGroupMode;
    @FXML RadioMenuItem normal;
    @FXML RadioMenuItem misere;
    
    @FXML ToggleGroup toggleGroupJoueur;
    @FXML RadioMenuItem jrvsjr;
    @FXML RadioMenuItem jrvsia;
    @FXML RadioMenuItem iavsia;
    
    Choix choix = new Choix();
    
    @FXML Pane conteneur;
    
    @FXML
    public void initialize() throws IOException {
        chargerJeu();
        //desactiver ia vs ia si on est pas en mode NIM
        iavsia.disableProperty().bind(nim.selectedProperty().not());
        
        toggleGroupJeu.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (toggleGroupJeu.getSelectedToggle()== hackenbush) {
                choix.jeu = Choix.JEU.HACKENBUSH;
                if(choix.joueur == Choix.JOUEUR.IAVSIA){ //Pas de ia vs ia en hachkenbush
                    choix.joueur = Choix.JOUEUR.JRVSJR;
                    iavsia.setSelected(false);jrvsjr.setSelected(true);
                }
            }else{
                choix.jeu = Choix.JEU.NIM;
            }
            try {
                chargerJeu();
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
   
        toggleGroupMode.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (toggleGroupMode.getSelectedToggle()== normal) {
                choix.mode = Choix.MODE.NORMAL;
            }else{
                choix.mode = Choix.MODE.MISERE;
            }
        });
        
        toggleGroupJoueur.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            if (toggleGroupJoueur.getSelectedToggle()== jrvsjr) {
                choix.joueur = Choix.JOUEUR.JRVSJR;
            }else if(toggleGroupJoueur.getSelectedToggle() == jrvsia){
                choix.joueur = Choix.JOUEUR.JRVSIA;
            }else{
                choix.joueur = Choix.JOUEUR.IAVSIA;
            }
        });
        

    }
    
    void chargerJeu() throws IOException{
        conteneur.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader();
        if(choix.jeu == Choix.JEU.HACKENBUSH){
            Pane p = fxmlLoader.load(getClass().getResource("hackenbushEdition.fxml").openStream());
            HackenbushEditionController controller = (HackenbushEditionController) fxmlLoader.getController();
            controller.setChoix(choix);
            controller.setConteneur(conteneur);
            conteneur.getChildren().add(p);
        }else{
            Pane p = fxmlLoader.load(getClass().getResource("nim.fxml").openStream());
            NimController controller = (NimController) fxmlLoader.getController();
            controller.setChoix(choix);
            conteneur.getChildren().add(p);
        } 
    }
        
    @FXML
    public void quitter(){
        Platform.exit();
    }
}
