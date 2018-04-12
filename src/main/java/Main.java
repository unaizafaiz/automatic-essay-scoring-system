import javax.swing.*;
import java.io.File;

public class Main extends JPanel {

    public static void main(String[] args){

        FileChooser fs = new FileChooser();
        File[] files = fs.getInput();

        Criteria criteria = new Criteria();
        criteria.findCriteriaAndScore(files);


    }
}
