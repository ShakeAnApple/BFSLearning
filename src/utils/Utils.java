package utils;

import automaton.Transition;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static double countPearsonCorrelationCoefficient(List<Double> varList1, List<Double> varList2){
        if (varList1.size() != varList2.size()){
            throw new IllegalArgumentException("Arrays have different size, can't count coefficient");
        }

        Double sum1 = varList1.stream().mapToDouble(Double::doubleValue).sum();
        Double sum2 = varList2.stream().mapToDouble(Double::doubleValue).sum();

        Double arithmeticMean1 = sum1/varList1.size();
        Double arithmeticMean2 = sum2/varList2.size();

        List<Double> deviations1 = varList1.stream().map(item -> arithmeticMean1 - item).collect(Collectors.toList());
        List<Double> deviations2 = varList2.stream().map(item -> arithmeticMean2 - item).collect(Collectors.toList());

        List<Double> squaredDeviations1 = deviations1.stream().map(item -> item*item).collect(Collectors.toList());
        List<Double> squaredDeviations2 = deviations2.stream().map(item -> item*item).collect(Collectors.toList());

        Double squaredDeviationsSum1 = squaredDeviations1.stream().mapToDouble(Double::doubleValue).sum();
        Double squaredDeviationsSum2 = squaredDeviations2.stream().mapToDouble(Double::doubleValue).sum();

        if (squaredDeviationsSum1 == 0 || squaredDeviationsSum2 == 0){
            return 0;
        }

        List<Double> deviationsProducts = new ArrayList<Double>();
        for(int i = 0; i < deviations1.size(); i++){
            deviationsProducts.add(deviations1.get(i)*deviations2.get(i));
        }

        Double deviationProductsSum = deviationsProducts.stream().mapToDouble(Double::doubleValue).sum();

        return deviationProductsSum/Math.sqrt(squaredDeviationsSum1*squaredDeviationsSum2);
    }

    public static void serializeTransitions(List<Transition> list, String filename){
        try {
            FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(list);
            out.close();
            fileOut.close();
            System.out.println("\nSerialization Successful... Checkout your specified output file..\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Transition> deserializeTransitions(String filename){
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fis);
            ArrayList<Transition> arr = (ArrayList<Transition>)in.readObject();
            in.close();
            return arr;
        }
        catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
