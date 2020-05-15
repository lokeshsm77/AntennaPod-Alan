# Registration with Alan Studio
Step 1. Please register at https://studio.alan.app/register

Step 2. After the registration complete, please log in

Step 3. Create a New Project

Step 4. Add new script - and copy the voice script from "https://github.com/lokeshsm77/AlanScripts-AntennaPod" and save the script.

# Configuration on Alan SDK
Step 1. Open build.gradle - Add the Alan Maven URL in the project repository in all projects repository section.

     maven { url "https://mymavenrepo.com/repo/fSCXIHAoBMWBdlZGqq6n/"}.

Step 2. Create new variable called alanSDKVersion = "4.7.7", in project.ext section.

Step 3. Update the minSdkVersion value to 21.

Step 4. Open - app - build.gradle - Add the dependency of  Alan SDK in dependencies.

     implementation "app.alan:sdk:$alanSDKVersion".

Step 5. Open AndroidMainfest.xml - and set the permission for RECORD_AUDIO.

     <uses-permission android:name="android.permission.RECORD_AUDIO"/>

Step 6. Open - main.xml - Add the alan button to the main layout.

     <com.alan.alansdk.button.AlanButton
     android:id="@+id/alan_button"
     android:layout_width="wrap_content"
     android:layout_height="wrap_content"/>

Step 7. Open - MainActivity.java - Create a new variable of AlanButton and initialize the alanButton

     private AlanButton alanButton;
     ....
     protected void onCreate(Bundle savedInstanceState) {
          ...
          alanButton = findViewById(R.id.alan_button);
     }

Step 8. Copy the project key code from Alan studio - embed section - select android tab and copy the key, and set the project id in Alan.java 

     AlanConfig config = AlanConfig.builder()
     .setProjectId("18db81871cd67aedeca572e1d8b807a3e2338fdd0dc/stage")
     .build();

# Please find complete document of Alan integration for Android
https://alan.app/docs/client-api/android


# AntennaPod

     
<img src="https://raw.githubusercontent.com/AntennaPod/AntennaPod/develop/app/src/main/play/listings/en-US/graphics/phone-screenshots/00.png" alt="Screenshot 0" height="200"> <img src="https://raw.githubusercontent.com/AntennaPod/AntennaPod/develop/app/src/main/play/listings/en-US/graphics/phone-screenshots/01.png" alt="Screenshot 1" height="200"> <img src="https://raw.githubusercontent.com/AntennaPod/AntennaPod/develop/app/src/main/play/listings/en-US/graphics/phone-screenshots/02.png" alt="Screenshot 2" height="200"> <img src="https://raw.githubusercontent.com/AntennaPod/AntennaPod/develop/app/src/main/play/listings/en-US/graphics/phone-screenshots/03.png" alt="Screenshot 3" height="200"> <img src="https://raw.githubusercontent.com/AntennaPod/AntennaPod/develop/app/src/main/play/listings/en-US/graphics/phone-screenshots/04.png" alt="Screenshot 4" height="200"> <img src="https://raw.githubusercontent.com/AntennaPod/AntennaPod/develop/app/src/main/play/listings/en-US/graphics/phone-screenshots/05.png" alt="Screenshot 5" height="200">


## Feedback
You can use the [AntennaPod Google Group](https://groups.google.com/forum/#!forum/antennapod) for discussions about the app.

Bug reports and feature requests can be submitted [here](https://github.com/AntennaPod/AntennaPod/issues) (please read the [instructions](https://github.com/AntennaPod/AntennaPod/blob/master/CONTRIBUTING.md) on how to report a bug and how to submit a feature request first!).

## Help test AntennaPod
AntennaPod has many users and we don't want them to run into trouble when we add a new feature. It's important that we have a significant group test our app, so that we know all possible combinations of phones, Android versions and use cases work as expected. Check out our wiki how to join our [Alpha and Beta testing programmes](https://github.com/AntennaPod/AntennaPod/wiki/Help-test-AntennaPod)!

## License

AntennaPod is licensed under the MIT License. You can find the license text in the LICENSE file.

## Translating AntennaPod
If you want to translate AntennaPod into another language, you can visit the [Transifex project page](https://www.transifex.com/antennapod/antennapod/).


## Building AntennaPod

Information on how to build AntennaPod can be found in the [Wiki](https://github.com/AntennaPod/AntennaPod/wiki/Building-AntennaPod).

