
package ferry.implmenetation;

import ferry.contract.FerryContract;
import ferry.contract.test.FerryContractHolder;
import ferry.dto.AbstractAccount;
import ferry.dto.AccountDetail;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class Main {
    FerryContract ferryManager = lookupFerryManagerRemote();
    
    public static void main(String[] args) {
        try {
            FerryContractHolder.contract = lookupFerryManagerRemote();
            AccountDetail ad = new AccountDetail(22, "1234561234", "password", "Test1", "test@test.dk", "DK");
            //FerryContractHolder.contract.makeAccount(ad);
            //AccountDetail showAccount = FerryContractHolder.contract.showAccount(new AbstractAccount(1) {});
            System.out.println("showAccount.getName()");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static FerryContract lookupFerryManagerRemote() {
        try {
            Context c = new InitialContext();
            return (FerryContract) c.lookup("java:global/ferry_ls-FerryMavenBackend_ejb_1.0-SNAPSHOT/FerryManager!ferry.contract.FerryContract");
        } catch (NamingException ne) {
            //Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
