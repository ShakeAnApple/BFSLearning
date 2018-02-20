package automaton;

import values.ValueHandler;

import java.io.Serializable;
import java.util.*;

public class Symbol implements Serializable{
    private Map<String, VariableValue> _valuesByName;

    public Symbol(List<VariableInfo> automatonVars) {
        _valuesByName = new HashMap<>();

        for (VariableInfo v: automatonVars) {
            _valuesByName.put(v.getName(), v.createVariableValue());
        }
    }

    private Symbol(){
        _valuesByName = new HashMap<>();
    }

    public List<VariableValue> getVariablesValues(){
        return new ArrayList<>(_valuesByName.values());
    }

    public void parseAndSetVariableValueByOrder(int order, String var){
        try {
            _valuesByName.values()
                    .stream()
                    .filter(v -> v.getOrder() == order)
                    .findFirst()
                    .get()
                    .parseAndSetValue(var);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getVariablesValuesNames(){
        return new ArrayList<>(_valuesByName.keySet());
    }

    public VariableValue getVariableValueByName(String varName){
        return _valuesByName.get(varName);
    }

    public void setVariableValueByName(String varName, ValueHandler valueHandler){
        _valuesByName.get(varName).setValue(valueHandler);
    }

    public void parseAndSetValueByName(String varName, Object value) throws Exception {
        _valuesByName.get(varName).parseAndSetValue(value);
    }

    public Symbol copyStructure() {
        Symbol result = new Symbol();

        for (VariableValue val: _valuesByName.values()) {
            result._valuesByName.put(val.getName(), val.createInstance(val.getName()));
        }

        return result;
    }


    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        return Arrays.toString(_valuesByName.values().toArray());
    }

    @Override
    public boolean equals(Object obj) {
        Symbol other = (Symbol)obj;

        if(_valuesByName.size() != other._valuesByName.size()){
            return false;
        }

        for (String varName: _valuesByName.keySet()) {
            VariableValue v1 = _valuesByName.get(varName);
            VariableValue v2 = other._valuesByName.get(varName);
            if (v2 == null){
                return false;
            }

            if (!v1.equals(v2)){
                return false;
            }
        }
        return true;
    }
}
