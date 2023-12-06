import table.TableCustom;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class CrudSlov {
    JPanel mainPanel;
    private JTextField textsearch;
    private JButton search;
    private JTable table1;
    private JTextField txtruns;
    private JTextField txtslovo;
    private JButton generate;
    private JPanel genpanel;
    private JButton delete;
    private JButton addButton;
    private JButton update;
    private JButton otmena;
    private JButton cleartxt;
    private JPanel button_panel;
    private JTextPane txtdop;
    private JTextPane txtitogo;
    private JScrollPane jScrollpane1;
    private JPanel niz_otstup;
    private JPanel pravo_otst;
    private JPanel vver_ostup;

    public JFrame frame = new JFrame("Добавление Слов");;



    ArrayList<Slova> slovalist;
    Slova slova;
    String[] header = new String[] { //Название столбцов в форме
            "ID",
            "Слова",
            "Руны",
            "Название",
            "Название",
            "Название",


    };
    DefaultTableModel dtm = new DefaultTableModel(0, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    static Connection conn;
    ResultSet rs;
    int row, col;
    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:identifier.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Connect Success! From SlovarViev");
        }
        catch (Exception ex){

            System.out.println(ex.getMessage());
        }

    }
    public void loadData() throws SQLException {
        System.out.println("Load data");
        slovalist = new ArrayList<>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery("select * from slova ORDER BY `slovo`");
        slovalist.clear();
        while (rs.next()) {
            slovalist.add(new Slova(rs.getInt(1), rs.getString(2), rs.getString(3)
                    , rs.getString(4), rs.getString(5), rs.getString(6)));
        }

        dtm.setRowCount(0); // reset data model
        for (Slova value : slovalist) {
            Object[] objs = {
                    value.slovid,
                    value.slovname,
                    value.slovrun,
                    value.slovdesc,
                    value.slovdop,
                    value.lowslov
            };
            dtm.addRow(objs);
        }


    }

    public void table_load() throws SQLException{

        //Стиль таблицы с переносом строк
        TableCustom.apply(jScrollpane1, TableCustom.TableType.MULTI_LINE);

        table1.setModel(dtm);
        dtm.setColumnIdentifiers(header);
//        //Вывод данных по центру
//        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table1.getDefaultRenderer(String.class);
//        renderer.setHorizontalAlignment(SwingConstants.CENTER);


        //Шрифт шапки таблицы
        table1.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));


        // Скрываем первый столбец (столбец с индексом 0)
        table1.getColumnModel().getColumn(0).setMinWidth(0);
        table1.getColumnModel().getColumn(0).setMaxWidth(0);
        table1.getColumnModel().getColumn(0).setWidth(0);

        table1.getColumnModel().getColumn(2).setMinWidth(0);
        table1.getColumnModel().getColumn(2).setMaxWidth(0);
        table1.getColumnModel().getColumn(2).setWidth(0);

        table1.getColumnModel().getColumn(3).setMinWidth(0);
        table1.getColumnModel().getColumn(3).setMaxWidth(0);
        table1.getColumnModel().getColumn(3).setWidth(0);

        table1.getColumnModel().getColumn(4).setMinWidth(0);
        table1.getColumnModel().getColumn(4).setMaxWidth(0);
        table1.getColumnModel().getColumn(4).setWidth(0);

        table1.getColumnModel().getColumn(5).setMinWidth(0);
        table1.getColumnModel().getColumn(5).setMaxWidth(0);
        table1.getColumnModel().getColumn(5).setWidth(0);


        table1.setRowHeight(30);


    }

    public static void main(String[] args) throws Exception {
        CrudSlov app =new CrudSlov();
        app.menu_interface();


    }
    private void menu_interface(){

        frame.setContentPane(new CrudSlov().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public CrudSlov() {
        connect();
        try {
            table_load();
            loadData();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }


        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (txtruns.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Введите Слово в виде Рун!", "Пустая строка", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String textruns = txtruns.getText();
                    //Подсчет кол-ва рун
                    char ch = '_';
                    if (textruns.endsWith("_")) {
                        textruns = textruns.substring(0, textruns.length() - 1);
                    }
                    int count = textruns.length() - textruns.replace(String.valueOf(ch), "").length() + 1;
                    System.out.println("Кол-во Рун = " + count);
                    //Создание массива рун
                    String[] runs = textruns.split("_");
                    String full_slovo = "";
                    for (int i = 0; i < count; i++) {
                        full_slovo += runs[i].toLowerCase();
                    }
                    if (!txtruns.getText().isEmpty()) {
                        new ArrayOfJTextField(count, runs);
                    }


                    full_slovo = full_slovo.substring(0, 1).toUpperCase() + full_slovo.substring(1);
                    txtslovo.setText(full_slovo);
                }
            }
        });
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
                String search = textsearch.getText();
                System.out.println("Search: "+search );
                //String sql ="select * from runs where name_run COLLATE NOCASE LIKE '%" + search + "%' " ;
                String sql ="select * from slova where LOWER(slovo) LIKE LOWER('%" + search + "%') or low_slov like ('%" + search + "%')";
                System.out.println("SQl: "+sql );
                table1.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                slovalist = new ArrayList<>();
                try {
                    Statement stmt = conn.createStatement();
                    rs = stmt.executeQuery(sql);
                    slovalist.clear();
                    while (rs.next()) {
                        slovalist.add(new Slova(rs.getInt(1), rs.getString(2), rs.getString(3)
                                , rs.getString(4), rs.getString(5), rs.getString(6)));
                    }
                    dtm.setRowCount(0); // reset data model
                    for (int i = 0; i < slovalist.size(); i++) {
                        Object[] objs = {
                                slovalist.get(i).slovid,
                                slovalist.get(i).slovname,
                                slovalist.get(i).slovrun,
                                slovalist.get(i).slovdesc,
                                slovalist.get(i).slovdop,
                                slovalist.get(i).lowslov
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
        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // super.mouseClicked(e);

                int row = table1.rowAtPoint(e.getPoint());
                int col = table1.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    txtslovo.setText(table1.getValueAt(row, 1).toString());
                    txtruns.setText(table1.getValueAt(row, 2).toString());
                    txtitogo.setText(table1.getValueAt(row, 3).toString());

                    String dop ="";
                    if(table1.getValueAt(row, 4)!=""){
                        txtdop.setText(table1.getValueAt(row, 4).toString());
                        dop = table1.getValueAt(row, 4).toString();
                    } else {txtdop.setText("");}

                    slova = new Slova(Integer.parseInt(table1.getValueAt(row, 0).toString()), table1.getValueAt(row, 1).toString(),
                            table1.getValueAt(row, 2).toString(), table1.getValueAt(row, 3).toString(), dop,
                            table1.getValueAt(row, 5).toString());



                }

            }
        });


        cleartxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtslovo.setText("");
                txtruns.setText("");
                txtitogo.setText("");
                txtdop.setText("");




            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();

                String slovo, run_slovo, itogo, dop , low_slov;
                slovo = txtslovo.getText();
                run_slovo = txtruns.getText();
                itogo = txtitogo.getText();
                dop = "";
                if(!txtdop.getText().isEmpty()) {dop = txtdop.getText();}
                low_slov = slovo.toLowerCase();


                if (slovo.isEmpty() || run_slovo.isEmpty() || itogo.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Заполнены не все поля!", "Пустая строка", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    int result = JOptionPane.showConfirmDialog(frame, "Добавить Слово: " + slovo + "?", "Добавление",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Add Slovo - " + slovo);
                            Statement stmt = conn.createStatement();
                            stmt.executeUpdate("insert into slova (`slovo`, `run_slov`,`descr_slov`, `dop`,`low_slov`) VALUES ('" +
                                    slovo + "','" + run_slovo + "','" + itogo + "','" + dop + "','" + low_slov + "')");
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
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();

                if (slova == null) {
                    System.out.println("Null");
                } else {

                    int result = JOptionPane.showConfirmDialog(frame, "Удалить Слово: " + slova.slovname + "?", "Удаление Руны",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Delete Slovo - " + slova.slovname);
                            Statement stmt = conn.createStatement();
                            stmt.executeUpdate("delete from slova where id_slov = '" + slova.slovid + "'");
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
                String slovo, run_slovo, itogo, dop , low_slov;
                slovo = txtslovo.getText();
                run_slovo = txtruns.getText();
                itogo = txtitogo.getText();
                dop = "";
                if(!txtdop.getText().isEmpty()) {dop = txtdop.getText();}
                low_slov = slovo.toLowerCase();
                String sql = null;
                if (slova == null) {
                    System.out.println("Null");
                } else {

                    int result = JOptionPane.showConfirmDialog(frame, "Изменить: " + slova.slovname + "?", "Изменение Руны",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        try {
                            System.out.println("Update Slovo - " + slova.slovname);
                            Statement stmt = conn.createStatement();
                            sql = "update slova set slovo = '" + slovo + "', run_slov = '" +
                                    run_slovo + "', descr_slov = '" + itogo + "', dop = '"  + dop + "',low_slov = '" + low_slov+
                                    "'  where id_slov =" + slova.slovid + "";
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
        otmena.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Close CRUD Slov");
                SlovarViev.closeFrameCrud();
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
