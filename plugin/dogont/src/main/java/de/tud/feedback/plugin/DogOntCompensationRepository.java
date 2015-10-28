package de.tud.feedback.plugin;

import com.google.common.collect.ImmutableMap;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.repository.CompensationRepository;

import java.util.Collection;
import java.util.Map;

import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

public class DogOntCompensationRepository implements CompensationRepository {

    private static final String ON   = "OnCommand";
    private static final String OFF  = "OffCommand";
    private static final String UP   = "UpCommand";
    private static final String DOWN = "DownCommand";

    private static final Map<Object, Command.Type> commands = ImmutableMap.<Object, Command.Type>builder()
            .put(ON,   Command.Type.ASSIGN)
            .put(OFF,  Command.Type.ASSIGN)
            .put(UP,   Command.Type.UP)
            .put(DOWN, Command.Type.DOWN)
            .build();

    private final CypherExecutor executor;

    private final String query;

    public DogOntCompensationRepository(CypherExecutor executor, String cypherQuery) {
        this.executor = executor;
        this.query = cypherQuery;
    }

    @Override
    public Collection<Command> findCommandsManipulating(Long testNodeId) {
        return executor.execute(query, params()
                    .put("stateId", testNodeId)
                    .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
    }

    // LATER crappy, find a better way...
    private boolean isRepeatable(Map<String, Object> attributes) {
        String command = commandFrom(attributes);
        String unit = unitFrom(attributes);
        Double state;

        switch (unit) {
            case "percent":
                state = numberFrom(attributes);
                return (0 < state && state < 100) && !OFF.equals(command) && !ON.equals(command);

            case "degree-Celsius":
                state = numberFrom(attributes);
                return (0 < state && state < 50) && !OFF.equals(command) && !ON.equals(command);

            default: return false;
        }
    }

    private String commandFrom(Map<String, Object> attributes) {
        return String.valueOf(attributes.get("commandType"));
    }

    private Double numberFrom(Map<String, Object> attributes) {
        return Double.valueOf(String.valueOf(attributes.get("actuatorState")));
    }

    private String unitFrom(Map<String, Object> attributes) {
        return String.valueOf(attributes.get("actuatorStateUnit"));
    }

    private Command toCommand(Map<String, Object> attributes) {
        return new Command()
                .setRepeatable(isRepeatable(attributes))
                .setTargetTo((String) attributes.get("actuator"))
                .setNameTo((String) attributes.get("commandName"))
                .setTypeTo(commands.get(attributes.get("commandType")));
    }

}
