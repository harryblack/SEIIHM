package ssiemens.ss16.se1;

/**
 * Created by Sascha on 12/01/2017.
 */
public abstract class PackageBooking implements Booking {
    /**
     * Da man nicht weiss ob SimpleBookings oder Pakete angegeben werden, muss hier der allgemeine Interface-Typ
     * angegeben werden. Gleiches gilt auch für den Konstruktor sowie in den unten genannten for-each Schleifen.
     */
    private Booking[] bookings;
    private boolean isBooked;

    @Override
    public double calcPrice() {
        int result = 0;
        for (Booking booking : bookings) {
            result += booking.calcPrice();
        }
        return result;
    }

    @Override
    public void book() {
        isBooked = true;
    }

    @Override
    public boolean isBooked() {
        return isBooked;
    }

    abstract Booking[] components();

    abstract Booking getComponent(int index);

    abstract void addComponent(Booking bc);

    abstract Booking removeComponent(int index);

    /**
     * Die toString()-Methode gibt am Anfang den Preis des Pakets und anschließend den Preis aller enthaltenen Einzel-
     * Buchungen bzw. Pakete aus. (PS: Die Ausgabe mag nicht wirklich schön sein, aber erfüllt sein Zweck)
     * @return Den Gesamt-Paketpreis sowie alle enthaltenen Einzelpakete inkl. dazugehörigen Buchungsstatus.
     */
    @Override
    public String toString() {
        if(bookings == null) return "Package is empty!";
        String result = "Package price: " + calcPrice() + ". Package booked: " + isBooked();
        result += "The package contains following bookings:";
        for (Booking booking : bookings) {
            result += booking.toString();
        }
        return result;
    }
}
