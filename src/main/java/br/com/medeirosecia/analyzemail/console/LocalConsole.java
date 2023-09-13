package br.com.medeirosecia.analyzemail.console;

public class LocalConsole {
    private String lastMessage;
    private int lastProgress;
    public void msgToUser(String msg){
        this.lastMessage = msg;
        System.out.println(msg);
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastProgress(int lastProgress) {
        this.lastProgress = lastProgress;
    }
    
    public int getLastProgress() {
        return lastProgress;
    }
    
}
