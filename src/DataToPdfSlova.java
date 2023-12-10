import java.awt.*;
import java.sql.*;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import table.TableCustom;
public class DataToPdfSlova {
    JTable jt = new JTable();
    static Connection conn;
    ResultSet rs;

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:identifier.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Connect Success! From Excel Runs");
        }
        catch (Exception ex){

            System.out.println(ex.getMessage());
        }

    }

    public void fillDataTable (){
        DefaultTableModel model = new DefaultTableModel(new String[]{
                "Слово", "Руны Слова", "Итого", "Дополнительно"
        },0);

        String[] header = new String[] { //Название столбцов в форме
                "Слово", "Руны Слова", "Итого", "Дополнительно"
        };

        model.setColumnIdentifiers(header);

        try {
            connect();
            Statement stmt = conn.createStatement();
            rs= stmt.executeQuery("SELECT `slovo`, `run_slov`, `descr_slov`, `dop` FROM slova ORDER BY `slovo`");
            while (rs.next()){

                String slovo = " " + rs.getString("slovo");
                String run_slov =" " +  rs.getString("run_slov");
                String itogo = rs.getString("descr_slov");
                String dop = rs.getString("dop");


                model.addRow(new Object[]{
                        slovo, run_slov, itogo, dop
                });
                jt.setModel(model);
            }
        } catch (SQLException err){
            System.out.println(err.getMessage());
        }
    }

    public DataToPdfSlova(){
        connect();
        fillDataTable();
        jt.getColumnModel().getColumn(0).setPreferredWidth(150);

        jt.getColumnModel().getColumn(1).setPreferredWidth(200);

        jt.getColumnModel().getColumn(0).setMaxWidth(150);

        jt.getColumnModel().getColumn(1).setMaxWidth(200);

        jt.setRowHeight(30);
        JFrame frame = new JFrame("Table");
        JScrollPane pane1 = new JScrollPane(jt);
        frame.setPreferredSize(new Dimension(1700,-1));

        //Стиль таблицы с переносом строк
        TableCustom.apply(pane1, TableCustom.TableType.MULTI_LINE);

        frame.add(pane1);
        //pane1.add(jt);
        frame.pack();
        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        MessageFormat header = new MessageFormat("Список Слов");
        MessageFormat footer = new MessageFormat("Page {0, number, integer}");

        try {


            jt.print(JTable.PrintMode.FIT_WIDTH, header,footer);

        }catch (Exception err){
            System.out.println(err.getMessage());
        }
        frame.dispose();

    }
}
