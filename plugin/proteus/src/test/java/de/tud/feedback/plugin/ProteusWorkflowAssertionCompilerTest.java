package de.tud.feedback.plugin;

import de.tud.feedback.WorkflowAssertion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ProteusWorkflowAssertionCompilerTest {

    private final String expression = "" +
            "// Assertion for Process step\n" +
            "// ----------------------------" +
            "// until: now().plusSeconds(10)\n" +
            "// ----------------------------\n" +
            "MATCH (thing)-[:isIn]->({ name: \"Kitchen_Mueller\" })\n" +
            "MATCH (thing)-[:hasState]->(state:LightIntensityState)\n" +
            "MATCH (state)-[:hasStateValue]->(value)\n" +
            "WHERE toFloat(value.realStateValue) > 1000" +
            "RETURN state";

    private ProteusWorkflowAssertionCompiler compiler;

    @Before
    public void setUp() {
        compiler = new ProteusWorkflowAssertionCompiler();
    }

    @Test
    public void something() {
        WorkflowAssertion assertion = compiler.compile(expression);

        /// TODO
    }

}
