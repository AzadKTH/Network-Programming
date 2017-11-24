package server.net;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;


public class MsgAndKeyObject {
    private final SelectionKey clientKey;
    private final ByteBuffer msgToSend;
    
    MsgAndKeyObject(ByteBuffer msg, SelectionKey key){
        this.msgToSend = msg;
        this.clientKey = key;
    }
    
    public SelectionKey getKey(){
        return this.clientKey;
    }
    
    public ByteBuffer getMessage(){
        return this.msgToSend;
    }
}
