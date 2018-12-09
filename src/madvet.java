// madvet
// Jerred Shepherd

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class madvet {
    public static void main(String[] args) throws FileNotFoundException {
        List<Input> inputs = getInputs();
        List<Output> outputs = solveInputs(inputs);
        printOutputs(outputs);
    }

    private static String outputToString(Output output) {
        StringBuilder sb = new StringBuilder();
        output.solutions.entrySet().forEach(entry -> {
            sb.append(entry.getKey() + " " + (entry.getValue() == -1 ? "NO SOLUTION" : entry.getValue()) + "\n");
        });
        return String.format("%s %s\n%s", output.dataSetNumber, output.numberOfQuestions, sb.toString());
    }

    private static void printOutputs(List<Output> outputs) throws FileNotFoundException {
        File outputFile = new File("madvet.out");
        PrintWriter printWriter = new PrintWriter(outputFile);
        System.out.println(outputs);
        List<String> outputStrings = new ArrayList<>();
        outputs.forEach(output -> outputStrings.add(outputToString(output)));
        outputStrings.forEach(s -> {
           System.out.print(s);
           printWriter.print(s);
        });
        printWriter.close();
    }

    private static int solveQuestion(List<Machine> machines, Question question) {
        System.out.println("Solving question " + question.number);
        Queue<QueueEntry> queue = new LinkedList<>();
        Set<ArrayWrapper> set = new HashSet<>();

        if (Arrays.equals(question.initialState, question.goalState)) {
            return 0;
        }

        machines.forEach(machine -> {
            set.add(new ArrayWrapper(question.initialState));
            if (machine.canRunForward(question.initialState)) {
                QueueEntry entry = new QueueEntry(question.initialState, machine, 0, QueueEntry.Mode.FOWARD);
                queue.add(entry);
            }
            if (machine.canRunReverse(question.initialState)) {
                QueueEntry entry = new QueueEntry(question.initialState, machine, 0, QueueEntry.Mode.REVERSE);
                queue.add(entry);
            }
        });

        while (!queue.isEmpty()) {
            QueueEntry entry = queue.remove();
//            System.out.println(entry);

            int[] newState;
            if (entry.mode == QueueEntry.Mode.FOWARD) {
                newState = entry.machine.runForward(entry.state);
            } else {
                newState = entry.machine.runReverse(entry.state);
            }

            if (set.contains(new ArrayWrapper(newState))) {
                continue;
            } else {
                set.add(new ArrayWrapper(newState));
            }

            if (Arrays.equals(newState, question.goalState)) {
                return entry.numberOfMoves + 1;
            }

            if (entry.numberOfMoves == 30) {
                continue;
            }

//            boolean areAnyAnimalsOverGoal = false;
//            for (int i = 0; i < question.goalState.length; i++) {
//                if (newState[i] > question.goalState[i]) {
//                    areAnyAnimalsOverGoal = true;
//                }
//            }
//
//            if (areAnyAnimalsOverGoal) {
//                continue;
//            }

            machines.forEach(machine -> {
                if (machine.canRunForward(newState)) {
                    QueueEntry newEntry = new QueueEntry(newState, machine, entry.numberOfMoves + 1, QueueEntry.Mode.FOWARD);
                    queue.add(newEntry);
                }
                if (machine.canRunReverse(newState)) {
                    QueueEntry newEntry = new QueueEntry(newState, machine, entry.numberOfMoves + 1, QueueEntry.Mode.REVERSE);
                    queue.add(newEntry);
                }
            });

        }

        return -1;
    }

    private static Output solveInput(Input input) {
        System.out.println("Solving input " + input.dataSetNumber);
        SortedMap<Integer, Integer> questionSolutions = new TreeMap<>();
        input.questions.forEach(question -> {
            int numberOfMovesToSolve = solveQuestion(input.machines, question);
            questionSolutions.put(question.number, numberOfMovesToSolve);
        });
        return new Output(input.dataSetNumber, input.numberOfQuestions, questionSolutions);
    }

    private static List<Output> solveInputs(List<Input> inputs) {
        List<Output> outputs = new ArrayList<>();
        inputs.forEach(input -> outputs.add(solveInput(input)));
        return outputs;
    }

    private static List<Input> getInputs() throws FileNotFoundException {
        File inputFile = new File("madvet.in");
        Scanner scanner = new Scanner(inputFile);

        List<Input> inputs = new ArrayList<>();
        int numberOfDataSets;
        while (scanner.hasNext()) {
            numberOfDataSets = scanner.nextInt();
            for (int i = 0; i < numberOfDataSets; i++) {
                int dataSetNumber = scanner.nextInt();
                int numberOfQuestions = scanner.nextInt();

                List<Machine> machines = new ArrayList<>();
                // Read in machines
                for (int j = 0; j < 3; j++) {
                    int[] input = new int[3];
                    int[] output = new int[3];
                    input[j] = 1;
                    for (int k = 0; k < 3; k++) {
                        output[k] = scanner.nextInt();
                    }
                    Machine m = new Machine(input, output);
                    machines.add(m);
                }

                List<Question> questions = new ArrayList<>();
                for (int j = 0; j < numberOfQuestions; j++) {
                    int questionNumber = scanner.nextInt();
                    int[] initialState = new int[3];
                    int[] goalState = new int[3];
                    for (int k = 0; k < 3; k++) {
                        initialState[k] = scanner.nextInt();
                    }
                    for (int k = 0; k < 3; k++) {
                        goalState[k] = scanner.nextInt();
                    }
                    Question q = new Question(initialState, goalState, questionNumber);
                    questions.add(q);
                }

                Input input = new Input(machines, questions, dataSetNumber, numberOfQuestions);
                inputs.add(input);
            }
        }

        return inputs;
    }

    public static class Question {
        final int number;
        final int[] initialState;
        final int[] goalState;

        public Question(int[] initialState, int[] goalState, int number) {
            this.initialState = initialState;
            this.goalState = goalState;
            this.number = number;
        }

        @Override
        public String toString() {
            return "Question{" +
                    "initialState=" + Arrays.toString(initialState) +
                    ", goalState=" + Arrays.toString(goalState) +
                    '}';
        }
    }

    public static class Machine {
        final int[] input;
        final int[] output;

        public Machine(int[] input, int[] output) {
            this.input = input;
            this.output = output;
        }

        boolean canRunForward(int[] userInput) {
            return canRunMachine(userInput, input);
        }

        boolean canRunReverse(int[] userInput) {
            return canRunMachine(userInput, output);
        }

        int[] runForward(int[] userInput) {
            return runMachine(userInput, this.input, this.output);
        }

        int[] runReverse(int[] userInput) {
            return runMachine(userInput, this.output, this.input);
        }

        private static boolean canRunMachine(int[] userInput, int[] machineInput) {
            return doesNotContainNegative(subtractArrays(userInput, machineInput));
        }

        private static int[] runMachine(int[] userInput, int[] machineInput, int[] machineOutput) {
            int[] output = subtractArrays(userInput, machineInput);
            output = addArrays(output, machineOutput);
            return output;
        }

        private static boolean doesNotContainNegative(int[] input) {
            for (int j : input) {
                if (j < 0) {
                    return false;
                }
            }
            return true;
        }

        private static int[] addArrays(int[] l, int[] r) {
            if (l.length != r.length) {
                throw new IllegalArgumentException("l and r are not the same length");
            }
            int[] output = new int[l.length];
            for (int i = 0; i < l.length; i++) {
                output[i] = l[i] + r[i];
            }
            return output;
        }

        private static int[] subtractArrays(int[] l, int[] r) {
            if (l.length != r.length) {
                throw new IllegalArgumentException("l and r are not the same length");
            }
            int[] output = new int[l.length];
            for (int i = 0; i < l.length; i++) {
                output[i] = l[i] - r[i];
            }
            return output;
        }

        @Override
        public String toString() {
            return "Machine{" +
                    "input=" + Arrays.toString(input) +
                    ", output=" + Arrays.toString(output) +
                    '}';
        }
    }

    static class Input {
        final List<Machine> machines;
        final List<Question> questions;
        final int dataSetNumber;
        final int numberOfQuestions;

        public Input(List<Machine> machines, List<Question> questions, int dataSetNumber, int numberOfQuestions) {
            this.machines = machines;
            this.questions = questions;
            this.dataSetNumber = dataSetNumber;
            this.numberOfQuestions = numberOfQuestions;
        }

        @Override
        public String toString() {
            return "Input{" +
                    "machines=" + machines +
                    ", questions=" + questions +
                    ", dataSetNumber=" + dataSetNumber +
                    ", numberOfQuestions=" + numberOfQuestions +
                    '}';
        }
    }

    static class Output {
        final int dataSetNumber;
        final int numberOfQuestions;
        final SortedMap<Integer, Integer> solutions;

        public Output(int dataSetNumber, int numberOfQuestions, SortedMap<Integer, Integer> solutions) {
            this.dataSetNumber = dataSetNumber;
            this.numberOfQuestions = numberOfQuestions;
            this.solutions = solutions;
        }

        @Override
        public String toString() {
            return "Output{" +
                    "dataSetNumber=" + dataSetNumber +
                    ", numberOfQuestions=" + numberOfQuestions +
                    ", solutions=" + solutions +
                    '}';
        }
    }

    static class QueueEntry {
        final int[] state;
        final Machine machine;
        final int numberOfMoves;
        final Mode mode;

        enum Mode {
            FOWARD, REVERSE
        }

        public QueueEntry(int[] state, Machine machine, int numberOfMoves, Mode mode) {
            this.state = state;
            this.machine = machine;
            this.numberOfMoves = numberOfMoves;
            this.mode = mode;
        }

        @Override
        public String toString() {
            return "QueueEntry{" +
                    "state=" + Arrays.toString(state) +
                    ", machine=" + machine +
                    ", numberOfMoves=" + numberOfMoves +
                    ", mode=" + mode +
                    '}';
        }
    }

    static class ArrayWrapper {
        final int[] array;

        ArrayWrapper(int[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ArrayWrapper that = (ArrayWrapper) o;
            return Arrays.equals(array, that.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }
}
