import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;

import java.util.ArrayList;

public class NimPlay {
    @FXML
    Pane panePlay;

    private Line sol;
    protected int aretes, tiges;
    protected double ratio;
    private ArrayList<Path> paths = new ArrayList<>();
    ArrayList<Circle[]> sommets = new ArrayList<>();
    private ArrayList<ArrayList<Path>> adjacents = new ArrayList<>();

    private static final int WIDTHF = 800;
    private static final int HEIGHTF = 415;
    private static final int HAUTEURSOL = 40;


    @FXML public void initialize(){
        int myY = HEIGHTF - HAUTEURSOL;
        int myX = 5;
        for(int i = 0; i < tiges; i++) {
            for(int j = 0; j < aretes; j++) {
                MoveTo moveTo = new MoveTo();
                Path path = new Path();
                moveTo.setX(myX+ratio);
                moveTo.setY(myY);
                LineTo lineTo = new LineTo();
                lineTo.setX(myX+ratio);
                lineTo.setY(myY);
            }
        }

    }

    public void init(int aretes, int tiges) {
        this.aretes = aretes;
        this.tiges = tiges;
        ratio = (panePlay.getWidth()/tiges)-10;
        initSol();
    }


    private void initSol(){
        sol = new Line();
        sol.setStartX(0.0f);
        sol.setStartY(HEIGHTF - HAUTEURSOL);
        sol.setEndX(WIDTHF);
        sol.setEndY(HEIGHTF - HAUTEURSOL);
        panePlay.getChildren().add(sol);
    }
}
