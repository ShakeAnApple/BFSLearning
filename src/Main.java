import automaton.Automaton;
import automaton.Symbol;
import automaton.VariableInfo;
import connector.IConnector;
import connector.NxtStudioConnector;
import graph.XmlGraphBuilder;
import impl.LearningService;
import values.BooleanValueHandler;
import values.IntervalValueHandler;
import values.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        List<BooleanValueHandler> possibleBoolValues = List.of(new BooleanValueHandler(false), new BooleanValueHandler(true));

        VariableInfo<BooleanValueHandler> inputDoor0Open = new VariableInfo("door0", 2, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> inputDoor1Open = new VariableInfo("door1", 3,  possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> inputDoor2Open = new VariableInfo("door2", 4,  possibleBoolValues, BooleanValueHandler::new);

        VariableInfo<BooleanValueHandler> inputDoMotorUp = new VariableInfo("motorUp", 0, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> inputDoMotorDown = new VariableInfo("motorDown", 1, possibleBoolValues, BooleanValueHandler::new);
        List<VariableInfo> inputVars = List.of(inputDoMotorDown,inputDoMotorUp,
                inputDoor0Open, inputDoor1Open, inputDoor2Open);

        Interval<Double>[] posIntervals = new Interval[]{
                new Interval<>(30.0, 30.5),
                new Interval<>(30.5, 224.5),
                new Interval<>(224.5, 225.5),
                new Interval<>(225.5, 418.5),
                new Interval<>(418.5, 419.5)
        };

        List<IntervalValueHandler> possiblePosValues = new ArrayList<>();

        for (Interval<Double> interval: posIntervals){
            possiblePosValues.add(
                    new IntervalValueHandler(posIntervals, Double::parseDouble, (Double o1, Double o2) -> {
                        if (01 > o2) return 1;
                        if (o1 < o2) return -1;
                        return 0;
                    }, interval)
            );
        }

        VariableInfo<IntervalValueHandler> outputPos = new VariableInfo("pos", 9, possiblePosValues, () -> new IntervalValueHandler(posIntervals, (String str) -> Double.parseDouble(str), (Double o1, Double o2) -> {
            if (01 > o2) return 1;
            if (o1 < o2) return -1;
            return 0;
        }, posIntervals[0]));

//        VariableInfo<BooleanValueHandler> outputButtonFloor0 = new VariableInfo("buttonFloor0", 0, possibleBoolValues, BooleanValueHandler::new);
//        VariableInfo<BooleanValueHandler> outputButtonFloor1 = new VariableInfo("buttonFloor1", 1, possibleBoolValues, BooleanValueHandler::new);
//        VariableInfo<BooleanValueHandler> outputButtonFloor2 = new VariableInfo("buttonFloor2", 2, possibleBoolValues, BooleanValueHandler::new);

        VariableInfo<BooleanValueHandler> outputRequestFloor0 = new VariableInfo("requestFloor0", 0, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> outputRequestFloor1 = new VariableInfo("requestFloor1",1, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> outputRequestFloor2 = new VariableInfo("requestFloor2", 2, possibleBoolValues, BooleanValueHandler::new);

        VariableInfo<BooleanValueHandler> outputElevatorAtFloor0 = new VariableInfo("elevatorAtFloor0", 3, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> outputElevatorAtFloor1 = new VariableInfo("elevatorAtFloor1", 4, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> outputElevatorAtFloor2 = new VariableInfo("elevatorAtFloor2", 5, possibleBoolValues, BooleanValueHandler::new);

        VariableInfo<BooleanValueHandler> outputDoor0Closed = new VariableInfo("door0Closed", 6, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> outputDoor1Closed = new VariableInfo("door1Closed", 7, possibleBoolValues, BooleanValueHandler::new);
        VariableInfo<BooleanValueHandler> outputDoor2Closed = new VariableInfo("door2Closed", 8, possibleBoolValues, BooleanValueHandler::new);

        List<VariableInfo> outputVars = List.of(
                outputDoor0Closed, outputDoor1Closed, outputDoor2Closed,
                outputElevatorAtFloor0, outputElevatorAtFloor1, outputElevatorAtFloor2,
                outputRequestFloor0, outputRequestFloor1, outputRequestFloor2,
                outputPos);

        AlphabetBuilder alphabetBuilder = new AlphabetBuilder();
        List<Symbol> inputAlphabet = alphabetBuilder.build(inputVars);
        List<Symbol> outputAlphabet = alphabetBuilder.build(outputVars);

//        ModelInfo modelInfo = new ModelInfo("C:\\Projects\\FCP\\active_learning\\cylinder_simulink", "Cylinder_simple");


        Automaton hypothesis = new Automaton(inputVars, outputVars);
        // magic numbers oO
        IConnector connector = new NxtStudioConnector(64999, 64998);
//        IConnector connector = new NxtStudioConnector(1010, 1011);

        hypothesis.setInputVariables(inputVars);
        hypothesis.setOutputVariables(outputVars);

        boolean needToLearn = false;
        if (needToLearn) {
            LearningService ls = new LearningService(inputAlphabet, outputAlphabet, hypothesis, connector);
            long start = System.currentTimeMillis();
            while (!ls.isReady()) {
                ls.stepForward();
            }
            System.out.print("Total alg: " + (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start)));
        } else{
            hypothesis.loadTransitions("C:\\tmp\\trans2");


            hypothesis.getNusmvRepresentation();
            XmlGraphBuilder.saveAsDgmlGraph(hypothesis, "C:\\tmp\\g.dgml");
        }


//        hypothesis.getNusmvRepresentation();
    }
}
