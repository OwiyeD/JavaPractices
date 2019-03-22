/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phonecard;

/**
 *
 * @author CET
 */
public final class CallZone {
     public static boolean isValidZone(String zone) {
        zone = zone.toLowerCase();
        return (zone.equals("canada") ||
            zone.equals("usa") ||
            zone.equals("europe") ||
            zone.equals("asia") ||
            zone.equals("anz") ||
            zone.equals("latinam") ||
            zone.equals("africa")
        );
    }
}
