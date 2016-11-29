package assignment7;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by aliziyaan on 11/28/16.
 */
public class ChatObserver extends Observable {
    public ArrayList<String> usersInChat = new ArrayList<>();

    public void changed(){
        setChanged();
    }

    public void unChanged(){
        clearChanged();
    }
}
