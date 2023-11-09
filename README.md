# Adb_Contact_import
this repository allows you to send contacts from your computer to your phone in the phone book
To send via adb you need:

Allow access to contacts from the application

install adb

use command
`adb shell am start -n "com.redi.contact/com.redi.contact.MainActivity" -e name "name" -e phoneNumber "number"`
