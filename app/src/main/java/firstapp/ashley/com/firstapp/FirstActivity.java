package firstapp.ashley.com.firstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class FirstActivity extends Activity {
    public final static String USER_NAME = "com.ashley.firstapp.USER_NAME";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
        Intent intent = new Intent(this,MainScreenActivity.class);
        EditText userNameWidget = (EditText) findViewById(R.id.user_name);
        String userName = userNameWidget.getText().toString();
        EditText passwordWidget = (EditText) findViewById(R.id.password);
        String password = passwordWidget.getText().toString();
/*        if(!userName.isEmpty()) {

            if (!password.equals("1234")) {
                Toast.makeText(getApplicationContext(), "Wrong Credentials",
                        Toast.LENGTH_SHORT).show();
            } else {*/
                intent.putExtra(USER_NAME, userName);
                startActivity(intent);
/*            }
        }else{
            Toast.makeText(getApplicationContext(), "User Name cannot be empty",
                    Toast.LENGTH_SHORT).show();

        }*/

    }

}
