package de.danoeh.antennapod.alan.data;

    public class AlanCommandObj{
        private String command;
        private String value;
        private String error;

        public AlanCommandObj(String cmd, String cValue){
            command = cmd;
            value = cValue;
        }

        public AlanCommandObj(String cError){
            this.error = cError;
        }
    }
