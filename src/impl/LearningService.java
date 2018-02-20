package impl;

import automaton.*;
import connector.IConnector;
import connector.RequestQueryItem;
import connector.ResponseQueryItem;
import utils.Tuple;
import utils.Utils;
import values.IntervalValueHandler;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LearningService {
    private List<Symbol> _inputAlphabet;
    private List<Symbol> _outputAlphabet;
    private IConnector _connector;

    private Automaton _hypothesis;
    private HashSet<State> _possibleStates;
    private Map<State, SingleRequest[]> _processedStates;
    private Map<State, SingleRequest[]> _nextStates;

    private long _roundCount;

    /*
    reset symbol: reset;
                  resetButtonFloor0 := STRING_TO_BOOL(substr[2]);
                resetButtonFloor1 := STRING_TO_BOOL(substr[3]);
                resetButtonFloor2 := STRING_TO_BOOL(substr[4]);

                resetReqButton0 := STRING_TO_BOOL(substr[5]);
                resetReqButton1 := STRING_TO_BOOL(substr[6]);
                resetReqButton2 := STRING_TO_BOOL(substr[7]);

                resetCarAt0 := STRING_TO_BOOL(substr[8]);
                resetCarAt1 := STRING_TO_BOOL(substr[9]);
                resetCarAt2 := STRING_TO_BOOL(substr[10]);

                resetDoor0Closed := STRING_TO_BOOL(substr[11]);
                resetDoor1Closed := STRING_TO_BOOL(substr[12]);
                resetDoor2Closed := STRING_TO_BOOL(substr[13]);

                resetPos := STRING_TO_BOOL(substr[14]);


     req symbol: reset;
                motorUp;motorDown;
                door0;door1;door2
     */
    public LearningService(List<Symbol> inputAlphabet, List<Symbol> outputAlphabet, Automaton hypothesis, IConnector connector) {
        _inputAlphabet = inputAlphabet;
        _outputAlphabet = outputAlphabet;
        _hypothesis = hypothesis;
        _connector = connector;

        _connector.connect();

        _roundCount = 0;

        init();
    }

    private void init(){
        State startState = _connector.getDefault(_outputAlphabet.get(0).copyStructure());

        _connector.resetSystem(startState);

        _possibleStates = new HashSet<State>();
        _nextStates = new HashMap<>();

//        State startState = _connector.sendQuery(new RequestQueryItem(null,new SingleRequest[] {new SingleRequest(_inputAlphabet.get(0),1)}),
//                _outputAlphabet.get(0).copyStructure()).getEndState();
        for (Symbol s: _outputAlphabet){
            State newState = new State(new StateValue(s), s.equals(startState.getStateValue().getSymbol()));
            _possibleStates.add(newState);
        }

        _processedStates = new HashMap<State, SingleRequest[]>();
        _nextStates.put(startState, new SingleRequest[0]);
    }

    public void stepForward(){
        _roundCount++;

        System.out.println("Round " + _roundCount + ":");
        long startAll = System.currentTimeMillis();

        // motorUp;motorDown;Door0;Door1;Door2
        List<RequestQueryItem> requestQueryItems = new ArrayList<>();
        for (State state: _nextStates.keySet()){
            SingleRequest[] sequence = _nextStates.get(state);
            for(Symbol inS: _inputAlphabet){
                SingleRequest[] newSeq = Arrays.copyOf(sequence, sequence.length + 1);
                newSeq[newSeq.length - 1] = new SingleRequest(inS, 1);
                requestQueryItems.add(new RequestQueryItem(state, newSeq));
            }
        }

        _nextStates.clear();


        System.out.print("Sending Queries: ");
        long startQueries = System.currentTimeMillis();

        List<ResponseQueryItem> responseQueryItems = _connector.sendQueries(requestQueryItems,
                _outputAlphabet.get(0).copyStructure());

        long elapsedQueries = System.currentTimeMillis() - startQueries;

        System.out.print(elapsedQueries);
        System.out.println();

        // let's decide that loops can be found in cases where variable is of type Interval and it needs time to pass it's interval
        // in this case we need to process the same queue several times to decide on whether it is really self loop or it is moving
        // if during sending the same queue change will occur not in interval variable, then WTF (internal variables?)
        List<ResponseQueryItem> loopedQueries = responseQueryItems
                .stream()
                .filter(item -> item.getStartState().equals(item.getEndState()))
                .collect(Collectors.toList());

        System.out.print("Processing loops: ");
        long startLoops = System.currentTimeMillis();
        if (loopedQueries.size() > 0){
            processLoops(loopedQueries);
        }

        long elapsedLoops = System.currentTimeMillis() - startLoops;
        System.out.print(elapsedLoops);
        System.out.println();

        for (ResponseQueryItem r : responseQueryItems){
            _processedStates.put(r.getStartState(),
                    Stream.of(
                            r.getSequence())
                            .limit(r.getSequence().length - 1)
                            .collect(Collectors.toList())
                            .toArray(new SingleRequest[r.getSequence().length - 1]
                            )
            );
        }

        for (ResponseQueryItem resp: responseQueryItems){
            SingleRequest lastRequest = resp.getSequence()[resp.getSequence().length - 1];

            if (_hypothesis.getTransitionsByFrom(resp.getStartState()) != null && _hypothesis.getTransitionsByFrom(resp.getStartState()).size() > 31){
                String s = "wtf";
            }

            Transition tr = new Transition(
                    resp.getStartState(),
                    resp.getEndState(),
                    lastRequest.getSymbol(),
                    lastRequest.getRepeatCount());
            _hypothesis.addTransition(tr);

            if (!_processedStates.containsKey(resp.getEndState()) && lastRequest.getRepeatCount() < Integer.MAX_VALUE){
                if (_hypothesis.getTransitionsByFrom(resp.getEndState()) != null && _hypothesis.getTransitionsByFrom(resp.getEndState()).size() > 30){
                    String s = "wtf";
                }
                _nextStates.put(resp.getEndState(), resp.getSequence());
            }
        }

        System.out.println("Total: " + (System.currentTimeMillis() - startAll));
    }

    private void processLoops(List<ResponseQueryItem> loopedQueries) {

//        HashMap<Tuple<State, SingleRequest[]>, List<StateHistoryItem>> history = new HashMap<>();
        HashMap<UUID, List<StateHistoryItem>> history = new HashMap<>();

        List<String> intervalValuesNames = _outputAlphabet.get(0)
                .getVariablesValues()
                .stream()
                .filter(v -> v.getValue() instanceof IntervalValueHandler)
                .map(v -> v.getName())
                .collect(Collectors.toList());

        for (ResponseQueryItem r: loopedQueries){

            List<VariableValue<IntervalValueHandler>> intervalValues = new ArrayList<>();
            for (int i = 0; i < intervalValuesNames.size(); i++) {
                intervalValues.add((VariableValue<IntervalValueHandler>) r.getStartState()
                        .getStateValue()
                        .getSymbol()
                        .getVariableValueByName(intervalValuesNames.get(i)));
            }

            List<VariableHistoryItem> sortedVariableHistoryItems = intervalValues
                    .stream()
                    .sorted(Comparator.comparing(VariableValue::getOrder))
                    .map(v -> new VariableHistoryItem(v.clone()))
                    .collect(Collectors.toList());

//            history.put(new Tuple<State,SingleRequest[]>(r.getStartState(), r.getSequence()),
//                    new ArrayList<StateHistoryItem>()
//                    {{ add(
//                            new StateHistoryItem(sortedVariableHistoryItems, 1));}});

            history.put(r.getId(),
                    new ArrayList<StateHistoryItem>()
                    {{ add(
                            new StateHistoryItem(sortedVariableHistoryItems, 1));}});
        }

        boolean allLoopsResolved = false;
        int iterationsCounter = 1;

        while(!allLoopsResolved) {
            iterationsCounter ++;

            List<RequestQueryItem> requestQueryItems = new ArrayList<>();
            for (ResponseQueryItem r : loopedQueries) {
                SingleRequest seq = r.getSequence()[r.getSequence().length - 1];
                if (r.getStartState().equals(r.getEndState()) && seq.getRepeatCount() < Integer.MAX_VALUE) {
                    seq.incrementRepeatCount();
                    requestQueryItems.add(new RequestQueryItem(r.getId(), r.getEndState(), r.getSequence()));
                }
            }

            // resolve self loops


            // set system to the start state
            // can I set any state?
            List<ResponseQueryItem> responseQueryItems = _connector.sendQueries(requestQueryItems,
                    _outputAlphabet.get(0).copyStructure());

            allLoopsResolved = true;
            for (ResponseQueryItem r: responseQueryItems){
                if (r.getStartState().equals(r.getEndState())){
                    allLoopsResolved = false;

                    List<VariableValue<IntervalValueHandler>> intervalValues = new ArrayList<>();
                    for (int i = 0; i < intervalValuesNames.size(); i++){
                        intervalValues.add((VariableValue<IntervalValueHandler>) r.getEndState()
                                .getStateValue()
                                .getSymbol()
                                .getVariableValueByName(intervalValuesNames.get(i)));
                    }

                    List<VariableHistoryItem> sortedHistoryItems = intervalValues
                            .stream()
                            .sorted(Comparator.comparing(VariableValue::getOrder))
                            .map(v -> new VariableHistoryItem(v.clone()))
                            .collect(Collectors.toList());

//                    history.get(new Tuple<State,SingleRequest[]>(r.getStartState(), r.getSequence())).add(new StateHistoryItem(sortedHistoryItems, iterationsCounter));
                    history.get(r.getId()).add(new StateHistoryItem(sortedHistoryItems, iterationsCounter));
                }
                else{
//                    history.remove(new Tuple<State,SingleRequest[]>(r.getStartState(), r.getSequence()));
                    history.remove(r.getId());

//                    loopedQueries
//                            .stream()
//                    //        .filter(q -> q.getStartState().equals(r.getStartState()) && q.getSequence().equals(r.getSequence()))
//                            .filter(q -> q.getId().equals(r.getId()))
//                            .findAny()
//                            .get()
//                            .changeEndState(r.getEndState());
                }
                loopedQueries
                        .stream()
                        //        .filter(q -> q.getStartState().equals(r.getStartState()) && q.getSequence().equals(r.getSequence()))
                        .filter(q -> q.getId().equals(r.getId()))
                        .findAny()
                        .get()
                        .changeEndState(r.getEndState());
            }

            //analyze history to determine whether interval value is changing
//            List<Tuple<State, SingleRequest[]>> statesToRemove = new ArrayList<>();
            List<UUID> statesToRemove = new ArrayList<>();

//            for(Tuple<State, SingleRequest[]> st : history.keySet()){
            for(UUID stateId : history.keySet()){

                List<StateHistoryItem> stateRequestHistory = history.get(stateId);

                boolean isVarChanging = false;
                for (int i = 0; i < intervalValuesNames.size(); i++ ){

                    List<VariableHistoryItem> currentVarHistory = new ArrayList<VariableHistoryItem>();
                    for(StateHistoryItem stateHistoryItem : stateRequestHistory){
                        currentVarHistory.add(stateHistoryItem.getVariableHistoryItems().get(i));
                    }

                    double currentPearsonCoeff = Utils.countPearsonCorrelationCoefficient(currentVarHistory
                                    .stream()
                                     .map(item -> item.getValue().getConcreteValue())
                                    .collect(Collectors.toList()),
                            stateRequestHistory
                                    .stream()
                                    .map(s -> (double)(s.getIterationsCount()))
                                    .collect(Collectors.toList()));

                    stateRequestHistory
                            .get(stateRequestHistory.size() - 1)
                            .getVariableHistoryItems()
                            .get(i)
                            .setPearsonCoefficient(currentPearsonCoeff);
                    
                    double previousPearsonCoeff = currentVarHistory.get(currentVarHistory.size() - 2).getPearsonCoefficient();
                    if (Math.abs(Math.abs(currentPearsonCoeff) - Math.abs(previousPearsonCoeff)) > 0 || (Math.abs(currentPearsonCoeff) > 0.2)){
                        isVarChanging = true;
                        break;
                    } else if(stateRequestHistory.get(stateRequestHistory.size() - 1).getIterationsCount() < 10){
                        isVarChanging = true;
                        break;
                    }
                }

                if (!isVarChanging){
                    statesToRemove.add(stateId);
                }
            }

//            for(Tuple<State, SingleRequest[]> stateRequest: statesToRemove){
            for(UUID stateRequestId: statesToRemove){

                history.remove(stateRequestId);
                SingleRequest[] seq = loopedQueries
                        .stream()
//                        .filter(q -> q.getStartState().equals(stateRequestId.getObj1()) && q.getSequence().equals(stateRequestId.getObj2()))
                        .filter(q -> q.getId().equals(stateRequestId))
                        .findAny()
                        .get()
                        .getSequence();

                seq[seq.length - 1].setInfiniteRepeatCount();
            }

            if (history.keySet().size() == 0){
                allLoopsResolved = true;
            }
        }
    }

    public boolean isReady(){
        if (_possibleStates.size() == _processedStates.size()
                || _nextStates.keySet().size() == 0){

            List<Transition> tr = _hypothesis.getAllTransitions();

            Utils.serializeTransitions(tr, "C:\\tmp\\trans2");
            List<Transition> list = Utils.deserializeTransitions("C:\\tmp\\trans2");

            return true;
        }

        return false;
    }

}
