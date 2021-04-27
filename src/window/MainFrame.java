package window;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class MainFrame extends JFrame {
    int row = 0;

    public MainFrame(int w, int h, int row, int col) throws FileNotFoundException {
        setSize(w,h);
        setVisible(true);
        setLayout(null);
        addContent(this.getContentPane(),row,col);

    }

    private void addContent(Container pane,int row, int col) throws FileNotFoundException {
        this.row = row;
        JTextArea textArea = new JTextArea();
        DefaultTableModel model = new DefaultTableModel(row,col);
        JTable table = new JTable(model);


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50,20,1000,400);
        this.add(scrollPane);
        JButton buttonShowAll = new JButton("Показать все");
        buttonShowAll.setBounds(1100,30,100,40);
        pane.add(buttonShowAll);
        JTextField textName = new JTextField("Введите имя");
        JTextField textNumber = new JTextField("Введите номер");
        JTextField textCount = new JTextField("Введите счет");
        textName.setBounds(100,500,150,40);
        textNumber.setBounds(300,500,150,40);
        textCount.setBounds(500,500,150,40);
        pane.add(textName);
        pane.add(textNumber);
        pane.add(textCount);
        JButton buttonAdd = new JButton("Добавить нового");
        buttonAdd.setBounds(700,500,130,40);
        pane.add(buttonAdd);

        JTextField textNumberForSearch = new JTextField("Введите номер");
        JTextField textCountForSearch = new JTextField("Сумма счета");
        JLabel labelDisc = new JLabel("Скидка");
        textNumberForSearch.setBounds(100,600,150,40);
        textCountForSearch.setBounds(500,600,150,40);
        labelDisc.setBounds(420,600,100,40);
        pane.add(textNumberForSearch);
        pane.add(textCountForSearch);
        pane.add(labelDisc);
        JButton buttonSearch = new JButton("Найти");
        JButton buttonSearchAdd = new JButton("Добавить счет");
        buttonSearch.setBounds(300,600,100,40);
        buttonSearchAdd.setBounds(700,600,130,40);
        pane.add(buttonSearch);
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
                        numberString=searchClient(textNumberForSearch.getText());
                        if(numberString!=-1){
                            labelDisc.setText(searchDisc(numberString)+"%");
                            textCountForSearch.setVisible(true);
                            buttonSearchAdd.setVisible(true);
                        }else{
                            labelDisc.setText("Не найдено");
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
