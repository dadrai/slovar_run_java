import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
public class ArrayOfJTextField {
    JFrame frame;
    JPanel pane;
    JTextField fields[];


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
    ArrayOfJTextField (int count, String[] runs) {
        connect();
        frame = new JFrame("Добавление Рун");
        fields = new JTextField[count];
        Container pane2 = frame.getContentPane();
        pane2.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        for(int i = 0; i < fields.length; i++) {

            System.out.println("Руна: " + runs[i]);
            String sql ="select description_run from runs where name_run = '" + runs[i] + "' or name_run_low = '" + runs[i] + "'";
            System.out.println("SQl: "+sql );
            try {
                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(sql);
                String desc_run = rs.getString("description_run");
                System.out.println("Значение: " + desc_run);
                fields[i] = new JTextField();
//                if(!desc_run.isEmpty()){fields[i].setText(desc_run);}
//                else {fields[i].setText("1");}
                fields[i].setText(desc_run);

            } catch (Exception err) {
                System.out.println(err.getMessage());
                fields[i] = new JTextField();
                fields[i].setText(" ");
                //Добавить список i рун которых нужно будет добавить/передать
            }
            runs[i]= runs[i].substring(0,1).toUpperCase()+runs[i].substring(1); //Первая буква руны всегда большая
            pane = new JPanel(new FlowLayout());
            JLabel lable = new JLabel(runs[i] + " - ");
            lable.setName("label_run"+(i+1));
            fields[i].setPreferredSize(new Dimension(300,25));
            pane.add(lable);
            pane.add(fields[i]);
            //pane.setPreferredSize(new Dimension(600,400));

            frame.add(pane);
        }

        JButton addbut = new JButton();
        addbut.setText("Добавить");
        frame.add(addbut);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        addbut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TEST");

            }
        });

    }

    public static void main (String args[]) {
        //new ArrayOfJTextField(3);
    }
}