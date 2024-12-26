package at.dici.shade.twitchbot;

import at.dici.shade.utils.log.LogLevel;
import at.dici.shade.utils.log.Logger;

public class ChannelProperties {
    private int messageCounter = 0;
    private boolean verified;

    public ChannelProperties(){
        this(false);
    }

    public ChannelProperties(boolean verified){
        this.verified = verified;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean rateLimit(){
        messageCounter++;
        Logger.log(LogLevel.DEBUG,"Channel.rateLimit() ctr: " + messageCounter);
        return messageCounter >= TtvConfig.MSG_RATE_LIMIT_QTY;
    }

    public void resetRateLimitCounter(){
        messageCounter = 0;
    }

}
