package no.oslomet.cs.algdat.Oblig2;


////////////////// class DobbeltLenketListe //////////////////////////////


import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;


public class DobbeltLenketListe<T> implements Liste<T> {

    /**
     * Node class
     *
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {
        hode = hale = null;
        antall = 0;
        endringer = 0;
    }

    public DobbeltLenketListe(T[] a) {
        // Kaster unntak for null tabell
        if(a == null){ throw new NullPointerException("Tabell a er null!"); }

        a = fjernullVerdier(a);
        if(a.length == 0) { return; }

        Node<T> aktuell = new Node<>(a[0]);
        antall++;
        hode = aktuell;

        for (int i = 1; i < a.length; i++) {
            Node<T> neste = new Node<>(a[i]);
            antall++;
            aktuell.neste = neste;
            neste.forrige = aktuell;
            aktuell = neste;
        }
        hale = aktuell;

    }
    private T[] fjernullVerdier(T[] a){
        int antallNullverdier = 0;

        for(T verdi : a){
            if(verdi == null){
                antallNullverdier++;
            }
        }

        if(antallNullverdier == 0){ return a; }

        T[] b = (T[]) new Object[a.length - antallNullverdier];

        int j = 0;


        for(int i = 0; i < a.length; i++) {
            if(a[i] != null){
                b[j] = a[i];
                j++;
            }
        }

        return b;

    }

    public Liste<T> subliste(int fra, int til) {
        fratilKontroll(antall, fra, til);
        Node<T> aktuell = hode;
        for(int i = 0; i < fra; i++) {
            aktuell = aktuell.neste;
        }
        T[] sublisteInput = (T[]) new Object[til-fra];

        int indeks = 0;

        for(int i = fra; i < til; i++) {
            sublisteInput[indeks] = aktuell.verdi;
            aktuell = aktuell.neste;
            indeks++;
        }
        DobbeltLenketListe<T> subliste = new DobbeltLenketListe<>(sublisteInput);
        return subliste;
    }
    //Hjelpemetode
    private void fratilKontroll(int antall, int fra, int til) {
        if (fra < 0) {                                // fra er negativ
            throw new IndexOutOfBoundsException("fra(" + fra + ") er negativ!");
        }
        if (til > antall) {                        // til er utenfor tabellen
            throw new IndexOutOfBoundsException("til(" + til + ") > antall(" + antall + ")");
        }
        if (fra > til) {                               // fra er større enn til
            throw new IllegalArgumentException("fra(" + fra + ") > til(" + til + ") - illegalt intervall!");
        }
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
       if (antall>0){
           return false;
       }
       return true;
    }

    @Override
    public boolean leggInn(T verdi) {
        verdi = Objects.requireNonNull(verdi, "Null-verdier er ikke tillatt");


        if(antall > 0){
            Node<T> ny = new Node<>(verdi);
            hale.neste = ny;
            ny.forrige = hale;
            hale = ny;

        } else {
            Node<T> ny = new Node<>(verdi);
            hode = ny;
            hale = ny;
        }

        antall++;
        endringer++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean inneholder(T verdi) {
        if (indeksTil(verdi)==-1){
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public T hent(int indeks) {
        indeksKontroll(indeks,false);
        Node<T> returNorde=finnNode(indeks);
        return returNorde.verdi;
    }


    // Hjelpemetode
    private Node<T> finnNode(int indeks) {
        Node<T> returNode;

        if(indeks < antall/2) {
            returNode = hode;
            int i = 0;

            while (i < indeks) {
                returNode = returNode.neste;
                i++;
            }
        } else {
            returNode = hale;
            int i = antall-1;

            while (i > indeks) {
                returNode = returNode.forrige;
                i--;
            }
        }

        return returNode;
    }

    @Override
    public int indeksTil(T verdi) {
        if (antall==0 || verdi==null){
            return -1;
        }
        Node<T> current=hode;
        for (int i=0; i<antall; i++){
            if (current.verdi.equals(verdi)){
                return i;
            }
            current=current.neste;
        }
        return -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        //Om nyverdi er null kastes et unntak
        if(nyverdi == null) { throw new NullPointerException("Nyverdi kan ikke være null"); }
        indeksKontroll(indeks, false);
        Node<T> node = finnNode(indeks);
        T gammelVerdi = node.verdi;
        node.verdi = nyverdi;
        endringer++;
        return gammelVerdi;
    }

    @Override
    public boolean fjern(T verdi) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T fjern(int indeks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void nullstill() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder utskrift = new StringBuilder("[");
        Node<T> current=hode;
        while (current!=null){
            if (current==hale){
                utskrift.append(current.verdi);
            }
            else {
                utskrift.append(current.verdi+", ");
            }
            current=current.neste;
        }
        utskrift.append("]");
        return utskrift.toString();
    }

    public String omvendtString() {
        StringBuilder print=new StringBuilder("[");
        Node<T> current=hale;
        while (current!=null){
            if (current==hode){
                print.append(current.verdi);
            }
            else {
                print.append(current.verdi+", ");
            }
            current=current.forrige;
        }
        print.append("]");
        return print.toString();
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    public Iterator<T> iterator(int indeks) {
        throw new UnsupportedOperationException();
    }

    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator() {
            denne = hode;     // p starter på den første i listen
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        private DobbeltLenketListeIterator(int indeks) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            return denne != null;
        }

        @Override
        public T next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    } // class DobbeltLenketListeIterator

    public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
        throw new UnsupportedOperationException();
    }

} // class DobbeltLenketListe


