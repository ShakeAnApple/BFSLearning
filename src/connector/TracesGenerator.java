//package connector;
//
///**
// * Created by Eskimos on 16.01.2018.
// */
//class TracesGenerator {
//    private static <T> List<T> toList(T[] arr) {
//        return new ArrayList<>(Arrays.asList(arr));
//    }
//
//    @SuppressWarnings("unchecked")
//    private static <T> T[] toArray(List<T> list, T... args) {
//        return list.toArray(args);
//    }
//
//    private static List<String> read(TcpServer server, Function<List<Integer>, List<Integer>> transformer) throws IOException {
//        final String outputsString = server.readLine();
//        final List<String> dataSplitted = toList(outputsString.split(";"));
////        final String lastData = dataSplitted.remove(dataSplitted.size() - 1);
//        final List<Integer> originalData = mapList(dataSplitted, Integer::parseInt);
//        final List<String> dataTransformed = mapList(transformer.apply(originalData), Object::toString);
//        final List<String> res = new ArrayList<>(dataTransformed);
////        dataTransformed.add(lastData);
//        server.write(toArray(dataTransformed));
//        return res;
//    }
//
//    static void run(String configFile) throws Exception {
//        Strategy strategy;
//        Config.load(configFile);
//        final Config config = Config.instance();
//        String strategyStr = config.tracesStrategy();
//        switch (strategyStr) {
//            case "semi100": strategy = new SemiRandomStrategy(100, 100); break;
//            case "semi10": strategy = new SemiRandomStrategy(10, 10); break;
//            case "random": strategy = new RandomStrategy(); break;
//            case "original": strategy = new NoStrategy(); break;
//            case "mutation": strategy = new MutationStrategy(); break;
//            default: throw new AssertionError("Not correct trace generation strategy: " + strategyStr);
//        }
//        double startTime = -1;
//        final String headerFile = combinePaths(config.workingDir(), config.header());
//        int[] ports = config.tcpPorts();
//        try (final PrintWriter out = new PrintWriter(combinePaths(config.workingDir(), "traces", config.genTraceFile()))) {
//            try (final TcpServer tcpInputs = new TcpServer(ports[0])) {
//                try (final TcpServer tcpOutputs = new TcpServer(ports[1])) {
//                    out.print(readAllText(headerFile));
//                    //noinspection InfiniteLoopStatement
//                    while (true) {
//                        List<String> outputs = read(tcpOutputs, strategy::transformOutputs);
//                        List<String> inputs = read(tcpInputs, strategy::transformInputs);
//                        if (startTime == -1) {
//                            startTime = System.nanoTime();
//                        }
//                        out.format("%.3f", (System.nanoTime() - startTime) / 1e9);
////                        System.out.println("out: " + outputs);
////                        System.out.println("in: " + inputs);
//                        for (String input : inputs) {
//                            out.print(" " + input);
//                        }
////                        out.print(" |");
//                        for (String output : outputs) {
//                            out.print(" " + output);
//                        }
//                        out.println();
//                        out.flush();
//                    }
//                }
//            }
//        }
//    }
//}
