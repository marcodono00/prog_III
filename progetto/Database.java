import javax.swing.*;
import java.sql.*;
import java.util.Calendar;
import java.util.Objects;

public class Database {

    private final Connection connessione;

    {
        try {
            connessione = DriverManager.getConnection("jdbc:mysql://localhost:3306/progetto_programmazione?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false","root","strunz");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    final Calendar dataAttuale = Calendar.getInstance();

    private final int giornoAtt = dataAttuale.get(Calendar.DAY_OF_MONTH);
    private final int meseAtt = dataAttuale.get(Calendar.MONTH) + 1;


    






    /*Il metodo seguente effettua l'inserimento dei valori rilevati dai sensori,effettuando il controllo dei duplicati
    relativamente alle vie o di valori che sono fuori scala,reinserendo così i valori corretti*/
    public void inserimentoValori(int temperatura, int livelloInquinamento, int numVeicoli, String viaSensore, Sensori s1) {

        try {
            PreparedStatement stmt = connessione.prepareStatement("INSERT INTO sensore(temperatura,livello_inquinamento,numero_veicoli,giorno_attuale,mese_attuale,via) VALUES (?, ?, ?,?,?,?)");
            stmt.setInt(1, temperatura);
            stmt.setInt(2, livelloInquinamento);
            stmt.setInt(3, numVeicoli);
            stmt.setInt(4, giornoAtt);
            stmt.setInt(5, meseAtt);
            stmt.setString(6, viaSensore);
            stmt.execute();

            if (temperatura < 10 || viaSensore.equals(" ")) {
                viaSensore=JOptionPane.showInputDialog(null,"Non è stata inserita una via!Inserire la via corretta:");
                stmt.setInt(1, temperatura);
                stmt.setInt(2, livelloInquinamento);
                stmt.setInt(3, numVeicoli);
                stmt.setInt(4, giornoAtt);
                stmt.setInt(5, meseAtt);
                stmt.setString(6, viaSensore);
                stmt.execute();
            }

            Polizia p1 = new Polizia();
            p1.setF1(s1);
        }

        catch(SQLException r)
        { r.printStackTrace();
         System.out.println("Errore!Dati già inseriti!!");
            JOptionPane.showMessageDialog(null,"Errore,dati già inseriti!!Riprovare!");
        }

    }



        /*Il metodo seguente provvede ad inserire all'interno della tabella corrispondente alla polizia,così come nella tabella corrispondente all'automobile
         che ha commesso l'nfrazione e la città in cui è avvenuto ci ò i dati relativi
        * come il giorno,il mese e la via dell'infrazione corrispondente,insieme alla targa del veicolo che ha commesso quest'ultimo*/
    public void infrazioneVeicolo(int numVeic, String viaSensore, String numTarga, Sensori s2) throws SQLException {
            Polizia pz = new Polizia();
            pz.setF1(s2);
            String viaDeviazione = "";
            PreparedStatement stmt1 = connessione.prepareStatement("INSERT INTO polizia(giorno_infrazione,mese_infrazione,via_infrazione,targa_veicolo) VALUES (?, ?,?,?)");
            PreparedStatement stmt2 = connessione.prepareStatement("INSERT INTO citta(num_automobili,via_t) VALUES (?,?)");
            PreparedStatement stmt3 = connessione.prepareStatement("INSERT INTO automobile(num_targa) VALUES (?)");
            if (numVeic > 45) {
                try {
                    System.out.println("Numero di veicoli eccessivo[" + numVeic + "],verrà effettuata la deviazione verso un'altra via");
                    System.out.print("Inserire la via in cui si vuole deviare il traffico:");
                    viaDeviazione = JOptionPane.showInputDialog(null, "Numero di veicoli eccessivo [" + numVeic + "],inserire la via in cui si vuole deviare il traffico:");
                    stmt2.setInt(1, numVeic);
                    stmt2.setString(2, viaDeviazione);
                    stmt2.execute();
                    if (viaDeviazione.equals("")) {
                        JOptionPane.showMessageDialog(null, "Errore,inserire la via in cui si vogliono deviare i veicoli:");
                        viaDeviazione = JOptionPane.showInputDialog(null, "Errore,inserire la via in cui si vogliono deviare i veicoli:");
                        stmt2.setString(2, viaDeviazione);
                        stmt2.execute();
                    }
                }

                catch (SQLException gh) {
                    gh.printStackTrace();
                    viaDeviazione=JOptionPane.showInputDialog(null,"Errore,inserire la via in cui si vogliono deviare i veicoli:");
                    System.out.println("Via già presente all'interno del database!!");
                }
                catch (Exception rt)
                {
                    rt.printStackTrace();
                    connessione.setAutoCommit(false);
                    connessione.rollback();
                    JOptionPane.showMessageDialog(null,"Errore,inserisci una via");

                }

            }


        try{
        stmt3.setString(1, numTarga);
        stmt3.execute();
        }
        catch(SQLIntegrityConstraintViolationException b)
        {
            System.out.println("La targa è già presente all'interno del database");
        }

        try {
            stmt1.setInt(1, giornoAtt);
            stmt1.setInt(2, meseAtt);
            stmt1.setString(3, viaSensore);
            stmt1.setString(4, numTarga);
            stmt1.execute();
        }
        catch(SQLIntegrityConstraintViolationException f)
        {   f.printStackTrace();
            System.out.println("I valori sono già presenti all'interno della tabella");

        }
        catch (SQLException rtt)
        {
         rtt.printStackTrace();
        }

        if (Objects.equals(viaDeviazione, " ")) {
            System.out.println("Non è stata inserita la via in cui il traffico verrà deviato!Inserire la via!");
            connessione.rollback();
        }

    }

    /*Il metodo seguente si occupa dell'inserimento all'interno della tabella corrispondente alla polizia i dati relativi alla targa
     * del veicolo coinvolto nell'infrazione e l'invio della multa all'automobilista avemnte macchina con targa corrispondente a quella
     * coinvolta nell'infrazione */
    public void inserimentoMulta(String targa, int multa, Polizia p1) throws SQLException {
        PreparedStatement ps1 = connessione.prepareStatement("INSERT INTO POLIZIA (infrazione) VALUES (?)");
        PreparedStatement ps2 = connessione.prepareStatement("INSERT INTO AUTOMOBILE (infrazione) VALUES (?)");
        PreparedStatement ps3 = connessione.prepareStatement("SELECT targa_veicolo FROM POLIZIA JOIN automobile on polizia.targa_veicolo=automobile.num_targa");
            try {
                ResultSet rs1 = ps3.executeQuery();
                String targaVeicolo = rs1.toString();
                if (targa.equals(targaVeicolo)) {
                    ps1.setInt(1, multa);
                    ps2.setInt(1, multa);
                    ps1.execute();
                    ps2.execute();
                }
            }

            catch(SQLIntegrityConstraintViolationException gh)
            {  gh.printStackTrace();
                System.out.println("Le targhe non combaciano,errore!!!");
            }

            catch(Exception tre)
            {
                tre.printStackTrace();
            }

    }
    /*La funzione va a ricercare i valori richiesti in input (nel caso specifico la via in cui è localizzato il sensore,
    * il giorno e il mese della rilevazione), per poi memorizzarli all'interno dell'array che va a restituire come output che verrà
    * utilizzato dalla funzione ottieniValori localizzata nella classe Sensori per mostrare a schermo i valori ricercati*/
    public void connessioneDB4(String viaDaCercare, int giorno, int mese) throws SQLException
    {int temperatura,livelloInquinamento,numeroVeicoli;
         PreparedStatement pd=connessione.prepareStatement("SELECT temperatura,livello_inquinamento,numero_veicoli FROM SENSORE WHERE VIA=? AND GIORNO_ATTUALE=? AND MESE_ATTUALE=?");
         pd.setString(1,viaDaCercare);
         pd.setInt(2,giorno);
         pd.setInt(3,mese);
         ResultSet rt=pd.executeQuery();
        try{
            if (rt.next())
            {
                temperatura=rt.getInt("temperatura");
                livelloInquinamento=rt.getInt("livello_inquinamento");
                numeroVeicoli=rt.getInt("numero_veicoli");
                String j="Via: "+ viaDaCercare + "\nTemperatura:"+ temperatura + "°C" +
                        "\nLivello Inquinamento: "  +  livelloInquinamento + "u"+
                        "\nNumero veicoli: "+ numeroVeicoli;
                JOptionPane.showMessageDialog(null,j,"Rilevazione dei sensori",JOptionPane.INFORMATION_MESSAGE);
            }
         else
             throw new Exception();
        }
        catch(SQLException ret)
        {
            ret.printStackTrace();
        connessioneDB4(viaDaCercare,giorno,mese);
        }

        catch(Exception ty)
        {System.out.println("Non sono presenti dati corrispondenti,riprovare!!");}
    }
}