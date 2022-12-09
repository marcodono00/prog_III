import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
public class Polizia {
     private Sensori f1;

    public void setF1(Sensori d1) {
     //imposta il sensore della classe polizia con i valori del sensore in input
        this.f1 = d1;
    }


    public void controlloCircolazione(int bz) throws SQLException {
        /*Il metodo provvede al controllo della circolazione in caso i valori rilevati dai sensori siano tutti sopra soglia (codice rosso)
         effettuando una verifica della targa secondo il principio delle targhe alterne:
        targhe pari giorni pari , targhe dispari in giorni dispari.Se quests regola non viene seguita viene inviata una multa e si dirotta il controllo della circolazione*/
        String status;
        Database db=new Database();
        status=f1.risultatoLettura();
        LocalDate dataAttuale=LocalDate.now();
        char ab=f1.targaVeicolo.charAt(6);
        int p=Integer.parseInt(String.valueOf(ab));
        /*Attraverso il costrutto seguente si applica il principio delle targhe alterne,secondo cui non è
        * possibile circolare con targa pari in giorni dispari e viceversa,provvedendo ad effettuare una multa ai veicoli
        * coinvolti nell'effrazione */
        if(status.equals("Codice rosso")) {
            switch (bz) {
                case 1 -> {
                    if (dataAttuale.getDayOfMonth() % 2 == 0) {
                        System.out.println("Controllo targa in corso...\n");
                        //in caso di targa pari giorni pari
                        if (p % 2 == 0) {
                            JOptionPane.showMessageDialog(null, "Il veicolo ha targa pari,poichè oggi è il giorno " + dataAttuale.getDayOfMonth() + "il veicolo può circolare in data odierna");
                            System.out.println("Il veicolo ha targa pari, può circolare in data odierna!\n");
                        }

                        //in caso invece targa pari giorni dispari
                        else {
                            System.out.println("Il veicolo ha targa dispari,non può circolare!\n");
                            JOptionPane.showMessageDialog(null, "Infrazione rilevata!La vettura con targa " + f1.targaVeicolo + " verrà segnalata alla centralina di polizia relativa che provvederà all'invio della multa di Euro 140");
                            System.out.println("Infrazione rilevata!La vettura con targa " + f1.targaVeicolo + "verrà segnalata alla centralina di polizia relativa che provvederà all'invio della multa di Euro 140");
                            db.inserimentoMulta(f1.targaVeicolo, 140, this);
                        }
                    } else {
                        //in caso di targa dispari giorni pari
                        if (p % 2 == 1) {
                            JOptionPane.showMessageDialog(null, "Il veicolo ha targa dispari,poichè oggi è il giorno " + dataAttuale.getDayOfMonth() + "il veicolo può circolare in data odierna");
                            System.out.println("Il veicolo ha targa dispari,può circolare in data odierna!\n");
                        }

                        //invece in caso di targa dispari giorni dispari
                        else {
                            System.out.println("Il veicolo non può circolare,ha targa pari!\n");
                            System.out.println("Infrazione rilevata!La vettura con targa " + f1.targaVeicolo + " verrà segnalata alla centralina di polizia relativa che provvederà all'invio della multa di Euro 140");
                            JOptionPane.showMessageDialog(null, "Infrazione rilevata!La vettura con targa " + f1.targaVeicolo + " verrà segnalata alla centralina di polizia relativa che provvederà all'invio della multa di Euro 140");
                            db.inserimentoMulta(f1.targaVeicolo, 140, this);
                        }
                    }
                }
                //Si effettua la deviazione su un'altra strada,funzione esplicitata da connessioneDB2
                case 2 -> System.out.println("Il traffico verrà ora dirottato su un altro percorso...");
            }
        }
    }
}
