package com.andrewgrosner.acela.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.andrewgrosner.acela.app.model.Team;
import com.andrewgrosner.acela.app.parser.BaseHandler;
import com.andrewgrosner.acela.app.parser.LoganSquareHandler;
import com.andrewgrosner.acela.app.parser.ParserHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.beginTestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            logTime(System.currentTimeMillis(), new LoganSquareHandler<Team>());

                            logTime(System.currentTimeMillis(), new ParserHandler<Team>());

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                };
                thread.start();
            }
        });
    }

    private void logTime(long startTime, BaseHandler<Team> baseHandler) throws IOException {

        InputStream inputStream = getAssets().open("teams.json");

        Log.e(getClass().getSimpleName(), "Starting parse for: " + baseHandler.getName());
        List<Team> teams = baseHandler.parseData(Team.class, inputStream);
        Log.e(getClass().getSimpleName(),
              "Finished " + baseHandler.getName() + ":" + (System.currentTimeMillis() - startTime));
        validateParse(teams);

        startTime = System.currentTimeMillis();
        String jsonData = baseHandler.serializeData(Team.class, teams);
        Log.e(getClass().getSimpleName(),
              "Finished " + baseHandler.getName() + ":" + (System.currentTimeMillis() - startTime));
        Log.e(getClass().getSimpleName(), "Serialized:" + jsonData);

    }

    private void validateParse(List<Team> teams) {
        Log.d(getClass().getSimpleName(), "Team Size: " + teams.size());
        assertTrue(teams.size() == 30);

        for (Team team : teams) {
            assertTrue(!TextUtils.isEmpty(team.getName()));
            assertTrue(team.getRoster().size() > 0);
            assertTrue(!TextUtils.isEmpty(team.getType()));
        }
    }

    private static void assertTrue(boolean truth) {
        if (!truth) {
            throw new RuntimeException("Assertion failed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
