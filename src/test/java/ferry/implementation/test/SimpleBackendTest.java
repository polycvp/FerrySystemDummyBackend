package ferry.implementation.test;

import ferry.contract.test.FerryAccountTest;
import ferry.contract.test.FerryContractHolder;
import ferry.contract.test.FerryReservationTest;
import ferry.implmenetation.FerryManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FerryAccountTest.class,FerryReservationTest.class})
public class SimpleBackendTest {
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        
        FerryContractHolder.contract = new FerryManager();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    
}
