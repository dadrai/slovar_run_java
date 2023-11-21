import javax.swing.*;
import javax.swing.text.View;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrudSlov {
    JPanel mainPanel;
    private JTextField textsearch;
    private JButton search;
    private JTable table1;
    private JTextField txtruns;
    private JTextField txtslovo;
    private JButton generate;
    private JPanel genpanel;


    public static void main(String[] args) throws Exception {

        CrudSlov app =new CrudSlov();
        app.menu_interface();

    }
    private void menu_interface(){
        JFrame frame = new JFrame("Добавление Слов");
        frame.setContentPane(new CrudSlov().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public CrudSlov() {


        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String textruns = txtruns.getText();
                //Подсчет кол-ва рун
                char ch = '_';
                if(textruns.endsWith("_")){
                    textruns= textruns.substring(0,textruns.length()-1);
                }
                int count = textruns.length() - textruns.replace(String.valueOf(ch), "").length() +1;
                System.out.println("Кол-во Рун = " + count);
                //Создание массива рун
                String[] runs  = textruns.split("_");
                String full_slovo="";
                for (int i=0; i<count; i++){
                    full_slovo+=runs[i].toLowerCase();
                }
                if (!txtruns.getText().isEmpty()){
                new ArrayOfJTextField(count, runs);}



                full_slovo= full_slovo.substring(0,1).toUpperCase()+full_slovo.substring(1);
                txtslovo.setText(full_slovo);

            }
        });
    }
}
