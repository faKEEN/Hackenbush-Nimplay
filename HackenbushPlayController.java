/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

/**
 *
 * @author LAM
 */
public class HackenbushPlayController {
    
    Choix choix;
    Pane dessin;
    ArrayList<Path> paths;
    ArrayList<Circle[]> sommets;
    ArrayList<ArrayList<Path>> adjacents;
    ArrayList<Boolean> racines;
    ArrayList<Path> pathsRemoved = new ArrayList<>();
    ArrayList<Integer> rangesPlay = new ArrayList<>();
    double solY;
    Paint jouable = Color.RED;
    
    @FXML Pane pane;
    @FXML Circle circle;
    @FXML Text alerte;
    
    @FXML 
    public void initialize(){
        circle.setFill(jouable);
        pane.setOnMousePressed((MouseEvent me) -> {
            if(me.isPrimaryButtonDown()){
                if(me.getTarget().getClass().getName().contains("Path")){
                    Path p = (Path)me.getTarget();
                    if(p.getStroke() == jouable || p.getStroke() == Color.GREEN ){
                        hidePath(p);
                        clearDessin(false);
                        switchJouable();
                        if(choix.joueur == Choix.JOUEUR.JRVSIA && !estFini())
                            jouerUnCoup();
                    }else if(!estFini()){
                        alerte("PAS POSSIBLE");
                    }

                }
            }else if(me.isSecondaryButtonDown()){
                cancelLastPlay();
                if(choix.joueur == Choix.JOUEUR.JRVSIA)
                    cancelLastPlay();
            }
        });

    }
    
    private void alerte(){
        String s;
        if(choix.mode == Choix.MODE.NORMAL)
            s = "Gagnant: ";
        else s ="Perdant: ";
        if(jouable == Color.RED)
            s+="BLEU";
        else s+= "ROUGE";
        alerte(s);
    }
    
    private void alerte(String s){
        alerte.setText(s);
    }
    
    private boolean estFini(){
        boolean fini = true;
        for(Path p: paths)
            if( !pathsRemoved.contains(p) && (p.getStroke() == Color.GREEN || p.getStroke() == jouable))
                fini = false;
        return fini;
    }
    
    private void switchJouable(){
        if(jouable == Color.RED)
            jouable = Color.BLUE;
        else jouable = Color.RED;
        circle.setFill(jouable);
        if(estFini())
            alerte();
    }
    
    private void jouerUnCoup(){
        ArrayList<Path> coupsJouable = new ArrayList<>();
        for(Path p: paths)
            if((p.getStroke() == jouable || p.getStroke() == Color.GREEN) && !pathsRemoved.contains(p))
                coupsJouable.add(p);
        int index = (int)(Math.random() * coupsJouable.size());
        hidePath(coupsJouable.get(index));
        clearDessin(false);
        switchJouable();
    }
    
    private void cancelLastPlay(){
        if(rangesPlay.size()>0){
            int rangeLastPlay = rangesPlay.remove(rangesPlay.size()-1);
            for(int i=0; i<rangeLastPlay; i++){
                pathsRemoved.remove(pathsRemoved.size()-1).setVisible(true);
            }
            switchJouable();
            alerte("");
        }
    }
    
    private boolean isConnectToGround(Path p){
        ArrayList<Path> chemins = new ArrayList<>();
        ajoutChemins(chemins, p);
        for(Path path: chemins)
            if(racines.get(paths.indexOf(path)))
                return true;
        return false;
    }
    
    private void ajoutChemins(ArrayList<Path> chemins, Path p){
        if(!chemins.contains(p) && !pathsRemoved.contains(p))
            chemins.add(p);
        for(Path adj: adjacents.get(paths.indexOf(p))){
            if(!chemins.contains(adj) && !pathsRemoved.contains(adj)){
                chemins.add(adj);
                ajoutChemins(chemins, adj);
            } 
        }
    }
    
    private void pathRacines(){
        racines = new ArrayList<>();
        for(Circle[] circles: sommets){
            if(circles[0].getCenterY() >= solY || circles[3].getCenterY() >= solY)
                racines.add(true);
            else racines.add(false);
        }
    }
    
    private void hidePath(Path p){
        p.setVisible(false);
        pathsRemoved.add(p);
    }
    
    private void deletePath(Path p){
        int index = paths.indexOf(p);
        dessin.getChildren().remove(p);
        Circle[] s = sommets.get(index);
        dessin.getChildren().remove(s[0]);
        dessin.getChildren().remove(s[3]);
        paths.remove(p);
        adjacents.remove(index);
        for(ArrayList<Path> adj: adjacents)
            adj.remove(p);
        sommets.remove(index);
        racines.remove(index);
    }
    
    private void clearDessin(boolean permanent){
        int rangeLastPlay = 1;
        boolean clean = false;
        while(!clean){
            for(Path p: paths){
                if(!isConnectToGround(p) && !pathsRemoved.contains(p)){
                    rangeLastPlay++;
                    if(permanent)
                        deletePath(p);
                    else hidePath(p);
                    clean = false;
                    break;
                }
                clean = true;
            }
        }
        if(!permanent)
            rangesPlay.add(rangeLastPlay);
        alerte("");
    }
    
    private void bindVisibility(){
        for(Circle[] circles: sommets){
            Circle c1 = circles[0];
            Circle c2 = circles[3];
            c1.visibleProperty().unbind();
            c2.visibleProperty().unbind();
            c1.visibleProperty().bind(paths.get(sommets.indexOf(circles)).visibleProperty());
            c2.visibleProperty().bind(paths.get(sommets.indexOf(circles)).visibleProperty());
        }
    }
    
    void init(Choix choix, Pane dessin, ArrayList<Path> paths, ArrayList<Circle[]> sommets, ArrayList<ArrayList<Path>> adjacents, int solY) {
        this.choix = choix;
        this.dessin = dessin;
        this.paths = paths;
        this.sommets = sommets;
        this.adjacents = adjacents;
        this.solY = solY;
        this.pathRacines();
        this.pane.getChildren().add(dessin);
        this.clearDessin(true);
        this.bindVisibility();
        if(estFini())
            alerte("INJOUABLE");
    }


}
