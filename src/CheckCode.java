import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CheckCode {

    public static void main(String[] args) throws IOException {
        FileWriter fw = new FileWriter("src/db/base",true);
        Calendar calendar = new GregorianCalendar();
        fw.append("89772806964"+";"+" Даня"+";"+"1000"+";"+"3.2.21"+"\n");
        fw.close();
    }
}
