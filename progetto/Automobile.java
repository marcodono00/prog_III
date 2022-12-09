public class Automobile {
    private static String targaVeicolo;

    public Automobile() {
        targaVeicolo="";
    }

    public static String generaTarga() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char ch = (char) (Math.random() * 26 + 'A');
            s.append(ch);
        }

        for (int i = 0; i < 4; i++) {
            int digit1 = (int) (Math.random() * 10 + '0');
            s.append(digit1);
        }
        //Converte la l'insieme di lettere e numeri che sono stati generati in una stringa che sarà la targa finale
        targaVeicolo = s.toString();

        if (targaVeicolo.equals(null))
            generaTarga();
        return targaVeicolo;
    }

}

