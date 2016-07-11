package me.spotlight.spotlight.event;

import com.squareup.otto.Bus;

/**
 * Created by Anatol on 7/10/2016.
 */
public class EventBus {

    static private Bus bus;

    public static Bus getInstance() {
        if (null == bus) {
            bus = new Bus();
        }
        return bus;
    }
}
