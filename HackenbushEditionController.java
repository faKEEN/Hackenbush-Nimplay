/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableObjectValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 *
 * @author LAM
 */
public class HackenbushEditionController {
    
    Choix choix;
    Pane conteneur;
    
    @FXML Pane pane;
    @FXML ToolBar toolbar;
    @FXML ComboBox colorPick;
    @FXML ToggleGroup toggle;
    @FXML ToggleButton ajouterButton;
    @FXML ToggleButton deplacerButton;
    @FXML ToggleButton colorierButton;
    @FXML ToggleButton supprimerButton;
    @FXML ToggleButton afficherButton;
    @FXML Button jouerButton;
    
    /*nombre de sommets  déjà dans PathElement*/

    private int nb = 0;
    private ArrayList<Path> paths = new ArrayList<>();
    ArrayList<Circle[]> sommets = new ArrayList<>();
    private ArrayList<ArrayList<Path>> adjacents = new ArrayList<>();
    
    private static final double MINSTROKE = 2.0;
    private static final double MAXSTROKE = 5.0;
    private static final double MINRADIUS = 4.0;
    private static final double MAXRADIUS = 6.0;
    
    private static final Color COLORCONNECTABLE = Color.GREY;
    private static final int WIDTHF = 800;
    private static final int HEIGHTF = 415;
    private static final int HAUTEURSOL = 40;
    private Line sol;
    
    private double oldX, oldY;

    
    
/* définir Hadler pour la construction de CubicCurveTo */
    private EventHandler<MouseEvent> cubicHandler = (MouseEvent e) -> {
        if(e.isControlDown()){
            dupliquer(e);
            return;
        }
        if(nb == 0)
            sommets.add(new Circle[4]);
        sommets.get(sommets.size()-1)[nb] = createVertex(e);
        pane.getChildren().add(sommets.get(sommets.size()-1)[nb]);
        if (nb == 0) {
            /* premier sommets dans Path */
            initFirstVertexInPath();
            adjacents.add(new ArrayList<Path>());
            sommets.get(sommets.size()-1)[nb].setFill(COLORCONNECTABLE);
            attachEdgeInCreation(e.getTarget(), sommets.get(sommets.size()-1)[nb]);
            nb = 1;
            return;
        }

        switch (nb) {
            case 1:
            case 2:
                sommets.get(sommets.size()-1)[nb].fillProperty().bind(paths.get(paths.size()-1).strokeProperty());
                sommets.get(sommets.size()-1)[nb].visibleProperty().bind(
                        Bindings.when(afficherButton.selectedProperty()).then(false).otherwise(true) );
                nb++;
                return;
            case 3:
                paths.get(paths.size()-1).setStroke(Paint.valueOf(colorPick.getSelectionModel().getSelectedItem().toString())); 
                paths.get(paths.size()-1).getElements().add(getCubicCurveTo());
                sommets.get(sommets.size()-1)[nb].setFill(COLORCONNECTABLE);
                attachEdgeInCreation(e.getTarget(), sommets.get(sommets.size()-1)[nb]);
                nb = 0;
        }
    };
    
    private CubicCurveTo getCubicCurveTo(){
        CubicCurveTo cubicCurve = new CubicCurveTo();
        cubicCurve.controlX1Property().bind(sommets.get(sommets.size()-1)[1].centerXProperty());
        cubicCurve.controlY1Property().bind(sommets.get(sommets.size()-1)[1].centerYProperty());
        cubicCurve.controlX2Property().bind(sommets.get(sommets.size()-1)[2].centerXProperty());
        cubicCurve.controlY2Property().bind(sommets.get(sommets.size()-1)[2].centerYProperty());
        cubicCurve.xProperty().bind(sommets.get(sommets.size()-1)[3].centerXProperty());
        cubicCurve.yProperty().bind(sommets.get(sommets.size()-1)[3].centerYProperty());
        return cubicCurve;
    }
    
    private void dupliquer(MouseEvent e){
        int index = -1;
        for(Circle[] circles: sommets){
            for(Circle circle: circles){
                if(circle == e.getTarget()){
                    index = sommets.indexOf(circles);
                }
            }
        }
        if(index != -1){
            sommets.add(new Circle[4]);
            adjacents.add(new ArrayList<Path>());
            Path copie = new Path();
            paths.add(copie);
            copieCircles(index, copie.strokeProperty());
            MoveTo move = new MoveTo();
            copie.mouseTransparentProperty().bind(paths.get(index).mouseTransparentProperty());
            copie.strokeWidthProperty().bind(paths.get(index).strokeWidthProperty());
            copie.onMouseEnteredProperty().bind(paths.get(index).onMouseEnteredProperty());
            move.xProperty().bind(sommets.get(sommets.size()-1)[0].centerXProperty());
            move.yProperty().bind(sommets.get(sommets.size()-1)[0].centerYProperty());
            copie.getElements().add(move);
            copie.setStroke(Paint.valueOf(colorPick.getSelectionModel().getSelectedItem().toString())); 
            copie.getElements().add(getCubicCurveTo());
            pane.getChildren().add(copie);
        }


    }
    
    private void copieCircles(int indexCircles, ObservableObjectValue<Paint> couleurPropertie){
        for(int i=0; i<4; i++){
            Circle circleCopy = new Circle();
            Circle circleOriginal = sommets.get(indexCircles)[i];
            circleCopy.setCenterX(circleOriginal.getCenterX()+15);
            circleCopy.setCenterY(circleOriginal.getCenterY());
            circleCopy.radiusProperty().bind(circleOriginal.radiusProperty());
            circleCopy.visibleProperty().bind(circleOriginal.visibleProperty());
            if(i==0||i==3){
                circleCopy.setFill(COLORCONNECTABLE);
            }else{
                circleCopy.fillProperty().bind(couleurPropertie);
            }         
            sommets.get(sommets.size()-1)[i] = circleCopy;
            pane.getChildren().add(circleCopy);
        }
    }
    
    private EventHandler<MouseEvent> deleteHandler = (MouseEvent e) ->{
        if(paths.size()>0){
            for (Path p: paths){
                if(e.getTarget() == p){
                    supprimerDessin(paths.indexOf(p));
                    return;
                }
            }
            for (Circle[] circles: sommets){
                for(Circle circle: circles){
                    if(e.getTarget() == circle){
                        supprimerDessin(sommets.indexOf(circles));
                        return;
                    }       
                }
            }
        }
    };
    
    private EventHandler<MouseEvent> colorierHandler = (MouseEvent e) -> {
      for(Path p: paths){
          if(e.getTarget() == p){
              p.setStroke(Paint.valueOf(colorPick.getSelectionModel().getSelectedItem().toString()));
              if(e.isControlDown())
                spreadColor(p);
              return;
          }
      }
      for(Circle[] circles: sommets){
          for(Circle circle: circles){
              if(e.getTarget() == circle){
                  int index = sommets.indexOf(circles);
                  paths.get(index).setStroke(Paint.valueOf(colorPick.getSelectionModel().getSelectedItem().toString()));
                  if(e.isControlDown())
                    spreadColor(paths.get(index));
                  return;
              }
          }
      }
    };
    
    private void spreadColor(Path p){
        int indexP = paths.indexOf(p);
        for(Path adj: adjacents.get(indexP)){ 
            if(adj.getStroke() != paths.get(indexP).getStroke()){
                adj.setStroke(paths.get(indexP).getStroke());
                spreadColor(adj);
            }
        }
    }
    
    private EventHandler<MouseEvent> moveHandler = (MouseEvent e) -> {
        if(e.getTarget().getClass().getName().contains("Circle") && !e.isControlDown()){
            moveCircle(e);
        }
        else if(e.getTarget().getClass().getName().contains("Circle") && e.isControlDown()){
            moveSelection(e);
        }
    };

    private void moveCircle(MouseEvent e){
        for(Circle[] circles: sommets){
            for(Circle circle : circles){
                if(circle == e.getTarget()){
                    Circle c = (Circle)e.getTarget();
                    c.setCenterX(getXPositionMouse(e));
                    c.setCenterY(getYPositionMouse(e));
                    attachEdgeInMove(c);
                    if(e.getY() > HEIGHTF-HAUTEURSOL && c.getFill() == COLORCONNECTABLE){
                        c.setCenterY(sol.getEndY());
                    }
                }
            }
        }
    }
    
    //on ne bouge pas le dernier sommet d'un path qui boucle sur lui meme
    private void moveSelection(MouseEvent e){
        double x = getXPositionMouse(e);
        double y = getYPositionMouse(e);
        int iteration = 0;
        Circle[] cercles = null;Circle cercle = null;
        ArrayList<Boolean> boucles = boucles();
        for(Circle[] circles: sommets){
            for(Circle cible : circles){
                if(cible == (Circle) e.getTarget()){
                    cercle = cible; cercles = circles;
                }
            }
        }
        if(boucles.get(sommets.indexOf(cercles)))
            iteration = 3;
        else iteration = 4;
        oldX = cercle.getCenterX(); oldY = cercle.getCenterY();
        boolean bloquer = false;
        double deplacementX = 0, deplacementY = 0;
        for(int i=0; i<iteration; i++){
            deplacementX = cercles[i].getCenterX() + x - oldX;
            deplacementY = cercles[i].getCenterY() + y - oldY; 
            if(deplacementX < 0 || deplacementX > WIDTHF-5)
                bloquer = true; 
            if(deplacementY < 0 || deplacementY > HEIGHTF)
                bloquer = true;
            if(cercles[i].getCenterY() > HEIGHTF-HAUTEURSOL && cercles[i].getFill() == COLORCONNECTABLE)
                bloquer = true;
        }
        if(!bloquer){   
            for(int j=0; j<iteration; j++){
                deplacementX = cercles[j].getCenterX() + x - oldX;
                deplacementY = cercles[j].getCenterY() + y - oldY; 
                attachEdgeInMove(cercles[j]);
                cercles[j].setCenterX(deplacementX);
                cercles[j].setCenterY(deplacementY);
            }
        }
        oldX = x;
        oldY = y;
    }
    
    private void attachEdgeInMove(Circle c){
        for(Circle[] circles: sommets){
            for(Circle circle : circles){
                if(circle != c && estProche(circle, c)){
                    c.setCenterX(circle.getCenterX());
                    c.centerXProperty().bindBidirectional(circle.centerXProperty());
                    c.setCenterY(circle.getCenterY());
                    c.centerYProperty().bindBidirectional(circle.centerYProperty());
                    ajoutAdjacents(c, circle);
                    for(Circle[] adjacentsDeCircle: sommets){
                        for(Circle adjacentDeCircle: adjacentsDeCircle){
                            if(estProche(adjacentDeCircle, circle)){
                                ajoutAdjacents(adjacentDeCircle, circle);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void attachEdgeInCreation(EventTarget e, Circle sommet){
        if(e.getClass().getName().contains("Circle")){
            for( Circle[] circles: sommets){
                for (Circle circle: circles){
                    if((Circle)e == circle && circle.getFill() == COLORCONNECTABLE){
                        sommet.setCenterX(circle.getCenterX());
                        sommet.setCenterY(circle.getCenterY());
                        circle.centerXProperty().bindBidirectional(sommets.get(sommets.size()-1)[nb].centerXProperty());
                        circle.centerYProperty().bindBidirectional(sommets.get(sommets.size()-1)[nb].centerYProperty());
                        ajoutAdjacents(circle, sommet);
                    }else if(circle != null && estProche(circle, sommet)){
                        ajoutAdjacents(circle, sommet);
                    }     
                }
            }
        }
        if(sommet.getCenterY() > HEIGHTF-HAUTEURSOL){
            sommet.setCenterY(sol.getEndY());
        }
    }
    
    private boolean estProche(Circle c1, Circle c2){
        return Math.sqrt(
            Math.pow((c1.getCenterX()-c2.getCenterX()),2)+
            Math.pow((c1.getCenterY()-c2.getCenterY()),2)) < MAXRADIUS &&
            c1.getFill() == COLORCONNECTABLE && 
            c2.getFill() == COLORCONNECTABLE;
    }


    @FXML
    public void initialize() {
        initSol();
        pane.setPrefSize(WIDTHF, HEIGHTF);
        pane.onMouseClickedProperty()
            .bind(Bindings.when(ajouterButton.selectedProperty()).then(cubicHandler)
            .otherwise(Bindings.when(colorierButton.selectedProperty()).then(colorierHandler)
            .otherwise(Bindings.when(supprimerButton.selectedProperty()).then(deleteHandler)
            .otherwise((MouseEvent e) -> {})))
        );
        pane.onMouseDraggedProperty().bind(
            Bindings.when(deplacerButton.selectedProperty()).then(moveHandler)
                    .otherwise((MouseEvent e) -> {}));
        toggle.selectedToggleProperty().addListener((observable, old, neuf) -> {
            if (paths.size()>0 && old == ajouterButton && neuf != ajouterButton && sommets.get(sommets.size()-1)[3] == null) {
                   supprimerDessin(paths.size()-1);
                nb = 0;
            }
        });
        jouerButton.visibleProperty().bind(Bindings.when(afficherButton.selectedProperty()).then(true).otherwise(false));
        jouerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    chargerJeu();
                } catch (IOException ex) {
                    Logger.getLogger(HackenbushEditionController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
   
    private void initSol(){
        sol = new Line();
        sol.setStartX(0.0f);
        sol.setStartY(HEIGHTF - HAUTEURSOL);
        sol.setEndX(WIDTHF);
        sol.setEndY(HEIGHTF - HAUTEURSOL);
        pane.getChildren().add(sol);
    }
    
    void setChoix(Choix c) {
        choix = c;
    }
    
    private void initFirstVertexInPath() {
        MoveTo move = new MoveTo();
        Path p = new Path();
        p.mouseTransparentProperty().bind(
                Bindings.when(supprimerButton.selectedProperty().or(colorierButton.selectedProperty().or(afficherButton.selectedProperty())))
                    .then(false).otherwise(true));
        p.strokeWidthProperty().bind(
            Bindings.when(supprimerButton.selectedProperty().or(afficherButton.selectedProperty()
                .or(colorierButton.selectedProperty())))
                .then(MAXSTROKE).otherwise(MINSTROKE));
        p.onMouseEnteredProperty().bind(Bindings.when(supprimerButton.selectedProperty())
            .then(deleteControle).otherwise((MouseEvent e) -> {}));
        paths.add(p);
        move.xProperty().bind(sommets.get(sommets.size()-1)[0].centerXProperty());
        move.yProperty().bind(sommets.get(sommets.size()-1)[0].centerYProperty());
        paths.get(paths.size()-1).getElements().add(move);
        pane.getChildren().add(paths.get(paths.size()-1));
    }

    private EventHandler<MouseEvent> deleteControle = (MouseEvent e) -> {
        if(e.isControlDown() && e.getTarget().getClass().getName().contains("Path")){
            for(Path p: paths)
                if(p == (Path)e.getTarget()){
                    supprimerDessin(paths.indexOf(p));
                    return;
                }
        }
    };
    
    private Circle createVertex(MouseEvent e) {
        Circle vertex = new Circle();
        vertex.radiusProperty().bind(Bindings.when(ajouterButton.selectedProperty().or(deplacerButton.selectedProperty())).then(MAXRADIUS).otherwise(MINRADIUS));
        vertex.setCenterX(e.getX());
        vertex.setCenterY(e.getY());
        return vertex;
    }
    
    private double getXPositionMouse(MouseEvent e){
        if(e.getX()<0)
            return 0;
        if(e.getX()>WIDTHF)
            return WIDTHF-5;
        return e.getX();
    }
    
    private double getYPositionMouse(MouseEvent e){
        if(e.getY()<0)
            return 0;
        if(e.getY()>HEIGHTF)
            return HEIGHTF;
        return e.getY();
    }
    
    private void supprimerDessin(int index){
        pane.getChildren().remove(paths.get(index));
        Circle[] s = sommets.get(index);
        pane.getChildren().remove(s[0]);
        pane.getChildren().remove(s[1]);
        pane.getChildren().remove(s[2]);
        pane.getChildren().remove(s[3]);
        Path p = paths.remove(index);
        adjacents.remove(index);
        for(ArrayList<Path> adj: adjacents)
            adj.remove(p);
        sommets.remove(index);
    }

    private ArrayList<Boolean> boucles(){
        ArrayList<Boolean> boucles = new ArrayList<>();
        for(Circle[] circles: sommets){
            if(circles[0].getCenterY() == circles[3].getCenterY() && circles[0].getCenterX() == circles[3].getCenterX())
                boucles.add(true);
            else boucles.add(false);
        }
        return boucles;
    }
    
    private void ajoutAdjacents(Circle c1, Circle c2){
        int indexC1 = 0;
        int indexC2 = 0;
        for(Circle[] circles: sommets){
            for(Circle circle: circles){
                if(c1 == circle)
                    indexC1 = sommets.indexOf(circles);
                if(c2 == circle)
                    indexC2 = sommets.indexOf(circles);
            }
        }
        if(indexC1 != indexC2){
            Path p1 = paths.get(indexC1);
            Path p2 = paths.get(indexC2);
            if(!adjacents.get(indexC1).contains(p2))
                adjacents.get(indexC1).add(p2);
            if(!adjacents.get(indexC2).contains(p1))
                adjacents.get(indexC2).add(p1);
        }
    }
    
    void chargerJeu() throws IOException{
        conteneur.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader();
        Pane p = fxmlLoader.load(getClass().getResource("hackenbushPlay.fxml").openStream());
        HackenbushPlayController controller = (HackenbushPlayController) fxmlLoader.getController();
        controller.init(choix, pane, paths, sommets, adjacents, HEIGHTF-HAUTEURSOL);
        conteneur.getChildren().add(p);
    }
    
    public void setConteneur(Pane conteneur){
        this.conteneur = conteneur;
    }
    
    
}
