package firstapp.ashley.com.firstapp;

import android.util.Log;

import com.google.android.gms.internal.lo;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ashley on 2/10/15.
 */
public class MongoDB {

    private DBCollection eventCollection;

    public MongoDB(){
        //final BasicDBObject[] seedData = createSeedData();
        connectDB();
        //eventCollection.insert(seedData);
    }

    private void  connectDB() {
        try {
            //mongodb://mee2thereservice:rva@2015@ds041831.mongolab.com:41831/mee2theredb
            // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
            MongoClientURI uri = new MongoClientURI("mongodb://mee2thereservice:rva2015@ds041831.mongolab.com:41831/mee2theredb");
            MongoClient client = new MongoClient(uri);
            DB db = client.getDB(uri.getDatabase());
            Log.i("connectDB","after getDB : "+db.toString());
        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */

             eventCollection = db.getCollection("events");
            Log.i("connectDB","after eventCollection : "+eventCollection.toString());
        }catch (UnknownHostException unknownHost) {
            Log.e("connectDB","Exception : "+unknownHost.getMessage());

        }
        }

    public boolean insertEvent(String eventName,String eventDesc, Date eventDate) {
        BasicDBObject newEvent = new BasicDBObject();
        newEvent.put("name", eventName);
        newEvent.put("description", eventDesc);
        newEvent.put("date", eventDate);
        eventCollection.insert(newEvent);
        return true;
    }

    public List<EventDetailVO> getEvents() {
// find all where i > 50
        BasicDBObject query = new BasicDBObject("i", new BasicDBObject("$ne", ""));
        List<EventDetailVO> eventVOList = new ArrayList<EventDetailVO>();
        //DBCursor cursor = eventCollection.find(query);
        DBCursor cursor = eventCollection.find();
        try {
            int index = 0;
            while (cursor.hasNext()) {
                BasicDBObject dbObject= (BasicDBObject)cursor.next();
                eventVOList.add(new EventDetailVO(index,dbObject.getString("name") ,dbObject.getString("description"),dbObject.getDate("date")));
                index++;
            }
        } finally {
            cursor.close();
        }

        return eventVOList;
    }

    // Extra helper code

    public static BasicDBObject[] createSeedData(){

        BasicDBObject sampleEvent1 = new BasicDBObject();
        sampleEvent1.put("name", "Hiking");
        sampleEvent1.put("description", "Hiking on mount everest");
        sampleEvent1.put("date", new Date());
        BasicDBObject location1 = new BasicDBObject();
        location1.put("x", 1);
        location1.put("y", 1);
        sampleEvent1.put("loc", location1);

        BasicDBObject sampleEvent2 = new BasicDBObject();
        sampleEvent2.put("name", "Golfing");
        sampleEvent2.put("description", "Golfing in California");
        sampleEvent2.put("date", new Date());
        BasicDBObject location2 = new BasicDBObject();
        location2.put("x", 1);
        location2.put("y", 1);
        sampleEvent2.put("loc", location2);

        BasicDBObject sampleEvent3 = new BasicDBObject();
        sampleEvent3.put("name", "Biking");
        sampleEvent3.put("description", "Biking in Hawai");
        sampleEvent3.put("date", new Date());
        BasicDBObject location3 = new BasicDBObject();
        location3.put("x", 1);
        location3.put("y", 1);
        sampleEvent3.put("loc", location3);

        final BasicDBObject[] seedData = {sampleEvent1, sampleEvent2, sampleEvent3};

        return seedData;
    }

}
