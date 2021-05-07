package window;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

public class MainFrame extends JFrame {
    int row = 0;

    public MainFrame(int w, int h, int row, int col) {
        setTitle("Система лояльность");
        setSize(w,h);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addContent(this.getContentPane(),row,col);
    }

    private void addContent(Container pane,int row, int col) {
        this.row = row;
        Vector<String> columnName = new Vector<>(Arrays.asList("Номер","Имя","Суммарный счёт","Дата обновления"));
        DefaultTableModel model = new DefaultTableModel(columnName,0);
        JTable table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50,20,1000,400);
        this.add(scrollPane);
        JButton buttonShowAll = new JButton("Показать все");
        buttonShowAll.setBounds(1100,30,100,40);
        pane.add(buttonShowAll);
        JTextField textName = createJTexField("Введите имя");
        JTextField textNumber = createJTexField("Введите номер");
        JTextField textCount = createJTexField("Введите счет");
        textName.setBounds(100,450,150,40);
        textNumber.setBounds(300,450,150,40);
        textCount.setBounds(500,450,150,40);
        pane.add(textName);
        pane.add(textNumber);
        pane.add(textCount);
        JButton buttonAdd = new JButton("Добавить нового");
        buttonAdd.setBounds(700,450,130,40);
        pane.add(buttonAdd);
        //        Поиск
        JTextField textNumberForSearch = createJTexField("Введите номер");
        JButton buttonSearch = new JButton("Найти");
        buttonSearch.setBounds(300,515,100,40);
        pane.add(buttonSearch);
        JTextField textCountForSearch = createJTexField("Сумма счёта");
        JLabel labelDisc = new JLabel("Скидка");
        textNumberForSearch.setBounds(100,515,150,40);
        textCountForSearch.setBounds(550,585,150,40);
        labelDisc.setBounds(560,550,100,40);
        pane.add(textNumberForSearch);
        pane.add(textCountForSearch);
        pane.add(labelDisc);

        JComboBox<String> boxSearch = new JComboBox<>();

        boxSearch.setBounds(100,550,450,40);
        boxSearch.setEditable(false);
        boxSearch.setVisible(true);
        pane.add(boxSearch);

        JButton buttonSearchAdd = new JButton("Добавить счет");
        buttonSearchAdd.setBounds(700,585,130,40);
        pane.add(buttonSearchAdd);
        buttonSearchAdd.setVisible(false);
        textCountForSearch.setVisible(false);


        buttonShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = "";
                try {
                    model.setRowCount(0);
                    Scanner scanner = new Scanner(new File("src/db/base"));
                    s=scanner.nextLine();
                    int i=0;
                    while (!s.isEmpty()){
                        model.insertRow(i, new String[]{s.split(";")[0], s.split(";")[1], s.split(";")[2],s.split(";")[3]});
                        i++;
                        try {
                            s=scanner.nextLine();
                        }catch (Exception exception){
                            break;
                        }
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textName.getText().isEmpty() && !textNumber.getText().isEmpty() && !textCount.getText().isEmpty()){
                    try {
                        if(searchClient(textNumber.getText())==-1){
                            FileWriter fw = new FileWriter("src/db/base",true);
                            Calendar calendar = new GregorianCalendar();
                            fw.write(textNumber.getText()+";"+textName.getText()+";"+textCount.getText()+";"+calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR)+"\n");
                            fw.close();
                            model.insertRow(row,new String[]{textNumber.getText(),textName.getText(),textCount.getText(),calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR)});
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textNumberForSearch.getText().isEmpty()){
                    try {
                        int numberString=0;
                        ArrayList<String> dictClient=searchClientForDisc(textNumberForSearch.getText());
                        boxSearch.removeAllItems();
                        if(!dictClient.isEmpty()){
                            for (String st :dictClient){
                                boxSearch.addItem(st+" Скидка:"+searchDisc(Integer.parseInt(st.split(";")[0]))+"%");
                            }
                            textCountForSearch.setVisible(true);
                            buttonSearchAdd.setVisible(true);
                        }else{
                            labelDisc.setText("Не найдено");
                            buttonSearchAdd.setVisible(false);
                            textCountForSearch.setVisible(false);
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }
                }
            }
        });

        buttonSearchAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textNumberForSearch.getText().isEmpty()) {
                    try {
                        int numberString = 0;
                        numberString = searchClient(textNumberForSearch.getText());
                        if (numberString != -1) {
                            rewriteBase(numberString,Integer.parseInt(textCountForSearch.getText()));
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    public  JTextField createJTexField (String text){
        JTextField jTextField = new JTextField(text);
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {

                if ( jTextField.getText().equals(text)){
                    jTextField.setText(null);
//                    super.focusGained(e);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if ( jTextField.getText().equals("")){
                    jTextField.setText(text);
//                    super.focusLost(e);
                }
            }
        });
        return jTextField;
    }

    public int searchClient(String number) throws FileNotFoundException {
        String s;
        Scanner scanner = new Scanner(new File("src/db/base"));
        s=scanner.nextLine();
        int i=0;
        while(true){
            try {
                if(s.split(";")[0].equals(number)){
                    System.out.println("Этот номер уже существует");
                    return i;
                }
                i++;
                s=scanner.nextLine();
            }catch (Exception exception){
                return -1;
            }
        }
    }

    public ArrayList<String> searchClientForDisc (String number) throws FileNotFoundException {
        ArrayList<String> dictClient = new ArrayList<>();
        String s;
        Scanner scanner = new Scanner(new File("src/db/base"));
        s=scanner.nextLine();
        int i=0;
        while(!s.isEmpty()){
            try {
                String ass = s.split(";")[0];
                if(ass.substring(ass.length()-4).equals(number)){
                    dictClient.add(i+";"+s.replace(';',' '));
                }
                i++;
                s=scanner.nextLine();
            }catch (Exception exception){
                break;
            }
        }
        return dictClient;
    }

    public int searchDisc(int numberString) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("src/db/base"));
        for(int i=0;i<numberString;i++){
            scanner.nextLine();
        }
        String s = scanner.nextLine();
        if(Integer.parseInt(s.split(";")[2])>=5000 && (Integer.parseInt(s.split(";")[2])<20000)){
            return 5;
        }else if(Integer.parseInt(s.split(";")[2])>20000 && (Integer.parseInt(s.split(";")[2])<80000)){
            return 10;
        }else if(Integer.parseInt(s.split(";")[2])>=80000 && (Integer.parseInt(s.split(";")[2])<150000)){
            return 15;
        }else if(Integer.parseInt(s.split(";")[2])>=150000){
            return 20;
        }else {
            return 0;
        }
    }

    public void rewriteBase(int numberString, int sum) throws IOException {
        copyFileUsingChannel(new File("src/db/base"),new File("src/db/baseTmp"));
        Scanner scanner = new Scanner(new File("src/db/baseTmp"));
        FileWriter fw = new FileWriter("src/db/base");
        String tmp;
        for(int i=0;i<numberString;i++){
            tmp=scanner.nextLine();
            fw.write(tmp+"\n");
        }
        String s = scanner.nextLine();
        int count = sum +Integer.parseInt(s.split(";")[2]);
        Calendar calendar = new GregorianCalendar();
        s=s.split(";")[0]+";"+s.split(";")[1]+";"+count+";"+calendar.get(Calendar.DAY_OF_MONTH)+"."+calendar.get(Calendar.MONTH)+"."+calendar.get(Calendar.YEAR)+"\n";
        fw.write(s);
        while(true){
            try {
                tmp=scanner.nextLine();
                fw.write(tmp+"\n");
            }catch (Exception e){
                fw.close();
                break;
            }
        }
    }

    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }
}
