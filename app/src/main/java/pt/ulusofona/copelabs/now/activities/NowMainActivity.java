package pt.ulusofona.copelabs.now.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.copelabs.now.R;

import net.named_data.jndn.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import pt.ulusofona.copelabs.now.adapters.HorizontalAdapterHolder;
import pt.ulusofona.copelabs.now.adapters.MessageArrayAdapter;
import pt.ulusofona.copelabs.now.helpers.Utils;
import pt.ulusofona.copelabs.now.models.Message;
import pt.ulusofona.copelabs.now.models.User;
import pt.ulusofona.copelabs.now.ndn.ChronoSync;
import pt.ulusofona.copelabs.now.ndn.NDNParameters;

/**
 * This class is part of Now@ application. It extends to AppCompactActivity.
 * It provides a user interface to use Now@. The principal components are a ListView
 * where is displayed all post that user exchanges and a Holder with categories that user
 * can selected.
 * From this class is started ChonoSync, principal component of this application.
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:05 PM
 */

public class NowMainActivity extends AppCompatActivity implements Observer,
        NowMainActivityInterface {

    static final String USER = "user";
    private TextView mUserNameTextView;
    private String TAG = NowMainActivity.class.getSimpleName();
    private List<Message> mMessagesList = new ArrayList<>();
    private List<ChronoSync> mChronosyncs = new ArrayList<>();
    private MessageArrayAdapter mMessagesListAdapter;
    private String interestSelected = null;
    private Spinner mInterestsSpinner;
    private ArrayAdapter<String> adapter;
    private List<String> mInterestsSelected = new ArrayList<>();
    private List<String> mInteresSubscribed = new ArrayList<>();
    private List<String> mPrefixes = new ArrayList<>();
    private Face mFace;
    private User mUser;
    private ChronoSync ChronoSync;
    private EditText mMessageEditText;
    private Map<String, ChronoSync> mChonoSyncMap = new HashMap();
    private NDNParameters mNDNParmiters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_main);


        initialConfiguration();


    }

    /**
     * This method creates the first configuration when the app is started.
     * Here is set up the user interface, create a Face and Users information.
     */
    public void initialConfiguration() {

        //Create a new Face
        mFace = new Face("127.0.0.1");

        //Create a new User
        mUser = new User(Utils.generateRandomName());

        mMessageEditText = findViewById(R.id.messageEditText);

        mUserNameTextView = findViewById(R.id.userNameTextView);
        mUserNameTextView.setText("User : " + mUser.getName());

        //Set up adapter for messages
        mMessagesListAdapter = new MessageArrayAdapter(this, mMessagesList);
        mMessagesList.clear();

        //Start listview with messages
        ListView lstMessages = findViewById(R.id.messagesListView);
        lstMessages.setAdapter(mMessagesListAdapter);
        List<String> interestsList = Arrays.asList(getResources().getStringArray(R.array.interests));

        //Set up the RecyclerView with the interests
        final RecyclerView interestsRecyclerView = findViewById(R.id.interestsRecyclerView);
        final HorizontalAdapterHolder mHorizontalAdapterHolder = new HorizontalAdapterHolder(interestsList, this, this);
        interestsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        interestsRecyclerView.setAdapter(mHorizontalAdapterHolder);

        //Set button to send message
        ImageButton mBtnSend = findViewById(R.id.imageButton);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mInterestsSelected.isEmpty()) {
                    Log.d(TAG, "Please select an Interest");
                    Toast.makeText(getApplicationContext(), "Please select an Interest", Toast.LENGTH_SHORT).show();
                } else if (mMessageEditText.getText().toString().isEmpty()) {
                    Log.d(TAG, "Please write a message");
                    Toast.makeText(getApplicationContext(), "Please write a message", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder sb = new StringBuilder(interestSelected);
                    sb.deleteCharAt(0);
                    String interest = sb.toString();
                    jsonMessageConstructor(mUser, interest, mMessageEditText.getText().toString());
                    mMessageEditText.setText("");
                }

            }
        });

        //Set the spinner
        mInterestsSpinner = findViewById(R.id.interestsSpinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mInterestsSelected);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInterestsSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent,
                                               View v, int position, long id) {
                        interestSelected = parent.getItemAtPosition(position).toString();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.information:
                //Show Dialog with the prefixes subscribed
                Utils.showListPrefix(mPrefixes, this, "Prefixes list");
                return (true);

            case R.id.user_profile:
                //Show Dialog whit username information
                showUserName(mUser);
                return (true);

            case R.id.more:

                //Start NDN-Opp. If it does not install in the device, GoolePlay will be launched to
                //download it
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("pt.ulusofona.copelabs.ndn");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Log.d(TAG, "no existe");
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=pt.ulusofona.copelabs.now")));
                }
                return (true);

        }
        return (super.onOptionsItemSelected(item));
    }


    /**
     * This method show the user name and allows to change it.
     *
     * @param user User object
     */
    public void showUserName(final User user) {

        final AlertDialog.Builder textBox = new AlertDialog.Builder(this);
        textBox.setTitle("Now@ User Name");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setText(user.getName());
        textBox.setView(input);
        textBox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Store toast_message in JSON object
                user.setName(input.getText().toString());
                mUserNameTextView.setText("User: " + user.getName());
                Toast.makeText(getApplicationContext(), "Name was changed", Toast.LENGTH_SHORT).show();

            }
        });
        textBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                dialog.cancel();
            }
        });
        textBox.show();


    }

    /**
     * This method bild a JSON Object whit the information that will be sent
     *
     * @param user     User object
     * @param interest String interest of the message
     * @param message  String content of the message
     */
    public void jsonMessageConstructor(User user, String interest, String message) {
        final JSONObject jObject = new JSONObject();  // JSON object to store toast_message

        try {
            jObject.put("data", message);
            jObject.put("type", "text");
            jObject.put("user", user.getName());
            jObject.put("interest", interest);
            jObject.put("date", Utils.getDate());

            sendData(jObject.toString(), interest.toLowerCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Add message to list
        mMessagesList.add(new Message(user.getName(), message, interest, Utils.getDate()));

        //Notify changes
        mMessagesListAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();

    }

    /**
     * This method subscribes the interests on Chronosync
     *
     * @param interest String interest
     */
    public void subscribeInterest(String interest) {

        mNDNParmiters = new NDNParameters(mFace);
        mNDNParmiters.setUUID(UUID.randomUUID().toString());
        mNDNParmiters.setApplicationBroadcastPrefix("/ndn/multicast/now/" + interest);
        mNDNParmiters.setApplicationNamePrefix("/ndn/multicast/" + interest + "/" + mNDNParmiters.getUUID());

        mPrefixes.add(mNDNParmiters.getApplicationBroadcastPrefix());
        mPrefixes.add(mNDNParmiters.getmApplicationNamePrefix());


        ChronoSync = new ChronoSync(mNDNParmiters);
        ChronoSync.addObserver(this);

        mChronosyncs.add(ChronoSync);

        mInteresSubscribed.add(interest);

        mChonoSyncMap.put(interest, ChronoSync);
    }

    /**
     * This method takes the data and the interest to subscribe into ChronoSync
     *
     * @param jsonData String based on json structure
     * @param interest String interest selected
     */
    public void sendData(String jsonData, String interest) {

        mChonoSyncMap.get(interest.toLowerCase()).getDataHistory().add(jsonData);
        mChonoSyncMap.get(interest.toLowerCase()).increaseSequenceNos();

        Log.d(TAG, "Stroke generated: " + jsonData);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof ChronoSync) {
            Log.d(TAG, "Data reveived");
            parseJSONReceiver(String.valueOf(arg), true);
            updateListView();
        }
    }

    /**
     * This method update the ListView using a Thread
     */
    public void updateListView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //stuff that updates uim
                mMessagesListAdapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * Function to parse and draw the action mentioned in the passed JSON string
     *
     * @param string       the json representation of the action to be performed
     * @param addToHistory if true, add the action to history
     */
    public void parseJSONReceiver(String string, boolean addToHistory) {
        String interestKey;
        try {
            JSONObject jsonObject = new JSONObject(string);
            try {

                String type = jsonObject.get("type").toString();

                switch (type) {

                    case "text": {
                        // Create a toast of the toast_message text
                        String message = jsonObject.getString("data");
                        String username = jsonObject.getString("user");
                        String interest = jsonObject.getString("interest");
                        String date = jsonObject.getString("date");

                        interestKey = jsonObject.getString("interest");
                        mMessagesList.add(new Message(username, message, interest, date));

                        break;
                    }

                    default:
                        throw new JSONException("Unrecognized string: " + string);
                }

                if (addToHistory) {
                    //ChronoSync.getDataHistory().add(string);
                    mChonoSyncMap.get(interestKey.toLowerCase()).getDataHistory().add(string);
                }


            } catch (JSONException e) {
                Log.d(TAG, "JSON string error: " + string);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateValueSelected(String interest) {
        Log.d(TAG, "Interest " + interest);

        if (mInterestsSelected.contains("#" + interest)) {
            Log.d(TAG, "Delete");
            mInterestsSelected.remove("#" + interest);
            mInterestsSpinner.setAdapter(adapter);

            mChonoSyncMap.get(interest.toLowerCase()).getNDN().setActivityStop(true);
            //mNDNParmiters.setActivityStop(true);

        } else if (mInteresSubscribed.contains(interest.toLowerCase())) {
            Log.d(TAG, "Contains");
            //mNDNParmiters.setActivityStop(false);
            mChonoSyncMap.get(interest.toLowerCase()).getNDN().setActivityStop(false);
            interestSelected = interest;
            mInterestsSelected.add("#" + interest);
            mInterestsSpinner.setAdapter(adapter);
            mInterestsSpinner.setSelection(mInterestsSpinner.getFirstVisiblePosition());
        } else {
            Log.d(TAG, "Create");
            subscribeInterest(interest.toLowerCase());
            interestSelected = interest;
            mInterestsSelected.add("#" + interest);
            mInterestsSpinner.setAdapter(adapter);
            mInterestsSpinner.setSelection(mInterestsSpinner.getFirstVisiblePosition());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        Log.d(TAG, "save");
        savedInstanceState.putString(USER, mUser.getName());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        Log.d(TAG, "Restore");
        // Restore state members from saved instance
        mUserNameTextView.setText(savedInstanceState.getString(USER));
    }


}



