import table.TableCustom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class RunsView {
    public JPanel panelMain;
    private JTextField txtsearch;
    private JButton search;
    private JScrollPane jScrollPane1;
    private JTable table1;
    private JButton otmena;

    public JFrame frame;

    //private SlovarViev slovarViev = new SlovarViev();

    ArrayList<Run> runlist;
    Run run;
    String[] header = new String[]{ //Название столбцов в форме
            "ID",
            "Название",
            "Описание"
    };
    DefaultTableModel dtm = new DefaultTableModel(0, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };


    static Connection conn;
    ResultSet rs;



    public static void main(String[] args) throws Exception {

//        MainForm app = new MainForm();
//        app.checkTables();
//        app.connect();
//        app.loadData();
//        app.mainInterface();


    }

    public void mainInterface() {

//        frame = new JFrame("Добавление Рун");
//        frame.setContentPane(new MainForm().panelMain);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//        frame.setLocationRelativeTo(null);
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:identifier.sqlite";
            // String url = "jdbc:sqlite:slovar.db";

            conn = DriverManager.getConnection(url);
            System.out.println("Connect Success! From RunsView");
        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }

    }

    private void checkTables() {
//        System.out.println("Check table");
//        String sql = "CREATE TABLE IF NOT EXISTS runs (" +
//                "	id_run integer PRIMARY KEY AUTOINCREMENT," +
//                "	name_run varchar(8) NOT NULL," +
//                "	description_run varchar(256) NOT NULL," +
//                "	name_run_low varchar(8) NOT NULL" +
//                ");";
//        try {
//            Statement stmt = conn.createStatement();
//            stmt.executeUpdate(sql);
//        } catch (Exception err) {
//            System.out.println(err.getMessage());
//        }
    }


    public RunsView() {
        connect(); //Connect k database
        try {
            table_load();
            loadData();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                String search = txtsearch.getText();
                System.out.println("Search: " + search);
                //String sql ="select * from runs where name_run COLLATE NOCASE LIKE '%" + search + "%' " ;
                String sql = "select * from runs where LOWER(name_run) LIKE LOWER('%" + search + "%') or name_run_low like ('%" + search + "%') ORDER BY `name_run`";
                System.out.println("SQl: " + sql);
                table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                runlist = new ArrayList<>();
                try {
                    Statement stmt = conn.createStatement();

                    rs = stmt.executeQuery(sql);
                    runlist.clear();
                    while (rs.next()) {
                        runlist.add(new Run(rs.getInt(1), rs.getString(2), rs.getString(3)));
                    }
                    dtm.setRowCount(0); // reset data model
                    for (Run value : runlist) {
                        Object[] objs = {
                                value.runid,
                                value.runname,
                                value.rundesc
                        };
                        dtm.addRow(objs);
                    }

                } catch (Exception err) {
                    System.out.println(err.getMessage());
                } finally {
                    try {
                        rs.close();
                    } catch (Exception err) { /* Ignored */ }
                    // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                    try {
                        conn.close();
                    } catch (Exception err) { /* Ignored */ }
                }
            }
        });


        otmena.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Close Spisok Runs");
                SlovarViev.closeFrameShowRuns();
            }
        });

    }


    private void loadData() throws SQLException {
        System.out.println("Load data");
        runlist = new ArrayList<>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery("select * from runs ORDER BY `name_run`");
        runlist.clear();
        while (rs.next()) {
            runlist.add(new Run(rs.getInt(1), rs.getString(2), rs.getString(3)));
        }

        dtm.setRowCount(0); // reset data model
        for (Run value : runlist) {
            Object[] objs = {
                    value.runid,
                    value.runname,
                    value.rundesc,
            };
            dtm.addRow(objs);
        }

    }

    private void table_load() throws SQLException {

        //Стиль таблицы с переносом строк
        TableCustom.apply(jScrollPane1, TableCustom.TableType.MULTI_LINE);

        table1.setModel(dtm);
        dtm.setColumnIdentifiers(header);
//// Центрирование текста в таблице
//        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table1.getDefaultRenderer(String.class);
//        renderer.setHorizontalAlignment(SwingConstants.CENTER);


        //Шрифт шапки таблицы
        table1.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));

        // Скрываем первый столбец (столбец с индексом 0)
        table1.getColumnModel().getColumn(0).setMinWidth(0);
        table1.getColumnModel().getColumn(0).setMaxWidth(0);
        table1.getColumnModel().getColumn(0).setWidth(0);

        // Устанавливаем ширину столбцов (по вашему выбору)
        table1.getColumnModel().getColumn(1).setPreferredWidth(150);
        table1.getColumnModel().getColumn(1).setMaxWidth(150);


        table1.getColumnModel().getColumn(2).setPreferredWidth(400);

        table1.setRowHeight(30);


    }

}
