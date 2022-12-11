import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class main {
    public static void accessoApplicazione() throws SQLException {

            boolean accessoRiuscito = false;
            JTextField username = new JTextField();
            JTextField password = new JPasswordField();
            Object[] message = {
                    "Username: ", username,
                    "Password: ", password};
            do {
                int opzioni = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
                String user = username.getText();
                String pass = password.getText();
                System.out.println(opzioni);
                switch (opzioni) {
                    //Se viene cliccato ok,allora viene effettuato l'accesso al database,che se ha successo porta al programma
                    case JOptionPane.OK_OPTION -> {
                        if (user.equals("root") && pass.equals("databaseprog")) {
                            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/progetto_programmazione?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false", user, pass);
                            JOptionPane.showMessageDialog(null, "Accesso riuscito");
                            accessoRiuscito=true;
                        } else
                            //Altrimenti va rifatto l'accesso
                            JOptionPane.showMessageDialog(null, "Accesso fallito,riprovare!!!");
                    }

                    //Se si decide di chiudere la finestra si chiude anche il programma
                    case JOptionPane.CLOSED_OPTION -> {
                        JOptionPane.showMessageDialog(null, "Grazie per aver utilizzato la nostra applicazione,arrivederci!");
                        System.exit(-1);
                    }

                    //Resetta l'input all'interni dei campi di testo
                    case JOptionPane.CANCEL_OPTION -> {
                        username.setText("");
                        password.setText("");
                    }

                }
            } while (!accessoRiuscito);
        }

     public static void main(String[] args) throws NullPointerException, SQLException {
         Connection rtt = DriverManager.getConnection("jdbc:mysql://localhost:3306/progetto_programmazione?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false", "root", "databaseprog");
         Object[] bottoni = {"Inserisci", "Lettura", "Grafici", "Esci"};
         Object[] bottoniGrafici = {"Visualizza", "Esci"};
         Grafici g = Grafici.getInstance();
         Sensori a1 = Sensori.getInstance();
         Polizia p1 = Polizia.getInstance();
         String status;
         JOptionPane.showMessageDialog(null,"Benvenuto nel nuovo software di gestione dell'inquinamento,che permette di effettuare svariate operazioni","Benvenuto",JOptionPane.INFORMATION_MESSAGE);
         System.out.println("\nLa legenda della lattura è la seguente : \ncodice verde,tutti i parametri sono sotto soglia;");
         System.out.println("codice giallo,alcuni parametri sono sopra soglia.");
         System.out.println("codice rosso,tutti i parametri sono sopra soglia.");
          accessoApplicazione();
         Scanner input = new Scanner(System.in);
         do {
             System.out.println("Premi 0 effettuare la lattura dei valori da parte del sensore,che comporterà delle sanzioni in caso di valori fuori norma e in caso di veicoli circolanti quando è vietato!\n");
             System.out.println("Premi 1 per uscire dal programma\n");
             System.out.println("Scelta dell'utente:");
             try {
                 int r = JOptionPane.showOptionDialog(null, "Premere lettura per ottenere la lettura dei valori dai sensori,premere inserisci per inserire un sensore nel sistema,premere grafici per visualizzare graficamente le rilevazioni dei sensori, premere esci per chiudere il programma", "Gestione traffico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, bottoni, bottoni[0]);
                 switch (r) {
                     case 0 -> {
                         status = a1.risultatoLettura();
                         p1.setF1(a1);
                         //A seconda del codice restituito vengono intraprese azioni differenti
                         switch (status) {
                             //Se lo stato della funzione risultatoLettura è codice giallo o verde non vengono intraprese ulteriori azioni
                             case "Codice giallo", "Codice verde" -> {
                                 System.out.println("Risultato della lettura: " + status + ",non sono necessari interventi!");
                                 JOptionPane.showMessageDialog(null, "La lettura ha restituito codice giallo,pertanto non verranno intrapresi interventi!");

                             }

                             //Se lo stato restituito dalla funzione risultatoLettura è codice rosso allora invia la multa al veicolo coinvolto nell'infrazione
                             case "Codice Rosso" -> {
                                 JOptionPane.showMessageDialog(null, "Rilevata eccessivo traffico,ora verrà effettuata la deviazione del traffico");
                                 System.out.println("" + status + ",ora verrà effettuata la deviazione del traffico!");
                                 p1.controlloCircolazione(1);

                             }

                             //Nel caso non venga restituito nessuno stato allora rieffettua la lettura dei valori
                             case " " -> {
                                 System.out.println("Errore!Il sensore non ha effettuato la lettura dei valori!");
                                 JOptionPane.showMessageDialog(null, "Errore!!Il sensore non ha effettuato la lettura dei valori,verrà immediatamente rieffettuata!");
                                 System.out.println("Verrà rieffettuata la lettura dei valori da parte del sensore!");
                                 throw new Exception();
                             }

                         }
                     }


                     case 1 -> a1.ottieniValori();


                     //Uscita dal programma

                     case 2 -> {
                         /*Attraverso questo costrutto è possibile visualizzare i grafici,premendo 1(quindi vedendo i grafici),o 2,andando avanti
                          * nell'esecuzione del programma*/
                         System.out.println("Per visualizzare i grafici delle rilevazioni dei sensori premere 1,altrimenti premere 2");

                         int tn = JOptionPane.showOptionDialog(null, "Premere 1 per vedere i grafici relativi alle rilevazioni dei sensori,per uscire premere 2", "Grafici", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, bottoniGrafici, bottoniGrafici[0]);
                         System.out.println(tn);
                         switch (tn) {
                                    /*Nel caso in cui tn sia 1 viene effettuata la chiamata alla funzione createGraphics che estrapola i dati del sensore
                                    e li mostra all'interno di un grafico,nello specifico vi sono tre grafici relativi alla temperatura,il livello dell'inquinamento
                                    e il numero di veicoli nell'arco di trenta giorni visualizzati giorno per giorno*/
                             case 0 -> {
                                 g.createGraphics();
                                 g.dispose();
                                 continue;
                             }
                             case 1 -> {
                                 //Va avanti con il programma
                                 continue;
                             }

                             default -> {
                                 JOptionPane.showMessageDialog(null, "Errore,è possibile selezionare una sola delle due opzioni");
                                 continue;
                             }

                         }

                     }

                     case 3, -1 -> {
                         System.out.println("\nGrazie per aver utilizzato il nostro programma,arrivederci!");
                         JOptionPane.showMessageDialog(null, "Grazie per aver utilizzato il nostro programma,arrivederci!");
                         System.exit(1);
                     }


                     //In caso l'input non corrisponda alle scelte presentate
                     default -> {
                         System.out.println("Errore!Effettaure una tra le due scelte indicate!!\n");
                         JOptionPane.showMessageDialog(null, "Errrore!!Inserire una tra le scelte indicate!!");
                         continue;
                     }
                 }
                 System.out.print("\033[H\033[2J");
                 System.out.flush();
             } catch (InputMismatchException c) {
                 c.printStackTrace();
                 System.out.println("\n\nErrore!Non sono ammessi altri caratteri oltre quelli indicati per effettuare una scelta!");
                 JOptionPane.showMessageDialog(null, "Non sono ammesse altre scelte oltre a quelle indicate!!!");
                 input.next();
             } catch (NumberFormatException tr) {
                 tr.printStackTrace();
                 JOptionPane.showMessageDialog(null, "Inserire un opzione valida!!");
             } catch (Exception tt) {//Se come visto in precedenza non viene effettuata la lettura dei valori si lancia l'eccezione e si ritorna all'inizio del programma
                 tt.printStackTrace();
                 System.out.println("Errore!!Verrà rieffettuata la lettura dei valori da parte del sensore!!");
               }

            } while(true);
         }

}


