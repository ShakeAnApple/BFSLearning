package automaton;

import utils.Utils;
import values.Interval;
import values.IntervalValueHandler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class Automaton {
    private HashSet<State> _states;
    private Map<State, List<Transition>> _transitions;
    private State _startState;

    private List<VariableInfo> _inputVariableInfos;
    private List<VariableInfo> _outputVariableInfos;

    public Automaton() {
        _states = new HashSet<>();
        _transitions = new HashMap();
    }

    public Automaton(List<VariableInfo> inputVars, List<VariableInfo> outputVars){
        this();
        _inputVariableInfos = inputVars;
        _outputVariableInfos = outputVars;
    }

    public List<VariableInfo> getInputVariables() {
        return _inputVariableInfos;
    }

    public void setInputVariables(List<VariableInfo> inputVars) {
        _inputVariableInfos = inputVars;
    }

    public List<VariableInfo> getOutputVariables() {
        return _outputVariableInfos;
    }

    public void setOutputVariables(List<VariableInfo> outputVars) {
        _outputVariableInfos = outputVars;
    }

    public State getStartState() {
        return _startState;
    }

    public void addState(State state) {
        _states.add(state);
        if (state.isStart()){
            _startState = state;
        }
    }

    public void loadTransitions(String path){
        List<Transition> list = Utils.deserializeTransitions(path);
        for(Transition tr : list){
            if (!_transitions.containsKey(tr.getFrom())){
                _transitions.put(tr.getFrom(), new ArrayList<>());
            }
            _transitions.get(tr.getFrom()).add(tr);

            addState(tr.getFrom());
            addState(tr.getTo());
        }
    }

    public void addTransition(Transition tr){
        if (_transitions.get(tr.getFrom()) == null){
            _transitions.put(tr.getFrom(), new ArrayList<>());
        }
        addState(tr.getFrom());

        _transitions.get(tr.getFrom()).add(tr);

//        if (!_transitions.get(tr.getFrom()).contains(tr)){
//
//        }
        addState(tr.getTo());
    }

    public List<Transition> getTransitionsByFrom(State from){
        return _transitions.get(from);
    }

    public List<Transition> getAllTransitions(){
        return _transitions.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public HashSet<State> getStates() {
        return _states;
    }

    public Map<State, String> getStatesNames(){
        Map<State, String> statesNames = new HashMap<>();
        List<State> statesArr = new ArrayList<>(_states);
        for (int i = 0; i < _states.size(); i++) {
            String stateName = "s" + i;
            statesNames.put(statesArr.get(i), stateName);
        }
        return statesNames;
    }

    public void getNusmvRepresentation() throws Exception {
        
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("C:\\tmp\\m_gen.smv"), "utf-8"))) {

            writer.append("MODULE main\n");
            writer.append("VAR\n");

            // states
            writer.append("state: {");
            Map<State, String> statesNames = new HashMap<>();
            List<State> statesArr = new ArrayList<>(_states);
            for (int i = 0; i < _states.size(); i++) {
                String stateName = "s" + i;
                statesNames.put(statesArr.get(i), stateName);
                writer.append(stateName);
                if (i < _states.size() - 1){
                    writer.append(", ");
                }
            }
            writer.append("};\n");

            // inputs
            for (VariableInfo var : _inputVariableInfos) {
                writer.append(String.format("%1$s: %2$s .. %3$s;\n", var.getName(), var.getPossibleValues().get(0), var.getPossibleValues().get(var.getPossibleValues().size() - 1)));
            }

            List<VariableInfo> intervalVars = _outputVariableInfos.stream()
                    .filter(v -> v.getPossibleValues().get(0) instanceof IntervalValueHandler)
                    .collect(Collectors.toList());
            HashMap<String, Interval<Double>[]> intervalsByName = new HashMap<>();
            if (intervalVars.size() > 0) {
                for(VariableInfo var: intervalVars) {
                    Interval<Double>[] values = new Interval[var.getPossibleValues().size()];

                    for(Object posValue : var.getPossibleValues()){
                        IntervalValueHandler curValue = (IntervalValueHandler)posValue;

                        values[curValue.getCurrentIntervalNum()] = curValue.getCurrentInterval();
                    }

                    intervalsByName.put(var.getName(), values);
                }
            }
//
//            // outputs
////        for (VariableInfo var : _outputVariableInfos) {
////            writer.append(String.format("%1$s: %2$s .. %3$s", var.getName(), var.getPossibleValues().get(0), var.getPossibleValues().get(var.getPossibleValues().size() - 1)));
////        }
//
            writer.append("ASSIGN\n");

            //transitions
            writer.append(String.format("init(state) := %1$s;\n", statesNames.get(_startState)));
            writer.append("next(state) := case\n");
//
            for (State st : _states) {
                List<Transition> transitionsFromSt = _transitions.get(st);
                String curStateName = statesNames.get(st);
                for (Transition tr: transitionsFromSt) {
                    writer.append(String.format("(state = %1$s)",curStateName));
                    for (VariableValue val : tr.getSymbol().getVariablesValues()){
                        writer.append(String.format(" & (%1$s = %2$s)",val.getName(), val.getValue()));
                    }
                    writer.append(String.format(": %1$s; \n",statesNames.get(tr.getTo())));
                }


            }

            writer.append("TRUE: state;\n");
            writer.append("esac;\n");
            writer.append("DEFINE\n");
            for (VariableInfo var : _outputVariableInfos) {
                writer.append(String.format("%1$s := ", var.getName()));

                boolean isIntervalValue = intervalsByName.containsKey(var.getName());

                for (int i = 0; i < var.getPossibleValues().size(); i++) {


                    List<State> curValStates = new ArrayList<>();
                    for (State st : _states) {
                        if (isIntervalValue) {
                            IntervalValueHandler i1 = (IntervalValueHandler) st.getStateValue().getSymbol().getVariableValueByName(var.getName()).getValue();

                            //fix serialization =(
                            for(Object o: var.getPossibleValues()){
                                IntervalValueHandler interval = (IntervalValueHandler)o;
                                if (interval.getCurrentInterval().getFrom() <= i1.getConcreteValue() && interval.getCurrentInterval().getTo() > i1.getConcreteValue()){
                                    i1.setCurrentInterval(interval.getCurrentInterval());
                                    break;
                                }
                            }

                            IntervalValueHandler i2 = (IntervalValueHandler) var.getPossibleValues().get(i);
                            boolean res = i1.equals(i2);
                            if (res){
                                curValStates.add(st);
                            }
                        } else {
                            if (st.getStateValue().getSymbol().getVariableValueByName(var.getName()).getValue().equals(var.getPossibleValues().get(i))) {
                                curValStates.add(st);
                            }
                        }
                    }

                    if (curValStates.size() > 0) {

                        if (i == var.getPossibleValues().size() - 1){
                            if (!isIntervalValue) {
                                writer.append(String.format("%1$s;", var.getPossibleValues().get(i)));
                            } else{
                                writer.append(String.format("%1$s;", ((IntervalValueHandler)var.getPossibleValues().get(i)).getCurrentIntervalNum()));
                            }

                            break;
                        }

                        writer.append("(");
                        for (int j = 0; j < curValStates.size(); j++) {
                            if (j < curValStates.size() - 1) {
                                writer.append(String.format("(state = %1$s) | ", statesNames.get(curValStates.get(j))));
                            } else {
                                writer.append(String.format("(state = %1$s) ", statesNames.get(curValStates.get(j))));
                            }
                        }
                        if (!isIntervalValue) {
                            writer.append(String.format(") ? %1$s :", var.getPossibleValues().get(i)));
                        } else{
                            writer.append(String.format(") ? %1$s :", ((IntervalValueHandler)var.getPossibleValues().get(i)).getCurrentIntervalNum()));
                        }
                    }
                }
                writer.append("\n");
            }

            if (intervalsByName.keySet().size() > 0){
                writer.append("DEFINE\n");

                for (String varName : intervalsByName.keySet()){
                    writer.append(String.format("CONT_%1$s := case\n", varName));

                    Interval<Double>[] curIntervals = intervalsByName.get(varName);
                    for (int i = 0; i < curIntervals.length; i++){
                        writer.append(String.format("%1$s = %2$s: %3$s..%4$s;\n", varName, i, Math.round(curIntervals[i].getFrom()), Math.round(curIntervals[i].getTo())));
                    }

                    writer.append("esac;\n");
                }


            }
        }
    }
}
