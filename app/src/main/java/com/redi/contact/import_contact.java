package com.redi.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class import_contact extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_contact);
        addContactsFromFile(getContentResolver());
    }
    private void addToLog(String message) {
        try {
            File logFile = new File(getExternalFilesDir(null), "log.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(logFile, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.append(message);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addContact(ContentResolver contentResolver, String name, String phoneNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();


        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            ContentProviderResult[] results = contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);

            Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(results[0].uri.getLastPathSegment()));
        } catch (Exception e) {
            e.printStackTrace();
            addToLog("error contactUri\n");
            finish();
        }

    }
    public void addContactsFromFile(ContentResolver contentResolver) {
        try {
            File file = new File(getExternalFilesDir(null), "contacts.txt");

            if (!file.exists()) {
                addToLog("file not found \n");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String phoneNumber = parts[1].trim();

                    addContact(contentResolver, name, phoneNumber);
                } else {
                    Log.e("ContactManager", "Invalid line format: " + line);
                }
            }
            addToLog("contact imported \n");
            reader.close();
            finish();
        } catch (IOException e) {
            Log.e("ContactManager", "Error reading file: " + e.getMessage());
        }
    }
}