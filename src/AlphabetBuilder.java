import automaton.Symbol;
import automaton.VariableInfo;
import values.ValueHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AlphabetBuilder {

    public List<Symbol> build(List<VariableInfo> variableInfos){
        List<Symbol> result = new ArrayList<Symbol>();

        List<VariableInfo> sortedVarInfos = variableInfos
                .stream()
                .sorted(Comparator.comparing(VariableInfo::getOrder))
                .collect(Collectors.toList());

        int capacity = 1;

        for (int i = 0; i < sortedVarInfos.size(); i++) {
            capacity *= sortedVarInfos.get(i).getPossibleValues().size();
        }

        for (int i = 0; i < capacity; i++) {
            result.add(new Symbol(sortedVarInfos));
        }

        for (int varIdx = 0, repeatCnt = 1; varIdx < sortedVarInfos.size(); varIdx++) {

            VariableInfo curVar = sortedVarInfos.get(varIdx);
            List possibleValues = curVar.getPossibleValues();

            int curSymbIdx = 0;
            for (int i = 0; i < result.size(); i+=possibleValues.size()*repeatCnt) {

                for (int posValueIdx = 0; posValueIdx < possibleValues.size(); posValueIdx++) {

                    for (int counter = 0; counter<repeatCnt; counter++) {
                        result.get(curSymbIdx).setVariableValueByName(curVar.getName(), (ValueHandler)possibleValues.get(posValueIdx));
                        curSymbIdx ++;
                    }
                }
            }

            repeatCnt *= possibleValues.size();

        }

        return result;
    }
}
