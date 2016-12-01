package ssiemens.ss16;

/**
 * Created by Sascha on 10/10/2016.
 */
public class Auto {
    // #######################
    // ### Konstanten      ###  // Konstanten existieren ohne Objekte und sind unveränderlich (final)
    // #######################  // Zugriff in Main: Auto.MAX_GESCHWINDIGKEIT
    static final int MAX_GESCHWINDIGKEIT = 300;      // km/h

    // ###########################
    // ### Statische Variablen ###  // Statische Variablen existieren ohne Objekte, sind aber veränderlich (jede Konstante ist somit auch eine statische Variable - die Umkehrung gilt allerdings nicht)
    // ###########################  // Zugriff in Main: Auto.marke
    static String marke = "BMW";

    // #######################      // Objektvariablen sind nur auf Objekte der Klasse möglich
    // ### Objektvariablen ###      // Beispiel: Auto dreierBMW = new Auto(1,100,5);
    // #######################      // int ps = dreierBMW.ps     Kein Zugriff über den Klassennamen möglich, nur über Objekte
    int tueren;
    int ps;
    int sitze;

    // #######################
    // ### Konstruktoren   ###      // Konstruktoren heißen genauso wie die Klasse und Initialisieren Objekte der Klasse
    // #######################      // Ohne Angabe eines Konstruktors exisitiert ein nicht sichtbarer "Default"-Konstruktor ohne Parameter

    // Konstruktor mit 3 Parametern
    public Auto(int tueren, int ps, int sitze) {
        this.tueren = tueren;
        this.ps = ps;
        this.sitze = sitze;
    }

    // Konstruktor mit 2 Parametern
    public Auto(int tueren, int sitze) {
        this.tueren = tueren;
        this.sitze = sitze;
        this.ps = 100;
    }

    // Default Konstruktor (nur nötig, wenn andere Konstruktoren exisitieren, ansonsten ist dieser unsichtbar vorhanden)
    public Auto() {
    }


    // #######################
    // ### Methoden        ###  // Methoden können auf Objekte angewendet werden.
    // #######################  // Beispiel von oben: dreierBMW.pimpCar(100); setzt den PS-Wert auf 100
    void pimpCar(int ps){
        this.ps = ps;
    }

    // ##########################
    // ### Statische Methoden ###   Statische methoden können aufgerufen werden ohne dass ein Objekt exisitiert
    // ##########################   Aufruf in Main: Auto.writeHelloWorld();
    static void writeHelloWorld(){
        System.out.println("Hello World");
    }
}
