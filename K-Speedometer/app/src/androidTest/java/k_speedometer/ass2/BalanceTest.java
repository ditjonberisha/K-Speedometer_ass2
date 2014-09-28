package k_speedometer.ass2;

import android.test.ActivityUnitTestCase;

/**
 * Created by AEP-STUDENT on 9/28/2014.
 */
public class BalanceTest extends ActivityUnitTestCase<Balance> {

    public BalanceTest() {
        super(Balance.class);
    }

        public void testCheckbalance()  throws Exception {

        Balance objBalance = new Balance();

        //set ball position in center to see if method returns true
        objBalance.sensorX = 0;
        objBalance.sensorY = 0;
        objBalance.nrPixels = 75;
        objBalance.range = 24;

        boolean isTrue = objBalance.checkBalance();
        //perform test
        assertEquals(true, isTrue);
    }

    }
