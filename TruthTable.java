import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLOutput;
import java.util.*;

public class TruthTable extends JFrame {
    //constants
    final static Dimension FrameSize = new Dimension(400, 400);


    public static JTable defaultTable(){
        Object[][] data = {{'-', '-', '-'}, {'-', '-', '-'}};
        String [] head = {"var 1", "var 2", "var 3"};



        return new JTable(data, head);
    }

    public static boolean operationsHandler(String expression, HashMap<Character, Boolean> variables){
        Stack <Character>  stack = new Stack<>();
        int i= 0;
        for(char ch : expression.toCharArray()){
            if(ch == ' '){
                i++;
                continue;
            } else if (Character.isAlphabetic(ch) && ch != 'v') {
                if(!stack.empty()){
                    if(Character.isAlphabetic(stack.peek()) && stack.peek()!= 'v'){
                        throw new IllegalArgumentException("Invalid expression");
                    }
                    else{
                        stack.push(ch);
                    }
                }
                else{
                    stack.push(ch);
                }
            } else if (ch == '^' || ch == 'v' || ch == '-' || ch =='>') {
                if(stack.empty()){
                    throw new IllegalArgumentException("Invalid expression");
                } else if (!Character.isAlphabetic(stack.peek()) && ch != '>' || stack.peek()== 'v') {
                    throw new IllegalArgumentException("Invalid expression");
                } else if (ch == '-') {
                    if(expression.charAt(i+1)== '>'){
                        stack.push(ch);
                        stack.push(expression.charAt(i+1));
                    }
                    else{
                        throw new IllegalArgumentException("Invalid expression");
                    }
                } else if (ch == '>') {
                    i++;
                    continue;
                }
                else{
                    stack.push(ch);
                }

            }

            i++;
        }
        if(!Character.isAlphabetic(stack.peek()) || stack.peek()== 'v'){
            throw new IllegalArgumentException("Invalid expression");
        }
        Stack <Character> tempStack = new Stack<>();
        while(!stack.empty()){
            tempStack.push(stack.pop());

        }
        Queue <Character> nQueue = new LinkedList<>();
        while(!tempStack.empty()){
            nQueue.offer(tempStack.pop());

        }

        boolean operand_1;
        String operator;
        boolean operand_2;
        boolean answer = true;
        boolean iteration_1 = true;

        while(!nQueue.isEmpty()){
            if(iteration_1){
                operand_1 = variables.get(nQueue.poll());
            }else{
                operand_1 = answer;
            }
            if(nQueue.peek() != '-'){
                operator= String.valueOf(nQueue.poll());}
            else{
                operator = String.valueOf(nQueue.poll());
                operator+= nQueue.poll();
            }
            //need to figure out for the ->
            operand_2 = variables.get(nQueue.poll());
            switch (operator) {
                case "^" -> answer = operand_1 && operand_2;
                case "v" -> answer = operand_1 || operand_2;
                case "->" -> answer = !operand_1 || operand_2;
            }
            iteration_1 = false;
        }
        return answer;
    }



    public static void flipArrayColumns(Object[][] array) {
        int numRows = array.length;
        int numCols = array[0].length;

        for (int i = 0; i < numRows; i++) {
            int left = 0;
            int right = numCols - 1;

            while (left < right) {
                Object temp = array[i][left];
                array[i][left] = array[i][right];
                array[i][right] = temp;

                left++;
                right--;
            }
        }
    }

    public static ArrayList<String> header(String inputText){
        ArrayList<String> header = new ArrayList<String>();
        char c;
        for(int i =0; i< inputText.length(); i++){
            c = inputText.charAt(i);
            if(Character.isAlphabetic(c)){
                header.add(c+"");

            }

        }

        Collections.sort(header);

        return header;
    }
    public static int numRows(String inputText){
        int numOfVar =0;
        char token;
        for(int i = 0; i < inputText.length(); i++){
            token = inputText.charAt(i);
            if(Character.isAlphabetic(token)){
                numOfVar++;
            }
        }
        return numOfVar;
    }

    public static Object [][] truthVal(int numOfVar){

        int numRows = (int) Math.pow(2, numOfVar);
        boolean[][] table = new boolean[numRows][numOfVar];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numOfVar; j++) {
                int value = (i / (int) Math.pow(2, j)) % 2;
                table[i][j] = (value == 1);
            }
        }

        Object [][] newTable = new Object[numRows][numOfVar];

        for(int i =0; i< numRows; i++){
            for(int j =0; j< numOfVar; j++){
                if(table[i][j]){
                    newTable[i][j]= "True";
                }
                else{
                    newTable[i][j]= "False";
                }

            }
        }
        return newTable;
    }
    public static ArrayList<Object> operationsPackager(String expression, Object[][] nArray) {
        ArrayList<Object> tArrayList = new ArrayList<>();
        for (Object[] objects : nArray) {
            HashMap<Character, Boolean> variables = new HashMap<>();
            for (int j = 0; j < nArray[0].length; j++) {
                char ch = header(expression).get(j).charAt(0);
                if (objects[j].equals("True")) {
                    variables.put(ch, true);
                } else {
                    variables.put(ch, false);
                }
            }
            if (operationsHandler(expression, variables)) {
                tArrayList.add("True");
            } else {
                tArrayList.add("False");
            }
        }
        return tArrayList;
    }


    public static void main(String[] args) {
        JFrame frame = getFrame();
        frame.setVisible(true);
    }

    private static JFrame getFrame(){
        JFrame frame = new JFrame("Truth Table Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(FrameSize);

        //mainPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        //Panel that handles variable input

        JPanel varPanel = getVarPanel();

        //Panel that handles the truth table output
        //where the Panels get initialized
        //packaging the panel
        mainPanel.add(varPanel);
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        return frame;
    }

    public static JPanel getVarPanel(){
        JPanel varPane = new JPanel();
        varPane.setLayout(new BoxLayout(varPane, BoxLayout.Y_AXIS));
        //Text field that reads the arguments given
        JLabel argHelper = new JLabel("Input variables below: ");
        JTextField readArgs = new JTextField(10000);
        readArgs.setPreferredSize(readArgs.getPreferredSize());
        readArgs.setMaximumSize(readArgs.getPreferredSize());
        ButtonGroup group = new ButtonGroup();
        JRadioButton printTruthValue = new JRadioButton("Print truth values");
        JRadioButton performOp = new JRadioButton("Print processes");
        JButton generateResult = new JButton("Generate Result");
        JButton helpPanel = new JButton(" See Help");


        JPanel bGroup = new JPanel();
        //bGroup.setLayout(new FlowLayout());
        JTable truthTable = defaultTable();
        JScrollPane sPane = new JScrollPane(truthTable);
        group.add(printTruthValue);
        group.add(performOp);
        bGroup.add(printTruthValue);
        bGroup.add(performOp);
        bGroup.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        varPane.add(argHelper);
        varPane.add(readArgs);
        varPane.add(bGroup);
        varPane.add(generateResult);
        varPane.add(sPane);
        varPane.add(helpPanel);

        class helpHandler implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                JTextArea textArea = new JTextArea(10, 30);
                String longText = """
                        The Truth Table Generator works as follows:
                        Print truth values returns a truth table with all possible unique truth values of the expression provided.
                        ACCEPTS: All alphabets as atomic propositions or variables.\s
                        Print processes accepts propositional logic expressions and returns a truth table with the expected values.\s

                        AND, OR and IMPLICATION are valid operators and are syntactically represented by ‘^’, ‘v’ and ‘->’ (where v should not be mistaken for V).
                        ACCEPTS: All alphabets not including ‘v’ as variables and valid operators above. Ternary operations and beyond are also accepted by the generator. \s
                        """;
                textArea.setText(longText);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(varPane,scrollPane,  "Help", JOptionPane.PLAIN_MESSAGE);
            }
        }

        class truthTableHandler implements ActionListener{
            public void actionPerformed(ActionEvent e) {
                if(printTruthValue.isSelected()) {

                    Object[][] table;
                    ArrayList<String> headers;
                    String inputText = readArgs.getText();
                    JTable newTable;
                    JScrollPane scroll;
                    int numRow;

                    headers = header(inputText);
                    numRow = numRows(inputText);
                    table = truthVal(numRow);
                    flipArrayColumns(table);
                    String[] newHeader = headers.toArray(new String[0]);
                    newTable = new JTable(table, newHeader);

                    scroll = new JScrollPane(newTable);
                    varPane.remove(sPane);
                    varPane.add(scroll);
                    varPane.revalidate();
                    varPane.repaint();

                } else if (performOp.isSelected()) {
                    Object[][] table;
                    ArrayList<String> headers;
                    String inputText = readArgs.getText();
                    JTable newTable;
                    JScrollPane scroll;
                    int numRow;
                    headers = header(inputText);
                    numRow = numRows(inputText);
                    table = truthVal(numRow);
                    flipArrayColumns(table);
                    String[] newHeader = headers.toArray(new String[0]);
                    newTable = new JTable(table, newHeader);

                    scroll = new JScrollPane(newTable);
                    ArrayList<Object> tArrayList = null;
                    try{
                    tArrayList = operationsPackager(inputText, table);}
                    catch(IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(varPane, "Invalid expression!");

                    }
                    assert tArrayList != null;
                    Object [] nTable = tArrayList.toArray(new Object[0]);
                    Object[][] tableData = new Object[nTable.length][1];
                    for ( int i = 0; i < nTable.length; i++ ){
                        tableData[i][0] = nTable[i];
                    }
                    Object[] head = {inputText};
                    JTable dTable = new JTable(tableData, head);
                    JScrollPane nscroll = new JScrollPane(dTable);
                    varPane.remove(sPane);
                    varPane.add(nscroll);
                    varPane.revalidate();
                    varPane.repaint();
                    varPane.add(scroll);
                }
            }
        }

        generateResult.addActionListener(new truthTableHandler());
        helpPanel.addActionListener(new helpHandler());


        //return the Panel
        return varPane;
    }




}
