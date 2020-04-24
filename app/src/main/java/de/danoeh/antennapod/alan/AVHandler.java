package de.danoeh.antennapod.alan;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanState;
import com.alan.alansdk.events.EventCommand;
import com.alan.alansdk.events.EventOptions;
import com.alan.alansdk.events.EventParsed;
import com.alan.alansdk.events.EventRecognised;
import com.alan.alansdk.events.EventText;

import org.json.JSONObject;

import androidx.annotation.NonNull;

public class AVHandler extends AlanCallback {

    AVListener alanCmdListener = null;

    public void registerAlanCmdListener(AVListener listener){
        if(this.alanCmdListener != null){
            this.alanCmdListener = null;
        }
        this.alanCmdListener = listener;
    }

    @Override
    public void onAlanStateChanged(@NonNull AlanState alanState) {
        super.onAlanStateChanged(alanState);
    }

    @Override
    public void onRecognizedEvent(EventRecognised eventRecognised) {
        super.onRecognizedEvent(eventRecognised);
    }

    @Override
    public void onParsedEvent(EventParsed eventParsed) {
        super.onParsedEvent(eventParsed);
    }

    @Override
    public void onOptionsReceived(EventOptions eventOptions) {
        super.onOptionsReceived(eventOptions);
    }

    @Override
    public void onCommandReceived(EventCommand eventCommand) {
        super.onCommandReceived(eventCommand);
        try {
            JSONObject commandObject = eventCommand.getData();
            if( commandObject != null) {
                String alanCmd = commandObject.getJSONObject("data").getString("command");
                String alanCmdValue = commandObject.getJSONObject("data").getString("value");
                if(alanCmd != null){
                    if(this.alanCmdListener != null){
                        this.alanCmdListener.handleAlanCommand(alanCmd, alanCmdValue);
                    }
                } else {
                    Alan.getInstance().getAlanButton().playText("Invalid response, Some thing went wrong.");
                }
            } else {
                Alan.getInstance().getAlanButton().playText("Invalid response, Some thing went wrong.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alan.getInstance().getAlanButton().playText("Invalid operation, Some thing went wrong.");
        }
    }

    @Override
    public void onTextEvent(EventText eventText) {
        super.onTextEvent(eventText);

    }

    @Override
    public void onEvent(String event, String payload) {
        super.onEvent(event, payload);
    }

    @Override
    public void onError(String error) {
        super.onError(error);
    }
}
