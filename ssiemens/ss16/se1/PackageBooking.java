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


    /**
     * Der Konstruktor frisst ein Vararg von Bookings. Diese können somit ssiemens.ss16.se1.SimpleBooking's oder ssiemens.ss16.se1.PackageBooking's sein, da beide
     * Klassen von dem Interface ssiemens.ss16.se1.Booking ableiten.
     * @param bookings Liste von SimpleBokkings oder/und ssiemens.ss16.se1.PackageBooking's.
     */
    public PackageBooking(Booking... bookings) {
        this.bookings = bookings;
        this.isBooked = false;
    }

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

    }

    @Override
    public boolean isBooked() {
        return false;
    }

    /**
     * Die toString()-Methode gibt am Anfang den Preis des Pakets und anschließend den Preis aller enthaltenen Einzel-
     * Buchungen bzw. Pakete aus. (PS: Die Ausgabe mag nicht wirklich schön sein, aber erfüllt sein Zweck)
     * @return Den Gesamt-Paketpreis sowie alle enthaltenen Einzelpakete inkl. dazugehörigen Buchungsstatus.
     */
    @Override
    public String toString() {
        String result = "Package price: " + calcPrice() + ". Package booked: " + isBooked();
        result += "The package contains following bookings:";
        for (Booking booking : bookings) {
            result += booking.toString();
        }
        return result;
    }
}
