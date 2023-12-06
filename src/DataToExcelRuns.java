import java.sql.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataToExcelRuns {
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

        try {
            connect();
            Statement stmt = conn.createStatement();
            rs= stmt.executeQuery("SELECT `name_run`, `description_run` FROM runs ORDER BY `name_run`");
            while (rs.next()){
                String name_run = rs.getString("name_run");
                String description_run= rs.getString("description_run");


                model.addRow(new Object[]{
                        name_run, description_run
                });
                jt.setModel(model);
            }
        } catch (SQLException err){
            System.out.println(err.getMessage());
        }finally {
            try { rs.close(); } catch (Exception err) { /* Ignored */ }
            // try { ps.close(); } catch (Exception e) { /* Ignored */ }
            try { conn.close(); } catch (Exception err) { /* Ignored */ }
        }
    }

    public void openFile(String file){
        try {
            File path = new File(file);
            Desktop.getDesktop().open(path);

        }catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }

    public DataToExcelRuns() {
        connect();
        fillDataTable();


        try {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.showSaveDialog(jt);
            File saveFile = jFileChooser.getSelectedFile();
            if(saveFile!=null){
                saveFile = new File(saveFile.toString()+".xlsx");
                Workbook wb = new XSSFWorkbook();
                Sheet sheet = wb.createSheet("Список Рун");
                Row rowCol = sheet.createRow(0);
                for(int i=0;i<jt.getColumnCount();i++){
                    Cell cell = rowCol.createCell(i);
                    cell.setCellValue(jt.getColumnName(i));
                }
                for (int j=0;j<jt.getRowCount();j++){
                    Row row = sheet.createRow(j+1);
                    for (int k =0; k<jt.getColumnCount();k++){
                        Cell cell = row.createCell(k);
                        if(jt.getValueAt(j,k)!=null){
                            cell.setCellValue(jt.getValueAt(j,k).toString());
                        }
                    }
                }

                FileOutputStream out = new FileOutputStream(new File(saveFile.toString()));
                wb.write(out);
                wb.close();
                out.close();
                openFile(saveFile.toString());

            }else {
                JOptionPane.showMessageDialog(null, "Ошибка!");
            }

        }catch (FileNotFoundException err){
            System.out.println(err.getMessage());
        }
        catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }

    }
}
