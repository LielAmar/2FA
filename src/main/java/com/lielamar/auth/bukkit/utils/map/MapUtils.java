package com.lielamar.auth.bukkit.utils.map;

import org.bukkit.map.MapView;

public class MapUtils {

    /**
     * Returns the ID of a MapView for versions before 1.13
     *
     * @param view   MapView to get the ID of
     * @return       ID of view
     */
    public static short getMapID(MapView view) {
        try {
            return (short) view.getId();
        } catch (NoSuchMethodError e) {
            try {
                Class<?> MapView = Class.forName("org.bukkit.map.MapView");
                Object mapID = MapView.getMethod("getId").invoke(view);
                return (short) mapID;
            } catch (Exception e1) {
                return 1;
            }
        }
    }
}