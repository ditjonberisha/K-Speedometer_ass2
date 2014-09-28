package k_speedometer.ass2;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

/**
 * Created by AEP-STUDENT on 9/28/2014.
 */
public class MyActivityTest extends ActivityUnitTestCase<MyActivity> {

    public MyActivityTest() {
        super(MyActivity.class);
    }

    public  void testLatLongLabels() throws  Exception{
        Intent i = new Intent(getInstrumentation().getTargetContext(),MyActivity.class);
        startActivity(i,null,null);

        MyActivity act = getActivity();
        //check if "Lat" and "Long" textViews have null value
        TextView tv = (TextView) act.findViewById(R.id.textView);
        assertNotNull(tv);

        TextView tv1 = (TextView) act.findViewById(R.id.textView2);
        assertNotNull(tv1);

        //check if label text is "Lat:"
        String labelText  = tv.getText().toString();
        assertEquals("Lat:",labelText);

        //check if label text is "Long:"
        String labelText1 = tv1.getText().toString();
        assertEquals("Long:",labelText1);
    }


}

