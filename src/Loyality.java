import window.MainFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Loyality {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(new File("src/db/base"));
        int row = 0, col=0;
        String s = scanner.nextLine();
        col=s.split(";").length-2;
        while(!s.isEmpty()){
            try {
                row++;
                s=scanner.nextLine();
            }catch (Exception e){
                break;
            }
        }

        MainFrame mainFrame = new MainFrame(1366,768,row,col);
        mainFrame.repaint();


    }
}
