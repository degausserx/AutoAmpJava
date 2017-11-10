package net.codejack.autoamp2.helpers;

/**
 * Created by Degausser on 7/9/2017.
 */

public class SharingIsCaring {

    private static SharingIsCaring instance = null;

    private SharingIsCaring() {

    }

    public static SharingIsCaring getInstance() {
        if (instance == null) {
            instance = new SharingIsCaring();
        }
        return instance;
    }

    private Object data = null;

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
