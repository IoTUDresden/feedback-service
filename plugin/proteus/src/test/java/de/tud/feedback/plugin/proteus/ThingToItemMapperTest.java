package de.tud.feedback.plugin.proteus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class ThingToItemMapperTest {

    private ThingToItemMapper mapper;

    @Before
    public void setup() {
        mapper = new ThingToItemMapper();
    }

    @Test
    public void testNonGenericMapping() {
        String thing = "Thing_homematic_dimmer_1";
        String item = "homematic_dimmer_dimmer_1";

        assertThat(mapper.apply(thing)).isEqualTo(item);
    }

}
