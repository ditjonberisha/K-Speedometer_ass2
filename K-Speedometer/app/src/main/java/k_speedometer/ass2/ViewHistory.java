package k_speedometer.ass2;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.sql.SQLException;

/*
    Created by Arnold, Ditjon and Blend
 */
public class ViewHistory extends Activity implements View.OnClickListener {
    private TextView data;
    private Button btnDelete;
    private SQLite objSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_speed);
        Initialize();
        objSQL = new SQLite(this);

        try {
            //open connection with database
            objSQL.Open();
            //get all data from database via getAllData method in SQLite class
            data.setText(objSQL.getAll());

        } catch (SQLException e) {
            Dialog d = new Dialog(this);
            d.setTitle("Error!");
            TextView tv = new TextView(this);
            tv.setText("Failed to get data from Database");
        } finally {

            objSQL.Close();
        }

    }


    private void Initialize() {

        data = (TextView) findViewById(R.id.txtData);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // on "Clear history" button click
        try {
            objSQL.Open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        objSQL.Delete();
        objSQL.Close();
        data.setText("Database is empty!");
    }
}

