package ie.sheehan.smarthome.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * An object representing an intrusion on the premises.
 */
public class IntrusionReading implements Serializable {

    // ============================================================================================
    // DECLARING CLASS VARIABLES
    // ============================================================================================
    private String id;
    private String image;
    private Date date;
    private boolean viewed;


    // ============================================================================================
    // DECLARING CONSTRUCTORS
    // ============================================================================================
    public IntrusionReading() {}

    /**
     * Default constructor that creates an {@link IntrusionReading} object from a {@link JSONObject}
     * with the properties id, image, timestamp & viewed.
     *
     * @param jsonObject with the properties id, image, timestamp & viewed
     */
    public IntrusionReading(JSONObject jsonObject) throws JSONException {
        setId(jsonObject.getString("id"));
        setImage(jsonObject.getString("image"));
        setDate(new Date(jsonObject.getLong("timestamp") * 1000L));
        setViewed(jsonObject.getBoolean("viewed"));
    }


    // ============================================================================================
    // DECLARING METHODS
    // ============================================================================================
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "Viewed: %b\nDate: %s",
                isViewed(), getDate());
    }


    // ============================================================================================
    // DECLARING GETTERS AND SETTERS
    // ============================================================================================
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getImage() {
        byte[] data = Base64.decode(this.image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

}
