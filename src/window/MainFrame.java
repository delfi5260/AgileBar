package window;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {
    int row = 0;
    JOptionPane infoMessage;
    JDialog infoWindow ;
    public MainFrame(int w, int h, int row, int col) {
        setTitle("Система лояльность");
        setSize(w,h);
        setVisible(true);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addContent(this.getContentPane(),row,col);
    }

    private void addContent(Container pane,int row, int col) {
/*  Таблица ***************/
        this.row = row;
        Vector<String> columnName = new Vector<>(Arrays.asList("Номер","Имя","Суммарный счёт","Дата обновления","История"));
        DefaultTableModel model = new DefaultTableModel(columnName,0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50,20,1000,400);
        this.add(scrollPane);

        JButton buttonShowAll = new JButton("Показать все");
        buttonShowAll.setBounds(1100,30,120,40);
        pane.add(buttonShowAll);

/*  Блок добавления записи ***************/
        JLabel labelName = new JLabel("Имя");
        labelName.setBounds(105,420,150,40);
        pane.add(labelName);
        JTextField textName = createJTexField("Введите имя",1);
        textName.setBounds(100,450,150,40);
        pane.add(textName);

        JLabel labelNumber = new JLabel("Номер телефона");
        labelNumber.setBounds(305,420,150,40);
        pane.add(labelNumber);
        JTextField textNumber = createJTexField(null,0);
        textNumber.setBounds(300,450,150,40);
        pane.add(textNumber);

        JLabel labelCount = new JLabel("Счёт");
        labelCount.setBounds(505,420,150,40);
        pane.add(labelCount);
        JTextField textCount = createJTexField(null,0);
        textCount.setBounds(500,450,150,40);
        pane.add(textCount);

        JButton buttonAdd = new JButton("Добавить нового");
        buttonAdd.setBounds(700,450,150,40);
        pane.add(buttonAdd);

/*  Поиск ***************/
        JLabel labelNumberForSearch = new JLabel("Последние 4 цифры телефона");
        labelNumberForSearch.setBounds(105,485,200,40);
        pane.add(labelNumberForSearch);
        JTextField textNumberForSearch = createJTexField(null,0);
        textNumberForSearch.setBounds(100,515,150,40);
        pane.add(textNumberForSearch);

        JButton buttonSearch = new JButton("Найти");
        buttonSearch.setBounds(250,515,100,40);
        pane.add(buttonSearch);

        JComboBox<String> boxSearch = new JComboBox<>();
        boxSearch.setBounds(100,550,450,40);
        boxSearch.setEditable(false);
        boxSearch.setVisible(true);
        pane.add(boxSearch);

        JLabel labelDisc = new JLabel("Скидка");
        labelDisc.setBounds(560,550,100,40);
        pane.add(labelDisc);

        JLabel labelCountForSearch = new JLabel("Сумма счёта");
        labelCountForSearch.setBounds(555,580,150,40);
        pane.add(labelCountForSearch);
        labelCountForSearch.setVisible(false);

        JTextField textCountForSearch = createJTexField(null,0);
        textCountForSearch.setBounds(550,610,150,40);
        pane.add(textCountForSearch);
        textCountForSearch.setVisible(false);

        JButton buttonSearchAdd = new JButton("Добавить счет");
        buttonSearchAdd.setBounds(700,610,130,40);
        pane.add(buttonSearchAdd);
        buttonSearchAdd.setVisible(false);

/*  Окно info ***************/
        infoMessage = new JOptionPane("ERROR",JOptionPane.WARNING_MESSAGE);
        infoWindow =infoMessage.createDialog("ОЙ БОЙ!");
        infoWindow.setBounds((pane.getSize().width)/2-200,(pane.getSize().height)/2-100,400,200);

//        textNumber.addCaretListener(new CaretListener() {
//            @Override
//            public void caretUpdate(CaretEvent e) {
//                if (ft.getText().matches(".*[a-z].*"))
//                    textNumber.setText("not found");
//                else
//                    textNumber.setText("found");
//            }
//        });

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
                        try {
                        model.insertRow(i, new String[]{s.split(";")[0], s.split(";")[1], s.split(";")[2],s.split(";")[3],s.split(";")[4]});
                        i++;
                            s=scanner.nextLine();
                        }catch (ArrayIndexOutOfBoundsException ex){
                            triggerInfoMessage("Ошибка при чтении Базы! \n Сообщить об ошибке одному из Дань!",JOptionPane.ERROR_MESSAGE);
                            s=scanner.nextLine();
                        }
                        catch (Exception exception){
                            break;
                        }
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    triggerInfoMessage("Ошибка при чтении Базы! \n Сообщить об ошибке одному из Дань!",JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textName.getText().isEmpty() && !textNumber.getText().isEmpty() && !textCount.getText().isEmpty()){
                    if (textNumber.getText().length()==11) {
                        try {
                            if (searchClient(textNumber.getText()) == -1) {
                                FileWriter fw = new FileWriter("src/db/base", true);
                                Calendar calendar = new GregorianCalendar();
                                String date = calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH)+1) + "." + calendar.get(Calendar.YEAR);
                                fw.write(textNumber.getText() + ";"
                                        + textName.getText() + ";"
                                        + textCount.getText() + ";"
                                        + date + ";"
                                        + date + "-" + textCount.getText()
                                        + "\n");
                                fw.close();
                                model.insertRow(row, new String[]{textNumber.getText(), textName.getText(), textCount.getText(), date,date + "-" + textCount.getText()});
                                triggerInfoMessage("Гость добавлен", JOptionPane.INFORMATION_MESSAGE);
                                textName.setText("");
                                textNumber.setText("");
                                textCount.setText("");

                            } else {
                                triggerInfoMessage("Этот номер уже существует",JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (IOException fileNotFoundException) {
                            fileNotFoundException.printStackTrace();
                            triggerInfoMessage("Ошибка при чтении Базы! \n Сообщить об ошибке одному из Дань!",JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        triggerInfoMessage("Номер телефона должен содержать 11 цифр",JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    triggerInfoMessage("Необходимо заполнить все поля",JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textNumberForSearch.getText().isEmpty()){
                    try {

                        ArrayList<String> dictClient= searchByLast4numbers(textNumberForSearch.getText());
                        boxSearch.removeAllItems();
                        if(!dictClient.isEmpty()){
                            labelDisc.setText("Найдено: " + dictClient.size());
                            for (String st :dictClient){
                                boxSearch.addItem(st+" Скидка:"+searchDisc(Integer.parseInt(st.split(";")[0]))+"%");
                            }
                            textCountForSearch.setVisible(true);
                            buttonSearchAdd.setVisible(true);
                            labelCountForSearch.setVisible(true);
                        }else{
                            labelDisc.setText("Не найдено");
                            buttonSearchAdd.setVisible(false);
                            textCountForSearch.setVisible(false);
                            labelCountForSearch.setVisible(false);
                        }
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                        triggerInfoMessage("Ошибка при чтении Базы! \n Сообщить об ошибке одному из Дань!",JOptionPane.ERROR_MESSAGE);

                    }
                }
            }
        });

        buttonSearchAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!textNumberForSearch.getText().isEmpty() && !textCountForSearch.getText().isEmpty()) {
                    try {
                        String st = (String) boxSearch.getSelectedItem();
                        int numberString = Integer.parseInt(st.split(";")[0].trim());
                        rewriteBase(numberString,Integer.parseInt(textCountForSearch.getText()));
                        buttonSearchAdd.setVisible(false);
                        textCountForSearch.setVisible(false);
                        labelCountForSearch.setVisible(false);
                        labelDisc.setText("Добавлено");
                        boxSearch.removeAllItems();
                        textCountForSearch.setText("");
                        triggerInfoMessage("Общий счет обновлен",JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                        triggerInfoMessage("Ошибка при чтении Базы! \n Сообщить об ошибке одному из Дань!",JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    triggerInfoMessage("Необходимо заполнить сумму счёта",JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public  JTextField createJTexField (String text,int type){
        JTextField jTextField = new JTextField(text);
        PlainDocument doc = (PlainDocument) jTextField.getDocument();
        doc.setDocumentFilter(new DigitFilter(type));

        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ( jTextField.getText().equals(text)){
                    jTextField.setText("");
//                    super.focusGained(e);
                }
            }
//            @Override
//            public void focusLost(FocusEvent e) {
//                if ( jTextField.getText().equals("")){
//                    jTextField.setText(text);
////                    super.focusLost(e);
//                }
//            }
        });
        return jTextField;
    }

    public void triggerInfoMessage(String strInginfoMessage, int type) {
        infoMessage.setMessage(strInginfoMessage);
        infoMessage.setMessageType(type);
        infoWindow.setVisible(true);
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

    public ArrayList<String> searchByLast4numbers(String number) throws FileNotFoundException {
        ArrayList<String> dictClient = new ArrayList<>();
        String s;
        Scanner scanner = new Scanner(new File("src/db/base"));
        s=scanner.nextLine();
        int i=0;
        while(!s.isEmpty()){
            try {
                String numberDB = s.split(";")[0];
                if(numberDB.substring(numberDB.length()-4).equals(number)){
                    dictClient.add(i+";"+s.split(";")[0]+";"+s.split(";")[1]+";"+s.split(";")[2]);
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
        String data = calendar.get(Calendar.DAY_OF_MONTH)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.YEAR);
        s=s.split(";")[0]+";"+s.split(";")[1]+";"+count+";"+data+";"+s.split(";")[4]+"@"+data+"-"+sum+"\n";
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
