package ssiemens.ss16.se1;

public abstract class SimpleBooking implements Booking{
    boolean isBooked;
    double price;

    public SimpleBooking(double price) {
        this.price = price;
        this.isBooked = false;
    }

    @Override
    public double calcPrice() {
        // ToDo: Anhand Aufgabenstellung implementieren
        return 0;
    }

    @Override
    public void book() {
        // ToDo: Anhand Aufgabenstellung implementieren
    }

    @Override
    public boolean isBooked() {
        // ToDo: Anhand Aufgabenstellung implementieren
        return false;
    }

    /**
     * Wenn man die toString()-Methode nicht überschreibt, dann wird nur die Speicheradresse des Objekts ausgegeben.
     * Bei der Redefinition der toString()-Methode wird anstelle der Speicheradresse ein String zurückgegeben,
     * der Auskunft über den Preis sowie über den Status der Buchung gibt. Darunter findest Du die auskommentiert
     * die Default-toString()-Methode, welche keine nützlichen Informationen ausgibt.
     * @return
     */
    @Override
    public String toString() {
        return "Price: " + price + ". Is booked: "+ isBooked;
    }

    // Default-toString()-Method, wenn man diese nicht überschreibt: Diese gibt nur die Speicheradresse/Hashcode
    // (Stoff von SE2) zurück und ist nich wirklich nützlich. Daher überschreibt man diese, wie oben beschrieben.

    //@Override
    //public String toString() {
    //    return getClass().getName() + "@" + Integer.toHexString(hashCode());
    //}
}
