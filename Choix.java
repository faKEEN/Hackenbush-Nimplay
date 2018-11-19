/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LAM
 */
public class Choix {
    
    public JEU jeu;
    public MODE mode;
    public JOUEUR joueur;
    
    public Choix(){
        jeu = JEU.HACKENBUSH;
        mode = MODE.NORMAL;
        joueur = JOUEUR.JRVSJR;
    }
    
    public enum JEU{
        HACKENBUSH, NIM;
    }
    public enum MODE{
        NORMAL, MISERE;
    }
    public enum JOUEUR{
        JRVSJR, JRVSIA, IAVSIA;
    }
 
    public String toString(){
        return "Jeu: "+jeu+ " Mode: "+mode+" Joueur: "+joueur;
    }
}
