package de.tud.feedback.plugin.proteus;

import java.util.function.Function;

/**
 *  Map Thing (rest/semantic) to Item (rest/items): <bindingName>_<thingName>_<itemName>_<number>
 *      e.g. Thing_homematic_dimmer_1 => homematic_dimmer_dimmer_1
 *
 *  DISCLAIMER: This is NOT generic due to the possible difference of thingName and itemName!
 */
class ThingToItemMapper implements Function<String, String> {

    @Override
    public String apply(String thing) {
        return thing.replaceAll("Thing_([^_]+)_([^_]+)_([0-9]+)", "$1_$2_$2_$3");
    }

}
