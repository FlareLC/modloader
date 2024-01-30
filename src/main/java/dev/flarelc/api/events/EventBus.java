package dev.flarelc.api.events;

import java.lang.reflect.Method;
import java.util.HashMap;


public class EventBus {
	private static HashMap<Object, RegisteredListener> objects = new HashMap<Object, RegisteredListener>();
	
	/*
	 * @description register an object to the POST event.
	 * @param Object to register
	 */
	public static void register(Object obj) {
		synchronized (objects) {
			objects.put(obj, new RegisteredListener(obj));
		}
		
	}
	
	/*
	 * @description Unregister an object to the POST event.
	 * @param Object to unregister
	 */
	public static void unregister(Object obj) {
		synchronized (objects) {
			objects.remove(obj);
		}
	
	}
	/*
	 * @description Post an event to the Event Bus
	 * @param Event to post
	 */
	
	public static void post(Event event) {
		synchronized (objects) {
			HashMap<Object, RegisteredListener> objects = (HashMap<Object, RegisteredListener>)EventBus.objects.clone();
			for(int i = 0; i < objects.size(); i++) {
				RegisteredListener listener = (RegisteredListener) objects.values().toArray()[i];
				Object obj = listener.object;
				Class current = obj.getClass();
				for(Method meth : listener.methods) {
					if(meth.getParameterTypes()[0] != event.getClass())continue;
					try {
						meth.invoke(obj, event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}