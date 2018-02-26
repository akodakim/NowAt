package pt.ulusofona.copelabs.now.ndn;

import net.named_data.jndn.Face;

/**
 * This class is part of Now@ application. It provides information about basic parameters necessary
 * to configure NDN. The principal parameters are Face, ApplicationBroadCastPrefix and
 * ApplicationNamePrefix
 *
 * @author Omar Aponte (COPELABS/ULHT)
 * @version 1.0
 *          COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 6/9/17 3:09 PM
 */


public class NDNParameters {

    private Face mFace;

    private String mApplicationNamePrefix;

    private String mApplicationBroadcastPrefix;

    private boolean mActivityStop;

    private String mUUID;

    /**
     * Construct method to NDNPArameters
     *
     * @param face NDN Face
     */
    public NDNParameters(Face face) {
        mFace = face;
    }

    public void setApplicationNamePrefix(String applicationNamePrefixt) {
        mApplicationNamePrefix = applicationNamePrefixt;
    }

    public void setActivityStop(boolean condition) {
        mActivityStop = condition;
    }

    /**
     * Get Application Broadcast Prefix of an interest
     *
     * @return String ApplicationBroadcastPrefix
     */
    public String getApplicationBroadcastPrefix() {
        return mApplicationBroadcastPrefix;
    }

    public void setApplicationBroadcastPrefix(String applicationBroadcastPrefix) {
        mApplicationBroadcastPrefix = applicationBroadcastPrefix;
    }

    /**
     * Get Application Name Prefix of an interest
     *
     * @return String ApplicationNamePrefix
     */
    public String getmApplicationNamePrefix() {
        return mApplicationNamePrefix;
    }

    /**
     * Get Face
     *
     * @return Face Object
     */
    public Face getFace() {
        return mFace;
    }

    /**
     * Get status of the activity
     *
     * @return boolean
     */
    public boolean getAtivityStop() {
        return mActivityStop;
    }

    /**
     * Get th UUID of the application
     *
     * @return String UUID
     */
    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }
}
