/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;


/**
 *
 * @author LAM
 */
public class NimController {


    private static final int WIDTHF = 800;
    private static final int HEIGHTF = 415;
    private static final int HAUTEURSOL = 40;
    private static final int LONGUEURMAX = 10;

    private Nim.Move move;
    private Line sol;
    private Nim nim;
    private int[] sizePath;
    private Choix choix;
    private ArrayList<ArrayList<Circle>> circles;
    private ArrayList<ArrayList<Path>> paths;
    private ArrayList<TextField> champs;
    private ArrayList<Integer> tiges;
    private boolean jouable;
    private static final String  joueur1 = "joueur 1" , joueur2 = "joueur 2", joueurIA = "robot";
    private String tourJoueur;
    @FXML TextField field1;@FXML TextField field2;@FXML TextField field3;@FXML TextField field4;@FXML TextField field5;
    @FXML TextField field6;@FXML TextField field7;@FXML TextField field8;@FXML TextField field9;@FXML TextField field10;
    @FXML Button jouer;
    @FXML Pane pane;
    @FXML ToolBar toolbar;
    @FXML Text text;


    @FXML public void initialize(){
        initSol();
        initChamps();
        jouer.setOnMouseClicked((MouseEvent me) -> {
            if(jouable) {
                jouer.setDisable(true);
                playGame();

            }

        });

    }

    public void updateHeap(){
        for (int i = 0; i < paths.size(); i++) {
            sizePath[i] = paths.get(i).size();
        }
    }


    public void playGame() {
        toolbar.getItems().removeAll(field1,field2,field3,field4,field5,field6,field7,field8,field9,field10);
        text.setText("Tour du "+tourJoueur);
        sizePath = new int[paths.size()];
        updateHeap();
        if (choix.mode == Choix.MODE.MISERE) {
            nim = new Nim(false);
        }
        else {
            nim = new Nim(true);
        }

    }

    public void setTourJoueur(String joueur) {
        tourJoueur = joueur;
        text.setText("Tour du "+tourJoueur);
    }

    private void genererJeu(ArrayList<Integer> tiges){
        projetjavafx.GenerationTiges gt = new projetjavafx.GenerationTiges(tiges, sol, pane);
        circles = gt.getCircles();
        paths = gt.getPaths();

        for(ArrayList<Circle> cir: circles){
            for(Circle c: cir){
                c.onMouseClickedProperty().bind(Bindings.when(jouer.disableProperty()).then(supressionTige).otherwise((MouseEvent e) -> {}));
            }
        }
    }

    public void deleteTiges(Nim.Move move_) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Path> pathDelete = new ArrayList<>();
        ArrayList<Circle> circleDelete = new ArrayList<>();
        ArrayList<Circle> tige = null;
        Circle c = circles.get(move_.getIndex()).get(move_.getSize());
        for(int i = 0; i<circles.size(); i++){
            if(circles.get(i).contains(c))
                tige = circles.get(i);
        }
        int j = tige.indexOf(c);
        for(int k = j; k< tige.size(); k++)
            circleDelete.add(tige.get(k));
        for(int k = j; k<paths.get(circles.indexOf(tige)).size(); k++)
            pathDelete.add(paths.get(circles.indexOf(tige)).get(k));

        pane.getChildren().removeAll(circleDelete);
        pane.getChildren().removeAll(pathDelete);
        circles.remove(circleDelete);
        paths.remove(pathDelete);
        updateHeap();

        if (checkWinner() && choix.mode == Choix.MODE.MISERE) {
            setTourJoueur(joueur1);
            revealWinner(tourJoueur);
        }
        else if (checkWinner() && choix.mode == Choix.MODE.NORMAL){
            revealWinner(tourJoueur);
        }
        else {
            setTourJoueur(joueur1);
        }
    }



    private EventHandler<MouseEvent> supressionTige = (MouseEvent e) -> {
        Circle c = (Circle) e.getTarget();
        ArrayList<Path> pathDelete = new ArrayList<>();
        ArrayList<Circle> circleDelete = new ArrayList<>();
        ArrayList<Circle> tige = null;
        for(int i = 0; i<circles.size(); i++){
            if(circles.get(i).contains(c))
                tige = circles.get(i);
        }
        int j = tige.indexOf(c);
        for(int k = j; k< tige.size(); k++)
            circleDelete.add(tige.get(k));
        for(int k = j; k<paths.get(circles.indexOf(tige)).size(); k++)
            pathDelete.add(paths.get(circles.indexOf(tige)).get(k));

        pane.getChildren().removeAll(circleDelete);
        pane.getChildren().removeAll(pathDelete);
        circles.remove(circleDelete);
        paths.remove(pathDelete);
        updateHeap();
        Nim.applyMove(nim.nextMove(sizePath),sizePath);
        if(choix.joueur == Choix.JOUEUR.JRVSIA) {
            if (checkWinner() && choix.mode == Choix.MODE.MISERE) {
                setTourJoueur(joueurIA);
                revealWinner(tourJoueur);
            }
            if (checkWinner() && choix.mode == Choix.MODE.NORMAL) {
                revealWinner(tourJoueur);
            }
            if (tourJoueur.equals(joueur1) && choix.mode == Choix.MODE.NORMAL) {
                setTourJoueur(joueurIA);
                move = nim.nextMove(sizePath);
                Nim.applyMove(move,sizePath);
                deleteTiges(move);
            }
            if (tourJoueur.equals(joueur1) && choix.mode == Choix.MODE.MISERE) {
                setTourJoueur(joueurIA);
                deleteTiges(Nim.winningMoveMisere(sizePath));
            }
        }
        else if (choix.joueur == Choix.JOUEUR.JRVSJR) {
            if (checkWinner() && choix.mode == Choix.MODE.MISERE) {
                if (tourJoueur.equals(joueur1)) {
                    setTourJoueur(joueur2);
                } else {
                    setTourJoueur(joueur1);
                }
                revealWinner(tourJoueur);
            }

            if (checkWinner() && choix.mode == Choix.MODE.NORMAL) {
                revealWinner(tourJoueur);
            }
            else if (tourJoueur.equals(joueur1)) {
                setTourJoueur(joueur2);
            }
            else {
                setTourJoueur(joueur1);
            }
        }
    };

    private void revealWinner(String tourJoueur) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Jeu fini !");
        alert.setHeaderText("On a un gagnant !");
        alert.setContentText("Le "+tourJoueur+" a gagné !");

        alert.showAndWait();
    }

    void setChoix(Choix c) {
        choix = c;
    }

    private void initSol(){
        sol = new Line();
        sol.setStartX(0.0f);
        sol.setStartY(HEIGHTF - HAUTEURSOL);
        sol.setEndX(WIDTHF);
        sol.setEndY(HEIGHTF - HAUTEURSOL);
        pane.getChildren().add(sol);
    }



    private void initChamps(){
        tourJoueur = joueur1;
        champs = new ArrayList<>();
        champs.add(field1);champs.add(field2);champs.add(field3);champs.add(field4);champs.add(field5);
        champs.add(field6);champs.add(field7);champs.add(field8);champs.add(field9);champs.add(field10);
        for(TextField field: champs){
            field.disableProperty().bind(jouer.disableProperty());
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                tiges = new ArrayList<>();
                jouable = false;
                for(TextField value: champs){
                    try{
                        int val = Integer.parseInt(value.getText().trim());
                        if(val>0 && val<11){
                            tiges.add(val);
                            jouable = true;
                            jouer.setDisable(false);
                        }
                        else throw new Exception();
                    }catch(Exception e){
                        tiges.add(0);
                    }
                }if(jouable){
                    pane.getChildren().clear();
                    initSol();
                    genererJeu(tiges);
                }

            });
        }
    }

    public boolean checkWinner() {
        return Nim.isFinished(sizePath);
    }
}
