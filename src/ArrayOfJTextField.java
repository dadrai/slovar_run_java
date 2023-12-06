import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class ArrayOfJTextField {
    JFrame frame;
    JPanel pane;
    //JTextField fields[];

    JTextArea[] areas;



    static Connection conn;
    ResultSet rs;
    int row, col;

    int[] i_runs;// Список индексов рун которые нужно добавить
    int i__=0;

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
    ArrayOfJTextField (int count, String[] runs) {
        frame = new JFrame("Добавление Рун");
        //fields = new JTextField[count];
        i_runs = new int[count]; // Список индексов рун которые нужно добавить
        JPanel pane1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel q = new JLabel("Введите Описание Рун");
        q.setFont(new Font("JetBrains Mono SemiBold",Font.BOLD, 18));
        // q.setHorizontalAlignment(SwingConstants.CENTER);
        pane1.add(q);
        frame.add(pane1);

        areas = new JTextArea[count];
        boolean nety_run = false;
        Container pane2 = frame.getContentPane();
        pane2.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        for(int i = 0; i < areas.length; i++) {
            boolean est_runa = false;
            connect();

            System.out.println("Руна: " + runs[i]);
            String sql ="select description_run from runs where name_run = '" + runs[i] + "' or name_run_low = '" + runs[i] + "'";
            System.out.println("SQl: "+sql );
            try {
                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                String desc_run = rs.getString("description_run");
                System.out.println("Значение: " + desc_run);


                est_runa =true;
                areas[i] = new JTextArea();
                areas[i].setText(desc_run);
                //areas[i].setEditable(false);


            } catch (Exception err) {
                System.out.println(err.getMessage());
                nety_run = true;
                areas[i] = new JTextArea();
                areas[i].setText("");
                //Добавить список i рун которых нужно будет добавить/передать
                i_runs[i__]=i;
                i__++;

            }finally {
                try { rs.close(); } catch (Exception err) { /* Ignored */ }
                // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                try { conn.close(); } catch (Exception err) { /* Ignored */ }
            }

            runs[i]= runs[i].substring(0,1).toUpperCase()+runs[i].substring(1); //Первая буква руны всегда большая
            pane = new JPanel(new FlowLayout());
            JLabel lable = new JLabel(runs[i] + " - ");
            lable.setName("label_run"+(i));
            lable.setFont(new Font("JetBrains Mono SemiBold",Font.BOLD, 18));
            areas[i].setMaximumSize(new Dimension(700,500));
            areas[i].setPreferredSize(new Dimension(700,150));
            areas[i].setFont(new Font("JetBrains Mono SemiBold",Font.PLAIN, 18));
            areas[i].setLineWrap(true);
            if(est_runa){
                areas[i].setVisible(false);
                lable.setVisible(false);
            }
            pane.add(lable);
            pane.add(areas[i]);

            frame.add(pane);
        }

        pane2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addbut = new JButton();
        addbut.setText("Добавить");
        pane2.add(addbut);
        JButton otmena = new JButton();
        otmena.setText("Отмена");
        pane2.add(otmena);

        frame.add(pane2);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        if(!nety_run){
            frame.dispose();
            JOptionPane.showMessageDialog(frame, "Все Руны уже добавлены!", "Добавление Рун", JOptionPane.INFORMATION_MESSAGE);

        }

        addbut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Добавление Рун:");
                String[] new_run = new String[i__];
                boolean desc_run_empty = true;
                for(int i =0; i<i__;i++){
                    new_run[i]=areas[i_runs[i]].getText();
                    System.out.println("New Run: " + new_run[i]);
                    if(areas[i_runs[i]].getText().isEmpty())desc_run_empty=false;
                }
                if (!desc_run_empty) {
                    JOptionPane.showMessageDialog(frame, "Введены не все описания!", "Пустая строка", JOptionPane.INFORMATION_MESSAGE);
                    areas[i_runs[0]].requestFocus();
                } else {
                    for(int i =0; i<i__;i++){
                        System.out.println(runs[i_runs[i]]+ " : "+areas[i_runs[i]].getText());
                        connect();

                        String name_run, descr_run;
                        name_run =  runs[i_runs[i]];

                        descr_run = areas[i_runs[i]].getText();
                        String name_run_low = name_run.toLowerCase();



                        int result = JOptionPane.showConfirmDialog(frame, "Добавить Руну: " + name_run + "?", "Добавление",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if (result == JOptionPane.YES_OPTION) {
                            try {
                                System.out.println("Add Run - " + name_run);
                                Statement stmt = conn.createStatement();
                                stmt.executeUpdate("insert into runs (`name_run`, `description_run`,`name_run_low`) VALUES ('" +
                                        name_run + "','" + descr_run + "','" + name_run_low + "')");

                            } catch (Exception err) {
                                System.out.println(err.getMessage());
                            }finally {
                                try { rs.close(); } catch (Exception err) { /* Ignored */ }
                                // try { ps.close(); } catch (Exception e) { /* Ignored */ }
                                try { conn.close(); } catch (Exception err) { /* Ignored */ }
                            }
                        }
                    }
                    frame.dispose(); //Закрыть после добавления
                }
            }
        });

        otmena.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Close Spisok add run");
                frame.dispose();

            }
        });

    }

    public static void main (String[] args) {
        //new ArrayOfJTextField(3);
    }
}