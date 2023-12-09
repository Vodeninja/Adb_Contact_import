package com.redi.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class export_contact extends AppCompatActivity {
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_contact);
        exportContacts();
    }

    private void exportContacts() {
        try {
            File logFile = new File(getExternalFilesDir(null), "contacts.txt");
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            PrintWriter writer = new PrintWriter(new FileWriter(logFile, true));
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);

                    if (nameIndex >= 0 && idIndex >= 0) {
                        String contactId = cursor.getString(idIndex);
                        String displayName = cursor.getString(nameIndex);

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{contactId},
                                null
                        );

                        if (phoneCursor != null && phoneCursor.moveToFirst()) {
                            int phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                            if (phoneIndex >= 0 && displayName != null) {
                                String phoneNumber = phoneCursor.getString(phoneIndex);
                                if (phoneNumber != null) {
                                    writer.println(displayName + ":" + phoneNumber);
                                }
                            }


                            phoneCursor.close();
                        }
                    }
                }
                cursor.close();
            }
            addToLog("contact exported\n");
            writer.close();
            finish();
        } catch (IOException e) {
            addToLog("contact not exported\n");
            e.printStackTrace();
        }
    }
}