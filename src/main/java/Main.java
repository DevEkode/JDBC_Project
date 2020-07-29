import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws SQLException, IOException {
        BDD bdd = new BDD();
        try{
            // Connect and start transaction
            bdd.connect();

            // Insert some data
            bdd.insertData("Alexis","POUPELIN",Date.valueOf(LocalDate.now()));
            bdd.insertData("Jean","MICHEL",Date.valueOf(LocalDate.now()));
            bdd.insertData("Robert","MACHIN",Date.valueOf(LocalDate.now()));

            // Print all first names in database
            ArrayList<String> firstNameList = bdd.getFirstNames();
            for(String s : firstNameList){
                logger.info(s);
            }

            // Update and delete lines
            bdd.updateLine(2,"Jean","MARTIN",Date.valueOf(LocalDate.now()));
            bdd.deleteLine(3);

            // Re-Print all first names in database
            firstNameList = bdd.getFirstNames();
            for(String s : firstNameList){
                logger.info(s);
            }

            // Commit if not exceptions
            bdd.commit();
            bdd.close();
        }catch(Exception e){
            // Rollback if one exception is throwed
            bdd.rollback();
            logger.error(e.getMessage());
        }


    }
}
