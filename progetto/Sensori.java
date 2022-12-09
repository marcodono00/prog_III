import javax.swing.*;
import java.util.Calendar;
import java.util.Random;


public class Sensori {
    //variabili della classe
    public String stato;

    private int temperatura;
    private int livelloInquinamento;
    private int numVeicoli;
    public String targaVeicolo;

    private static Sensori istance;
    public static Sensori getInstance()  {
        if(istance==null)
            istance=new Sensori();
        return istance;
    }

    public Sensori()
    //costruttore
    {
        stato = " ";
        livelloInquinamento = 0;
        numVeicoli = 0;

    }

    private final Database db =new Database();


    //Attraverso questo metodo si genera casualmente la targa che verrà poi successivamente assegnata ad un veicolo


    /*Il metodo ottiene i valori rilevati dai sensori e a seconda di questi restuituisce la lettura, la quale segue la seguente legenda:
    codice verde , tutti i valori sono sotto la soglia imposta dall'amministratore del sistema,
    codice giallo , vi sono alcuni valori sopra la soglia imposta dall'amministratore,
    codice rosso , tutti i valori sono al di sopra della soglia imposta dall'amministratore*/
    public String risultatoLettura() throws NullPointerException  /*effettua la lettura dei valori e a seconda del fatto che questi rientrino nella soglia o meno viene impostato uno stato*/ {
        try { Automobile auto=Automobile.getInstance();
            Calendar dataAttuale = Calendar.getInstance();
            int meseA = dataAttuale.get(Calendar.MONTH) + 1;
            Random num = new Random();
            String viaSensore, targaAuto;
            System.out.print("Inserire la via in cui il sensore è localizzato:");
            viaSensore = JOptionPane.showInputDialog(null, "Inserisci la via di cui si vogliono conoscere i valori:");
            System.out.println("Sto raccogliendo i valori...");
          /*Questo costrutto fà si che la generazione dei valori produca valori realistici a seconda della stagione
          in cui ci troviamo,ottenuta attraverso il mese meseA
          */
            if (meseA <= 3)
                temperatura = num.nextInt(15);
            else if (meseA <= 6)
                temperatura = num.nextInt(28) + 17;
            else if (meseA <= 9)
                temperatura = num.nextInt(40) + 21;
            else if (meseA <= 12)
                temperatura = num.nextInt(21) + 5;
            livelloInquinamento = num.nextInt(5100);
            numVeicoli = num.nextInt(150);
            targaAuto = Automobile.generaTarga();
            Polizia p1 = Polizia.getInstance();
            p1.setF1(this);
            if ((temperatura <= 20 && livelloInquinamento <= 300 && numVeicoli <= 70))
            //Se i valori sono tutti sotto soglia allora lo stato sarà codice verde
            {
                db.inserimentoValori(temperatura, livelloInquinamento, numVeicoli, viaSensore, this);
                stato = "Codice verde";
            } else if ((temperatura >= 20 && livelloInquinamento >= 300 && numVeicoli >= 70))
            //Se i valori sono invece tutti sopra soglia invece lo stato restituirà codice rosso
            {
                stato = "Codice rosso";
                db.inserimentoValori(temperatura, livelloInquinamento, numVeicoli, viaSensore, this);
                db.infrazioneVeicolo(numVeicoli, viaSensore, targaAuto, this);
                p1.controlloCircolazione(1);
            }


            if ((temperatura <= 20 && livelloInquinamento >= 300 && numVeicoli >= 70) || (temperatura >= 20 && livelloInquinamento >= 300 && numVeicoli <= 70) || (temperatura >= 20 && livelloInquinamento <= 300 && numVeicoli >= 70))
            //Mentre invece se 2 dei 3 parametri sono sopra soglia allora lo stato sarà codice giallo
            {
                stato = "Codice giallo";
                db.inserimentoValori(temperatura, livelloInquinamento, numVeicoli, viaSensore, this);
                db.infrazioneVeicolo(numVeicoli, viaSensore, targaAuto, this);
            }

            if (stato.equals(""))
                throw new Exception();

            //al termine restituisce lo stato ottenuto dalla lettura dei valori
        } catch (Exception nd) {
            nd.printStackTrace();
            System.out.println("Errore!!");
            risultatoLettura();
        }

        return stato;
    }


    /*La funzione si occupa della ricerca dei valori della rilvazione del sensore corrispondente richiedendo l'inserimento in input della
    * via in cui è localizzatp il sensore,insieme al giorno e al mese della rilevazione di cui si vogliono ottenere i valori*/
    public void ottieniValori() {
            JTextField via = new JTextField(10);
            JTextField giorno = new JTextField(10);
            JTextField mese = new JTextField(10);
            //Object[] bottoniGrafici = {"OK", "Cancel"};
            try {
                /*JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Immetti la via,il giorno e il mese del sensore di cui si vogliono conoscere i valori:\n"));
                myPanel.add(Box.createHorizontalStrut(10));
                myPanel.add(new JLabel("Via:\n"));
                myPanel.add(via);
                myPanel.add(Box.createHorizontalStrut(10));
                myPanel.add(new JLabel("Giorno:\n"));
                myPanel.add(giorno);
            myPanel.add(Box.createHorizontalStrut(10));
            myPanel.add(new JLabel("Mese:\n"));
            myPanel.add(mese);*/
                Object[] message = {
                        "Via: ", via,
                        "Giorno: ", giorno,
                        "Mese:",mese};


            //int result = JOptionPane.showOptionDialog(null, myPanel, "Lettura valori", JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,bottoniGrafici,bottoniGrafici[0]);
            int result=JOptionPane.showConfirmDialog(null,message,"Inserimento parametri",JOptionPane.OK_CANCEL_OPTION );
                switch(result) {
                case JOptionPane.OK_OPTION -> {
                    String viaB,g,m;
                    int gg,mm;
                    viaB=via.getText();
                    g=giorno.getText();
                    m=mese.getText();
                    gg= Integer.parseInt(g);
                    mm= Integer.parseInt(m);
                    System.out.println(g + m);
                    db.connessioneDB4(viaB, gg, mm);
                    if (viaB == null)
                        throw new Exception();
                }

                case JOptionPane.CANCEL_OPTION->
                {
                    via.setText("");
                 giorno.setText("");
                 mese.setText("");
                }


               default ->
                       JOptionPane.showMessageDialog(null,"Errore,non si possono fare altre scelte!");
            }

        }
        catch (Exception t)
        {
            t.printStackTrace();
            //JOptionPane.showMessageDialog(null,"Errore!!Valori inseriti 11 non corretti,reinserire i valori!");
            ottieniValori();
        }

    }


}


