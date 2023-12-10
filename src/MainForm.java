

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import table.TableCustom;



public class MainForm {
    public JPanel panelMain;
    private JTextField txtrunName;
    private JTextField txtrunText;
    private JButton addButton;
    private JButton search;
    private JButton delete;
    private JTextField txtsearch;
    private JTable table1 ;
    private JButton update;
    private JTextArea txtdescrun;
    private JButton cleartxt;
    private JButton otmena;
    private JPanel button_panel;
    private JScrollPane jScrollPane1;
    private JScrollPane table_1;
    public JFrame frame;

    //private SlovarViev slovarViev = new SlovarViev();

    ArrayList<Run> runlist;
    Run run;
    String[] header = new String[] { //Название столбцов в форме
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

    public void mainInterface(){

//        frame = new JFrame("Добавление Рун");
//        frame.setContentPane(new MainForm().panelMain);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
//        frame.setLocationRelativeTo(null);
    }
    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:identifier.sqlite";
           // String url = "jdbc:sqlite:slovar.db";

            conn = DriverManager.getConnection(url);
            System.out.println("Connect Success! From MainForm");
        }
       catch (Exception ex){

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



    public MainForm() {
        connect(); //Connect k database
        try {
            table_load();
            loadData();

        }
       catch (SQLException e){
            System.out.println(e.getMessage());
       }
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                connect();

                String name_run, descr_run;
                name_run =  txtrunName.getText().substring(0,1).toUpperCase()+txtrunName.getText().substring(1).toLowerCase();

                descr_run = txtdescrun.getText();
                String name_run_low = name_run.toLowerCase();


                if (name_run.isEmpty() || descr_run.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Введите имя и описание Руны!", "Пустая строка", JOptionPane.INFORMATION_MESSAGE);
                    txtrunName.requestFocus();
                } else {
                    int result = JOptionPane.showConfirmDialog(frame, "Добавить Руну: " + name_run + "?", "Добавление",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Add Run - " + name_run);
                            Statement stmt = conn.createStatement();
                            stmt.executeUpdate("insert into runs (`name_run`, `description_run`,`name_run_low`) VALUES ('" +
                                    name_run + "','" + descr_run + "','" + name_run_low + "')");
                            loadData();
                        } catch (Exception err) {
                            System.out.println(err.getMessage());
                        }finally {
                            try { rs.close(); } catch (Exception err) { /* Ignored */ }
                            // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                            try { conn.close(); } catch (Exception err) { /* Ignored */ }
                        }
                    }
                }


            }
        });
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = table1.rowAtPoint(e.getPoint());
                int col = table1.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    txtrunName.setText(table1.getValueAt(row, 1).toString());
                    txtdescrun.setText(table1.getValueAt(row, 2).toString());
                    run = new Run(Integer.parseInt(table1.getValueAt(row, 0).toString()), table1.getValueAt(row, 1).toString(),
                            table1.getValueAt(row, 2).toString());
                }
            }
        });
        cleartxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtrunName.setText("");
                txtdescrun.setText("");
            }
        });

        table1.addComponentListener(new ComponentAdapter() {
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();

                if (run == null) {
                    System.out.println("Null");
                } else {

                    int result = JOptionPane.showConfirmDialog(frame, "Удалить Руну: " + run.runname + "?", "Удаление Руны",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Delete Run - " + run.runname);
                            Statement stmt = conn.createStatement();
                            stmt.executeUpdate("delete from runs where id_run = '" + run.runid + "'");
                            loadData();
                        } catch (Exception err) {
                            System.out.println(err.getMessage());
                        }finally {
                            try { rs.close(); } catch (Exception err) { /* Ignored */ }
                            // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                            try { conn.close(); } catch (Exception err) { /* Ignored */ }
                        }
                    }

                }
            }
        });
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                String runname = txtrunName.getText().substring(0,1).toUpperCase()+txtrunName.getText().substring(1).toLowerCase();
                String rundesc = txtdescrun.getText();
                String name_run_low = runname.toLowerCase();
                String sql = null;
                if (run == null) {
                    System.out.println("Null");
                } else {

                    int result = JOptionPane.showConfirmDialog(frame, "Изменить: " + run.runname + "?", "Изменение Руны",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Update Run - " + run.runname);
                            Statement stmt = conn.createStatement();
                            sql = "update runs set name_run = '" + runname + "', description_run = '" +
                                      rundesc + "', name_run_low = '" + name_run_low +
                                    "'  where id_run =" + run.runid + "";
                            stmt.executeUpdate(sql);
                            loadData();
                        } catch (Exception err) {
                            System.out.println(err.getMessage());
                            System.out.println(sql);
                        }finally {
                            try { rs.close(); } catch (Exception err) { /* Ignored */ }
                            // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                            try { conn.close(); } catch (Exception err) { /* Ignored */ }
                        }
                    }

                }
            }
        });
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                String search = txtsearch.getText();
                System.out.println("Search: "+search );
                //String sql ="select * from runs where name_run COLLATE NOCASE LIKE '%" + search + "%' " ;
                String sql ="select * from runs where LOWER(name_run) LIKE LOWER('%" + search + "%') or name_run_low like ('%" + search + "%') ORDER BY `name_run`";
                System.out.println("SQl: "+sql );
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
                }finally {
                    try { rs.close(); } catch (Exception err) { /* Ignored */ }
                   // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                    try { conn.close(); } catch (Exception err) { /* Ignored */ }
                }
            }
        });

       
        otmena.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Close Spisok Runs");
                SlovarViev.closeFrame();
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

    private void table_load() throws SQLException{

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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

}
