import org.jetbrains.annotations.Nullable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.jdbc.JDBCXYDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Calendar;

public class Grafici extends JFrame{

    private static Grafici istance;
    public static Grafici getInstance()  {
        if(istance==null)
            istance=new Grafici();
        return istance;
    }
    private final JFrame frame=new JFrame("Grafici valori sensori");

    private @Nullable JDBCXYDataset createDataset(String valore) {
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/progetto_programmazione?allowPublicKeyRetrieval=true&serverTimezone=UTC&useSSL=false", "root", "strunz");
            ResultSet rt;
            Statement st = conn.createStatement();
            PreparedStatement ps = conn.prepareStatement("insert into inventory values (?,?,?,?)");
            rt=st.executeQuery("select temperatura,livello_inquinamento,numero_veicoli from sensore where giorno_attuale=(select extract(day from current_date))");

            Calendar c = Calendar.getInstance();
            while(true) {
                assert rt != null;
                if (!rt.next()) break;
                ps.setTimestamp(1, new Timestamp(c.getTimeInMillis()));
                ps.setInt(2,rt.getInt(1));
                ps.setInt(3,rt.getInt(2));
                ps.setInt(4,rt.getInt(3));
                ps.execute();
                c.add(Calendar.HOUR_OF_DAY, 1);
            }

            JDBCXYDataset jds = new JDBCXYDataset(conn);

            if(valore.equals("temperatura"))
                jds.executeQuery("select whel,n1 from inventory order by whel");
            if(valore.equals("livello_inquinamento"))
                jds.executeQuery("select whel,n2 from inventory order by whel");
            if(valore.equals("numero_veicoli"))
                jds.executeQuery("select whel,n3 from inventory order by whel");
            return jds;
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }





    /*Attraverso questa funzione si visualizzano i dati all'interno di un grafico visti su base mensile,nello specifico vengono mostrate
        * le rilevazioni effettuate dai sensori delle temperature,del livello di inquinamento e del numero di veicoli c ogni giorno per tutto il mese*/
     public void createGraphics() {
         
         try {
             Connection cd = DriverManager.getConnection("jdbc:mysql://localhost:3306/progetto_programmazione?serverTimezone=UTC&useSSL=false", "root", "strunz");
             frame.setTitle("Rilevamento valori sensori");
             frame.setLayout(new FlowLayout());
             frame.setVisible(true);
             JDBCXYDataset dataset = createDataset("temperatura");
             JFreeChart chart1 = ChartFactory.createTimeSeriesChart("Temperature rilevate", "Ora", "Temperature", dataset, false, false, false);
             frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
             frame.getContentPane().add(new ChartPanel(chart1));
             JDBCXYDataset dataset1 = createDataset("livello_inquinamento");
             JDBCXYDataset dataset2 = createDataset("numero_veicoli");
             JFreeChart chart2 = ChartFactory.createTimeSeriesChart("Inquinamento rilevato", "Ora", "Inquinamento", dataset1, false, false, false);
             frame.getContentPane().add(new ChartPanel(chart2));
             JFreeChart chart3 = ChartFactory.createTimeSeriesChart("Numero veicoli rilevato", "Ora", "Numero veicoli", dataset2, false, false, false);
             frame.getContentPane().add(new ChartPanel(chart3));
             Toolkit tk=Toolkit.getDefaultToolkit(); //Initializing the Toolkit class.
             Dimension screenSize = tk.getScreenSize(); //Get the Screen resolution of our device.
             frame.setSize(screenSize.width,screenSize.height); //Set the width and height of the JFrame.
             frame.pack();
             Thread.sleep(2000000);
         }catch (Exception yr)
         //Attraverso questa funzione si dealloca la finestra nella quale sono contenuti i grafici
         {
             yr.printStackTrace();
         }
     }




}



