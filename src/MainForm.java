import javax.swing.*;
import java.awt.event.*;
import  java.sql.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;


public class MainForm {
    private JPanel panelMain;
    private JTextField txtrunName;
    private JTextField txtrunText;
    private JButton addButton;
    private JButton search;
    private JButton delete;
    private JTextField txtsearch;
    private JTable table1;
    private JButton update;
    private JTextArea txtdescrun;
    private JScrollPane table_1;
    private JFrame frame;

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
    PreparedStatement pst;
    int row, col;

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Добавление Рун");
        frame.setContentPane(new MainForm().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        MainForm app = new MainForm();
        app.loadData();
        app.mainInterface();
        

    }

    private void mainInterface(){


    }
    public void connect(){
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:identifier.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Connect Success!");
        }
       catch (SQLException ex){

           System.out.println(ex.getMessage());
       }

    }





    public MainForm() {
        connect(); //Connect k database
        try {
            table_load();
        }
       catch (SQLException e){
            System.out.println(e.getMessage());
       }
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String name_run, descr_run;
                name_run = txtrunName.getText();
                descr_run = txtdescrun.getText();


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
                            stmt.executeUpdate("insert into runs (`name_run`, `description_run`) VALUES ('" +
                                    name_run + "','" + descr_run + "')");
                            loadData();
                        } catch (Exception err) {
                            System.out.println(err.getMessage());
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
        table1.addComponentListener(new ComponentAdapter() {
        });
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                        }
                    }

                }
            }
        });
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String runname = txtrunName.getText();
                String rundesc = txtdescrun.getText();
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
                                      rundesc + "'  where id_run =" + run.runid + "";
                            stmt.executeUpdate(sql);
                            loadData();
                        } catch (Exception err) {
                            System.out.println(err.getMessage());
                            System.out.println(sql);
                        }
                    }

                }
            }
        });
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String search = txtsearch.getText();
                System.out.println("Search: "+search);
                table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                runlist = new ArrayList<>();
                try {

                    Statement stmt = conn.createStatement();
                    rs = stmt.executeQuery("select * from runs where name_run LIKE LOWER('%" + search + "%') ");
                    runlist.clear();
                    while (rs.next()) {
                        runlist.add(new Run(rs.getInt(1), rs.getString(2), rs.getString(3)));
                    }
                    dtm.setRowCount(0); // reset data model
                    for (int i = 0; i < runlist.size(); i++) {
                        Object[] objs = {
                                runlist.get(i).runid,
                                runlist.get(i).runname,
                                runlist.get(i).rundesc
                        };
                        dtm.addRow(objs);
                    }

                } catch (Exception err) {
                    System.out.println(err.getMessage());
                }
            }
        });
    }


    private void loadData() throws SQLException {
        System.out.println("Load data");
        runlist = new ArrayList<>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery("select * from runs");
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
        table1.setModel(dtm);
        dtm.setColumnIdentifiers(header);
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
