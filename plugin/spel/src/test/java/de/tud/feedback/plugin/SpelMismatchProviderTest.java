package de.tud.feedback.plugin;

import de.tud.feedback.domain.ContextMismatch;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static de.tud.feedback.Utils.params;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class SpelMismatchProviderTest {

    private SpelMismatchProvider provider;

    @Before
    public void init() {
        provider = new SpelMismatchProvider();
    }

    @Test
    public void testContextVariableToHigh() {
        ContextMismatch mismatch = provider
                .getMismatch("#lightIntensity < 750", params()
                    .put("lightIntensity", 800)
                    .build());

        assertThat(mismatch.getType()).isEqualTo(ContextMismatch.Type.TOO_HIGH);
        assertThat(mismatch.getSource()).isEqualTo(800);
        assertThat(mismatch.getTarget()).isEqualTo(750);
    }

    @Test
    public void testContextVariableToLow() {
        ContextMismatch mismatch = provider
                .getMismatch("#lightIntensity > 750", params()
                        .put("lightIntensity", 700)
                        .build());

        assertThat(mismatch.getType()).isEqualTo(ContextMismatch.Type.TOO_LOW);
        assertThat(mismatch.getSource()).isEqualTo(700);
        assertThat(mismatch.getTarget()).isEqualTo(750);
    }

    @Test
    public void testContextVariableUnequal() {
        ContextMismatch mismatch = provider
                .getMismatch("#kodi == 'playing'", params()
                        .put("kodi", "stopped")
                        .build());

        assertThat(mismatch.getType()).isEqualTo(ContextMismatch.Type.UNEQUAL);
        assertThat(mismatch.getSource()).isEqualTo("stopped");
        assertThat(mismatch.getTarget()).isEqualTo("playing");
    }

}
