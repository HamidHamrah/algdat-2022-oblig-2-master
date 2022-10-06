package no.oslomet.cs.algdat.Oblig2;


////////////////// class DobbeltLenketListe //////////////////////////////


import javax.print.DocFlavor;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.temporal.ChronoUnit;
import java.util.*;


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
        if(indeks < 0 || antall < indeks){
            throw new IndexOutOfBoundsException("Indeks kan ikke være negative eller støre enn antall.");
        }

        if(antall == 0) {
            leggInn(verdi);
            return;
        }

        else if(indeks == 0){
            Node<T> ny = new Node<>(verdi);
            ny.neste = hode;
            hode.forrige = ny;
            hode = ny;

        }

        else if(indeks == antall){
            Node<T> ny = new Node<>(verdi);
            ny.forrige = hale;
            hale.neste = ny;
            hale = ny;
        }

        else if(indeks > antall/2){
            Node<T> aktuell = hode;
            for(int i = 0; i < indeks-1; i++){
                aktuell = aktuell.neste;
            }

            Node<T> ny = new Node<>(verdi);
            ny.neste = aktuell.neste;
            ny.forrige = aktuell;
            aktuell.neste = ny;
            ny.neste.forrige = ny;
        }

        else{
            Node<T> aktuell = hale;
            for(int i = antall; i > indeks; i--){
                aktuell = aktuell.forrige;
            }

            Node<T> ny = new Node<>(verdi);
            ny.neste = aktuell.neste;
            ny.forrige = aktuell;
            aktuell.neste = ny;
            ny.neste.forrige = ny;


        }
        antall++;
        endringer++;
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
      if (verdi==null){
          return false;
      }
      Node<T> current=hode;
      int i=1;
      while (!current.verdi.equals(verdi) && i<antall){
          current=current.neste;
          i++;
      }
      if (i==antall && !current.verdi.equals(verdi)){return false;}
      if (antall==1){
          hode=null;
          hale=null;
      } else if (current.forrige==null) {
          hode.neste.forrige=null;
          hode=hode.neste;
      } else if (current.neste==null) {
          hale.forrige.neste=null;
          hale=hale.forrige;
      }
      else {
          current.forrige.neste=current.neste;
          current.neste.forrige=current.forrige;
      }
      antall--;
      endringer++;
      return true;
    }

    @Override
    public T fjern(int indeks) {
      if (indeks<0 || indeks>=antall){
          throw new IndexOutOfBoundsException(" indeks kan ikke være negative eller støre enn antall");
      }
      T returnverdi;
      if (antall==1){
          returnverdi=hode.verdi;
          hode=null;
          hale=null;
      } else if (indeks==0) {
          returnverdi=hode.verdi;
          hode.neste.forrige=null;
          hode=hode.neste;
      } else if (indeks==antall-1) {
          returnverdi= hale.verdi;
          hale.forrige.neste=null;
          hale=hale.forrige;
      }
      else {
          Node<T> current=hode;
          for (int i=0; i<indeks; i++){
              current=current.neste;
          }
          returnverdi= current.verdi;
          current.forrige.neste=current.neste;
          current.neste.forrige=current.forrige;
      }
      antall--;
      endringer++;
      return returnverdi;
    }

    @Override
    public void nullstill() {
        for(Node<T> t = hode; t != null; t = t.neste) {
            t.verdi = null;
            t.forrige = t.neste = null;
        }
        hode = hale = null;
        antall = 0;
        endringer++;
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
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
       indeksKontroll(indeks,false);
       return new DobbeltLenketListeIterator(indeks);
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
           denne=finnNode(indeks);
           fjernOK=false;
           iteratorendringer=endringer;
        }

        @Override
        public boolean hasNext() {
            return denne != null;
        }

        @Override
        public T next() {
            if (iteratorendringer!=endringer){
                throw new ConcurrentModificationException("iterator endringer er skal være lik endinger.");
            }
            if (hasNext()!=true){
                throw new NoSuchElementException("Det er ikkke flere igjen i listen");
            }
            fjernOK=true;
            T denneVerdi= denne.verdi;
            denne=denne.neste;
            return denneVerdi;
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


