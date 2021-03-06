package pt.ulusofona.copelabs.now.task;

import android.os.AsyncTask;
import android.util.Log;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;

import java.io.IOException;
import java.util.Observable;

import pt.ulusofona.copelabs.now.ndn.ChronoSync;


public class FetchChanges extends Observable {


    private String TAG = FetchChanges.class.getSimpleName();

    public FetchChanges(ChronoSync ChronoSync, String namePrefixStr) {
        new FetchChangesTask(ChronoSync, namePrefixStr).execute();
    }

    public class FetchChangesTask extends AsyncTask<Void, Void, Void> {


        String namePrefixStr;
        boolean m_shouldStop = false;
        String m_retVal;
        private ChronoSync mChronoSync;

        // Constructors
        public FetchChangesTask(ChronoSync ChronoSync, String namePrefixStr) {
            this.mChronoSync = ChronoSync;
            this.namePrefixStr = namePrefixStr;

        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "Fetch Task (doInBackground) for prefix: " + namePrefixStr);
            String nameStr = namePrefixStr;


            try {
                mChronoSync.getNDN().getFace().expressInterest(new Name(nameStr), new OnData() {
                    @Override
                    public void
                    onData(Interest interest, Data data) {
                        // Success, send data to be drawn by the Drawing view
                        m_retVal = data.getContent().toString();
                        m_shouldStop = true;
                        Log.d(TAG, "Got content: " + m_retVal);
                        setChanged();
                        notifyObservers(m_retVal);
                    }

                }, new OnTimeout() {
                    @Override
                    public void onTimeout(Interest interest) {
                        // Failure, try again
                        m_retVal = null;
                        m_shouldStop = true;
                        Log.d(TAG, "Got Timeout " + namePrefixStr);
                        if (!mChronoSync.getNDN().getAtivityStop()) {
                            new FetchChangesTask(mChronoSync, namePrefixStr).execute();
                        }
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void data) {
            Log.d(TAG, "Fetch Task (onPostExecute)");
        }
    }
}
