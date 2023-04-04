package com.dev.rx.db;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
/*
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoSocketException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
*/
public class MongoConnect {
  /*

    private Context mContext;

    public MongoConnect(Context context) {
        mContext = context;
    }

    public void connect(){
        try {
            ConnectionString connectionString = new ConnectionString("mongodb+srv://jaimez07788:Criso1989#@app.uold5ek.mongodb.net/?retryWrites=true&w=majority");
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connectionString)
                    .serverApi(ServerApi.builder()
                            .version(ServerApiVersion.V1)
                            .build())
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);
            MongoDatabase database = mongoClient.getDatabase("app");
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Connect db!", Toast.LENGTH_SHORT).show();
                }
            });
        }  catch (MongoSecurityException e) {
            e.printStackTrace();
        } catch (MongoSocketException e) {
            e.printStackTrace();
        } catch (MongoTimeoutException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

   */
}
