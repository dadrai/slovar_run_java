import table.TableCustom;

import java.awt.*;
import java.sql.*;
import java.text.MessageFormat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import table.TableCustom;
public class DataToPdfRuns {
    JTable jt = new JTable();
    static Connection conn;
    ResultSet rs;
    int row, col;

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
                "Название", "Описание"
        },0);

        String[] header = new String[] { //Название столбцов в форме
                "Название",
                "Описание"
        };

        model.setColumnIdentifiers(header);




        try {
            connect();
            Statement stmt = conn.createStatement();
            rs= stmt.executeQuery("SELECT `name_run`, `description_run` FROM runs ORDER BY `name_run`");
            while (rs.next()){
                String name_run = " " + rs.getString("name_run");
                String description_run= " " + rs.getString("description_run");


                model.addRow(new Object[]{
                        name_run, description_run
                });
                jt.setModel(model);
            }
        } catch (SQLException err){
            System.out.println(err.getMessage());
        }
    }

    public DataToPdfRuns(){
        connect();
        fillDataTable();

        jt.getColumnModel().getColumn(0).setPreferredWidth(100);

        jt.getColumnModel().getColumn(0).setMaxWidth(100);

        jt.setRowHeight(30);
        JFrame frame = new JFrame("Table");
        JScrollPane pane1 = new JScrollPane(jt);

        //Стиль таблицы с переносом строк
        TableCustom.apply(pane1, TableCustom.TableType.MULTI_LINE);

        frame.setPreferredSize(new Dimension(1100,-1));

        frame.add(pane1);
        //pane1.add(jt);
        frame.pack();
        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        MessageFormat header = new MessageFormat("Список Рун");
        MessageFormat footer = new MessageFormat("Page {0, number, integer}");

        try {


            jt.print(JTable.PrintMode.FIT_WIDTH, header,footer);
        }catch (Exception err){
            System.out.println(err.getMessage());
        }
        frame.dispose();
    }
}
