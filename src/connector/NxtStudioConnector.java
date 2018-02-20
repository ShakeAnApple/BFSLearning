package connector;

import automaton.State;
import automaton.StateValue;
import automaton.Symbol;
import automaton.VariableValue;
import connector.tcp.TcpServer;
import impl.SingleRequest;
import values.BooleanValueHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Eskimos on 11.01.2018.
 */
public class NxtStudioConnector implements IConnector {

    private int _inPort;
    private int _outPort;

    private TcpServer _serverIn;
    private TcpServer _serverOut;

    public NxtStudioConnector(int inPort, int outPort){
        _inPort = inPort;
        _outPort = outPort;
    }

    @Override
    public boolean connect() {
        Thread th1 = new Thread(() -> {
            try {
                _serverIn = new TcpServer(_inPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread th2 = new Thread(() -> {
            try {
                _serverOut = new TcpServer(_outPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        th1.start();
        th2.start();
        try {
            th1.join();
            th2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public ResponseQueryItem sendQuery(RequestQueryItem queryItem, Symbol respSymbol) {
        try {
            return processQuery(queryItem, respSymbol);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResponseQueryItem processQuery(RequestQueryItem queryItem, Symbol respSymbol) throws IOException {

        String responseString = "";
//        for (SingleRequest s : queryItem.getSequence()){
            // TODO catch counter mistakes
            SingleRequest lastReq = queryItem.getSequence()[queryItem.getSequence().length - 1];
            //for(int i = 0; i < lastReq.getRepeatCount(); i++) {
//                String[] in = new String[s.getSymbol().getVariablesValues().size()];
                _serverIn.write(
                        getSymbolString(lastReq.getSymbol(), false)
//                        s.getSymbol().getVariablesValues()
//                                .stream()
//                                .sorted(Comparator.comparing(VariableValue::getOrder))
//                                .map(v -> v.getValue().toString())
//                                .collect(Collectors.toList())
//                                .toArray(new String[s.getSymbol().getVariablesValues().size()])
                );
                responseString = _serverOut.readLine();
                _serverOut.writeLine(responseString);

                if (_serverIn.canRead()) {
                    _serverIn.readLine();
                }

//                _serverIn.write(new String[]{"1", "0", "0", "0", "0"});
//                response = _serverOut.readLine();
//
//                _serverOut.writeLine(response);
//                _serverIn.readLine();
        //}

        Symbol responseSymb = parseResponse(responseString, respSymbol);
        State st = new State(new StateValue(responseSymb),false);
        return new ResponseQueryItem(queryItem.getId(), queryItem.getState(), st, queryItem.getSequence());
    }

    private Symbol parseResponse(String str, Symbol respSymbol){
        String[] strVars = str.split(";");
        Symbol newSymb = respSymbol.copyStructure();
        for (int i = 0; i < strVars.length; i++){
            newSymb.parseAndSetVariableValueByOrder(i, strVars[i]);
        }
        return newSymb;
    }

    @Override
    public List<ResponseQueryItem> sendQueries(List<RequestQueryItem> queryItems, Symbol respSymbol) {
        try {
            List<ResponseQueryItem> responses = new ArrayList<>();
            for(RequestQueryItem queryItem : queryItems){
                // TODO move resetsystem to logic?
                resetSystem(queryItem.getState());
                responses.add(processQuery(queryItem, respSymbol));
            }
            return responses;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void resetSystem(State state) {
        try {
//            if (_serverOut.canRead()){
//                _serverOut.readLine();
//            }
//            String response = "";
//            while(!response.equalsIgnoreCase("0;0;0;0;0;0;0;0;1;1;1;1;30;")){
//                _serverIn.write(new String[]{"1", "0", "0", "0", "0"});
//                response = _serverOut.readLine();
//
//                _serverOut.writeLine(response);
//                _serverIn.readLine();
//            }
            while (_serverOut.canRead()){
                _serverOut.readLine();
            }

            _serverIn.write(
                   getSymbolString(state.getStateValue().getSymbol(), true)
            );


//            String responseString = "";
//            while (_serverOut.canRead()){
                String responseString = _serverOut.readLine();
                _serverOut.writeLine(responseString);
//                System.out.println("write resp back");
//            }

            while (_serverIn.canRead()) {
                _serverIn.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] getSymbolString(Symbol smb, boolean isReset){
        List<VariableValue> varVals = smb.getVariablesValues();

        List<String> varValsString = varVals
                .stream()
                .sorted(Comparator.comparing(VariableValue::getOrder))
                .map(v -> v.getValue().toString())
                .collect(Collectors.toList());

        varValsString.add(0, String.valueOf(new BooleanValueHandler(isReset)));

        return varValsString.toArray(new String[varValsString.size()]);
    }



    @Override
    public State getDefault(Symbol respSymbol) {
        return new State(new StateValue(parseResponse("0;0;0;0;0;1;1;1;1;30;", respSymbol)),true);
    }

    public void close(){
        try {
            _serverIn.close();
            _serverOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
