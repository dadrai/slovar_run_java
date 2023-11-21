import org.w3c.dom.Text;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.Style;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


public class SlovarViev {
    private JTable table1;
    private JTextField textsearch;
    private JButton search;
    private JPanel mainPanel;
    private JTextPane textPane1;
    private  JMenuBar menu;


    ArrayList<Slova> slovalist;
    Slova slova;
    String[] header = new String[] { //Название столбцов в форме
            "ID",
            "Название",
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


    public static void main(String[] args) throws Exception {

        SlovarViev app = new SlovarViev();
        app.checkTables();
        app.menu_interface();

    }

    private void menu_interface() {
        JFrame frame = new JFrame("Словарь Рун");
        frame.setContentPane(new SlovarViev().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Font font = new Font("Arial", Font.PLAIN, 15);

        JMenuBar menuBar = new JMenuBar();

        JMenu Menu = new JMenu("Меню");
        Menu.setFont(font);

        JMenu newMenu = new JMenu("Добавить/Изменить");
        newMenu.setFont(font);
        Menu.add(newMenu);

        JMenuItem add_slov = new JMenuItem("Слова");
        add_slov.setFont(font);
        newMenu.add(add_slov);

        JMenuItem add_run = new JMenuItem("Руны");
        add_run.setFont(font);
        newMenu.add(add_run);

//        JMenuItem openItem = new JMenuItem("Open");
//        openItem.setFont(font);
//        fileMenu.add(openItem);
//
//        JMenuItem closeItem = new JMenuItem("Close");
//        closeItem.setFont(font);
//        fileMenu.add(closeItem);

        Menu.addSeparator(); // Разделитель

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.setFont(font);
        Menu.add(exitItem);


        JMenu setting = new JMenu("Настройки");
        setting.setFont(font);

        JMenuItem admin = new JMenuItem("Администрирование");
        admin.setFont(font);
        setting.add(admin);


        menuBar.add(Menu);
        menuBar.add(setting);

        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add_slov.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Добавление Слов");
                frame.setContentPane(new CrudSlov().mainPanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });

        add_run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("Добавление Рун");
                MainForm mainForm = new MainForm();
                frame.setContentPane(new MainForm().panelMain);
                frame.pack();
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);

            }
        });

        setting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

    }


    static Connection conn;
    ResultSet rs;
    int row, col;



    
    public void connect(){
        try {
            //Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:identifier.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Connect Success! From SlovarViev");
        }
        catch (SQLException ex){

            System.out.println(ex.getMessage());
        }

    }
    private void checkTables() {
        System.out.println("Check table");
        String sql = "CREATE TABLE IF NOT EXISTS slova (" +
                "	id_slov integer PRIMARY KEY AUTOINCREMENT," +
                "	slovo varchar(32) NOT NULL," +
                "	run_slov varchar(64) NOT NULL," +
                "	descr_slov varchar(256) NOT NULL," +
                "	dop varchar(256)," +
                "	low_slov varchar(32) NOT NULL" +
                ");";
        //String sql_start = "insert into slova (`slovo`, `run_slov`,`descr_slov`,`low_slov`) SELECT  * FROM (VALUES ('База данных пуста','-','-','-')) as t(`slovo`, `run_slov`,`descr_slov`,`low_slov`) WHERE NOT EXISTS(SELECT * FROM slova)";
        //String sql_start = "insert IGNORE into slova set `slovo` = 'BD EMPTY', `run_slov`='-',`descr_slov`='-',`low_slov`='-' ";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            //stmt.executeUpdate(sql_start);
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    public SlovarViev() {
        connect();
        try {
           // loadData();
            table_load();
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                    String txt_run = table1.getValueAt(row, 2).toString();

                    //Подсчет кол-ва рун
                    char ch = '_';
                    int count = txt_run.length() - txt_run.replace(String.valueOf(ch), "").length() +1;
                    System.out.println("Кол-во Рун = " + count);
                    //Создание массива рун
                    String[] runs  = txt_run.split("_");

                    String[][] spisok_run = new String[count][2];
                    //
                    String zapis="";

                    for (int i=0; i<count; i++){
                        System.out.println("Руна: " + runs[i]);
                        String sql ="select description_run from runs where name_run = '" + runs[i] + "' or name_run_low = '" + runs[i] + "'";
                        System.out.println("SQl: "+sql );
                        try {
                            Statement stmt = conn.createStatement();
                            rs = stmt.executeQuery(sql);
                            String desc_run = rs.getString("description_run");
                            System.out.println("Значение: " + desc_run);
                            spisok_run[i][0]= runs[i].substring(0,1).toUpperCase()+runs[i].substring(1); //Первая буква руны всегда большая
                            spisok_run[i][1] = desc_run;

                            zapis += spisok_run[i][0] + " - "+ spisok_run[i][1] +"\n";

                        } catch (Exception err) {
                            System.out.println(err.getMessage());
                        }
                    };

                    System.out.println("Zapis: " + zapis);

                    String dop;

                    if(table1.getValueAt(row,4)!=null){
                        dop = "Дополнительно:"+ "\n" + table1.getValueAt(row,4)+ "\n\n";

                    } else dop ="";

                    String vivod = "Слово: " + "\n" + table1.getValueAt(row, 1).toString() + "\n\n"+
                            "Руны: " + "\n" + zapis + "\n" +
                            dop +
                            "Итого: " + "\n" + table1.getValueAt(row, 3).toString() + "\n";

                    textPane1.setEditable(false);

                    //ММММ СДЕЛАЛ ЖИРНЫМИ СЛОВА совсем просто
                    Style bold = textPane1.addStyle("bold", null);
                    StyleConstants.setBold(bold, true);

                    textPane1.setText(vivod);
                    textPane1.getStyledDocument().setCharacterAttributes(0, 6, bold, true);
                    int index1 = vivod.indexOf(" Слово:");
                    textPane1.getStyledDocument().setCharacterAttributes(index1, 5, bold, true);
                    int index2 = vivod.indexOf("Руны:");
                    textPane1.getStyledDocument().setCharacterAttributes(index2, 5, bold, true);
                    int index3 = vivod.indexOf("Итого:");
                    textPane1.getStyledDocument().setCharacterAttributes(index3, 6, bold, true);
                    if(dop!=""){
                        int index4 = vivod.indexOf("Дополнительно:");
                        textPane1.getStyledDocument().setCharacterAttributes(index4, 14, bold, true);
                    }

                }
            }
        });
    }

    private void loadData() throws SQLException {
        System.out.println("Load data");
        slovalist = new ArrayList<>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery("select * from slova");
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

    private void table_load() throws SQLException{


        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)table1.getDefaultRenderer(String.class);
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        table1.setModel(dtm);
        dtm.setColumnIdentifiers(header);



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


        table1.setRowHeight(22);



    }
    
}
