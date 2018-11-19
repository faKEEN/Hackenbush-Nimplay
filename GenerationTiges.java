/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetjavafx;

import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

/**
 *
 * @author LAM
 */
public class GenerationTiges {
    
    private static final int HAUTEUR = 30;
    private static final int EPAISSEUR = 2;
    private static final int RADIUS = 5;
    private static final int STROKEWIDTH = 3;
    private static final int STARTX = 140;
    private static final int ECARTX = 54;
    private static final int DIMINUTION = 2;
    
    private ArrayList<ArrayList<Path>> paths;
    private ArrayList<ArrayList<Circle>> cercles;
    
    public GenerationTiges(ArrayList<Integer> longueurs, Line sol, Pane pane){
        Circle lastCircle = null;
        paths = new ArrayList<>();
        cercles = new ArrayList<>();
        for(int i=0; i<longueurs.size(); i++){
            paths.add(new ArrayList<>());
            cercles.add(new ArrayList<>());
            for(int j=0; j<longueurs.get(i); j++){
                MoveTo move = new MoveTo();
                Path p = new Path();
                p.getElements().add(move);
                p.setStroke(Color.GREEN);
                p.setStrokeWidth(EPAISSEUR);
                
                QuadCurveTo quadCurve = new QuadCurveTo();
                Circle previous;
                if(lastCircle != null){
                    move.xProperty().bind(lastCircle.centerXProperty());
                    move.yProperty().bind(lastCircle.centerYProperty().subtract(RADIUS));
                    previous = lastCircle;
                }else{
                    Circle c1 = new Circle(STARTX+i*ECARTX, sol.getStartY(), RADIUS);
                    setCircleProperty(c1);
                    move.xProperty().bind(c1.centerXProperty());
                    move.yProperty().bind(c1.centerYProperty().subtract(RADIUS));
                    pane.getChildren().add(c1);
                    cercles.get(i).add(c1);
                    previous = c1;
                }
                
                double random =  Math.random()*3 + Math.abs(j-longueurs.get(i)/2);
                if(j<longueurs.get(i)/2)
                    random = -random;
                
                Circle c2 = new Circle();
                c2.centerXProperty().bind(previous.centerXProperty().add(random/2));
                c2.centerYProperty().bind(previous.centerYProperty().subtract((HAUTEUR-DIMINUTION*j)/2));
                quadCurve.controlXProperty().bind(c2.centerXProperty());
                quadCurve.controlYProperty().bind(c2.centerYProperty());

                Circle c3 = new Circle(RADIUS);
                setCircleProperty(c3);
                c3.centerXProperty().bind(c2.centerXProperty().add(random));
                c3.centerYProperty().bind(c2.centerYProperty().subtract((HAUTEUR-DIMINUTION*j)));
                quadCurve.xProperty().bind(c3.centerXProperty().add(-random/2));
                quadCurve.yProperty().bind(c3.centerYProperty().add(RADIUS));
                
                pane.getChildren().add(c3);
                cercles.get(i).add(c3);
                paths.get(i).add(p);
                p.getElements().add(quadCurve);
                lastCircle = c3;
            }
           lastCircle = null;
           pane.getChildren().addAll(paths.get(i)); 
        }
    }
    
 
    private void setCircleProperty(Circle c){
        c.setFill(Color.WHITE);
        c.setStroke(Color.BLACK);
        c.setStrokeWidth(STROKEWIDTH);
    }
    
    public ArrayList<ArrayList<Path>> getPaths(){
        return this.paths;
    }
    
    public ArrayList<ArrayList<Circle>> getCircles(){
        return this.cercles;
    }
}
