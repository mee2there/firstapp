package firstapp.ashley.com.firstapp;

/**
 * Created by ashley on 1/28/15.
 */

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public  class EventDetailVO implements Parcelable{

    private int _eventID;
    private String _eventName;
    private String _eventDesc;

    public int getEventID() {
        return _eventID;
    }

    public String getEventName() {
        return _eventName;
    }

    public String getEventDesc() {
        return _eventDesc;
    }

    public Date getEventTime() {
        return _eventTime;
    }

    private Date _eventTime;

    public EventDetailVO(String name,int id) {
        _eventID = id;
        _eventName = name;
        _eventDesc = name;
        _eventTime = new Date();

    }

    public EventDetailVO(int id, String name,String desc,Date eventDate) {
        _eventID = id;
        _eventName = name;
        _eventDesc = desc;
        _eventTime = eventDate;

    }    // Parcelling part
    public EventDetailVO(Parcel in){
        String[] data = new String[2];

        this._eventID = in.readInt();
        in.readStringArray(data);
        this._eventName = data[0];
        this._eventDesc = data[1];
        this._eventTime = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._eventID);
        parcel.writeStringArray(new String[] {this._eventName,
                this._eventDesc});
        parcel.writeLong(this._eventTime.getTime());


    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventDetailVO createFromParcel(Parcel in) {
            return new EventDetailVO(in);
        }

        public EventDetailVO[] newArray(int size) {
            return new EventDetailVO[size];
        }
    };
}