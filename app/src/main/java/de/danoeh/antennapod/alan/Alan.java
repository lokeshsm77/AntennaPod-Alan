package de.danoeh.antennapod.alan;

import com.alan.alansdk.AlanConfig;
import com.alan.alansdk.button.AlanButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Alan {

    static final String PROJECT_ID = "18db81871cd67aed0799a571520370fa2e956eca572e1d8b807a3e2338fdd0dc/stage";

    private AlanButton alanButton;

    private static Alan INSTANCE;

    private static AVHandler alanCallback;

    private Alan()
    {
        alanCallback = new AVHandler();

    }

    /*private static class AlanSingleton
    {
        private static final Alan INSTANCE = new Alan();
    }*/

    public static Alan getInstance()
    {
        if(INSTANCE == null){
              INSTANCE = new Alan();
        }
        return INSTANCE;
    }

    public static void clearInstance(){
        INSTANCE = null;
        alanCallback = null;
    }


    public AlanButton getAlanButton() {
        return alanButton;
    }

    public void setAlanButton(AlanButton alanButton) {
        if(this.alanButton != null) {
            this.getAlanButton().deactivate();
            this.getAlanButton().clearCallbacks();
            this.alanButton = null;
        }
        this.alanButton = alanButton;
        initConfig();
        registerCallback();
    }

    public void initConfig(){
        AlanConfig config = AlanConfig.builder()
                .setProjectId(PROJECT_ID)
                .build();
        this.getAlanButton().initWithConfig(config);
    }

    public void registerCallback(){
        this.getAlanButton().registerCallback(this.alanCallback);
    }

    public void registerCmdListener(AVListener cmdListener){
        this.alanCallback.registerAlanCmdListener(cmdListener);
    }

    public void clearAlanInstance(){
        if(this.getAlanButton() != null) {
            this.getAlanButton().clearCallbacks();
            clearInstance();
        }
    }

    public void playText(String inpuText){
        this.getAlanButton().playText(inpuText);
    }

    public void stopPlaying(){
        this.getAlanButton().performClick();
        this.getAlanButton().performClick();
    }

    public void setVisualState(String screenName){
        try{
            Map<String, String> screenState = new HashMap<>();
            screenState.put("screen", screenName);
            JSONObject visualState = new JSONObject(screenState);
            this.getAlanButton().setVisualState(visualState.toString());
        } catch (Exception je){
            this.getAlanButton().playText("Something went wrong while setting visual state");
        }
    }


    public void callProjectApi(String apiFunction, String data){
        this.getAlanButton().callProjectApi(apiFunction, data);
    }
}
