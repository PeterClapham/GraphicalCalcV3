import java.util.*;
/*
ADD COMMENTS.
 */
public class ShuntingYard {

    public enum Function {
        SIN, COS, TAN, CSC, SEC, COT, SINH, COSH, TANH, LOG, LN, ARCCOS, ARCSIN, ARCTAN
    }

    public Map<String, Function> fnc = new HashMap<>() {{
        put("s", Function.SIN);
        put("c", Function.COS);
        put("t", Function.TAN);
        put("v", Function.CSC);
        put("$", Function.SEC);
        put("u", Function.COT);
        put("z", Function.SINH);
        put("h", Function.COSH);
        put("r", Function.TANH);
        put("l", Function.LOG);
        put("n", Function.LN);
        put("q", Function.ARCCOS);
        put("w", Function.ARCSIN);
        put("f", Function.ARCTAN);
    }};

    public enum Operator
    {
        ADD(1), SUBTRACT(2), MULTIPLY(3), DIVIDE(4), EXPONENT(6),
        SIN(7), COS(7), TAN(7), SEC(7), COT(7), SINH(7), COSH(7), TANH(7),
        LOG(7), LN(7), CSC(7), ARCTAN(7), ARCSIN(7), ARCCOS(7);
        final int precedence;
        Operator(int p) { precedence = p; }
    }

    public Map<String, Operator> ops = new HashMap<>() {{
        put("+", Operator.ADD);
        put("-", Operator.SUBTRACT);
        put("*", Operator.MULTIPLY);
        put("/", Operator.DIVIDE);
        put("^", Operator.EXPONENT);
        put("s", Operator.SIN);
        put("c", Operator.COS);
        put("t", Operator.TAN);
        put("v", Operator.CSC);
        put("$", Operator.SEC);
        put("u", Operator.COT);
        put("z", Operator.SINH);
        put("h", Operator.COSH);
        put("r", Operator.TANH);
        put("l", Operator.LOG);
        put("n", Operator.LN);
        put("q", Operator.ARCCOS);
        put("w", Operator.ARCSIN);
        put("f", Operator.ARCTAN);
    }};

    private boolean isHigherPrec(String op, String sub)
    {
        return (ops.containsKey(sub) && ops.get(sub).precedence >= ops.get(op).precedence);
    }

    public String postfix(String infix)
    {
        infix = formatString(infix);
        for (int i = 0; i < infix.length() - 1; i++){
            String token1 = infix.substring(i, i+1);
            String token2 = infix.substring(i+1, i+2);
            if (!(ops.containsKey(token1) || token1.equals("(") || token1.equals(")"))
                    && (token2.equals("x"))) {
                infix = infix.substring(0, i+1) + "*" + infix.substring(i+1);
                i++;
            }
            /*
            if (ops.containsKey(token2) && !ops.containsKey(token1)) {
                infix = infix.substring(0, i+1) + "," + infix.substring(i+1);
                i++;
            }
            */
            if (token1.equals("^") && token2.equals("-")) {
                int bCount = 0;
                for(int j = i-1; j >= 0; j --) {
                    if (infix.substring(j, j+1).equals(")")){
                        bCount ++;
                    }
                    else if (infix.substring(j, j+1).equals("(")){
                        bCount --;
                    }
                    if (bCount == 0) {
                        if (isHigherPrec(infix.substring(j-1, j), "^")){
                            j --;
                        }
                        infix = infix.substring(0, j-1) + "1/(" + infix.substring(j-1, i) + ")^"
                                + infix.substring(i+2);
                        i++;
                        break;
                    }
                }
            }
        }
        StringBuilder output = new StringBuilder();
        Deque<String> stack  = new LinkedList<>();
        for (int i = 0; i < infix.length(); i++) {
            String token = infix.substring(i, i+1);
            // operator or function
            if (ops.containsKey(token)) {
                while (!stack.isEmpty() && isHigherPrec(token, stack.peek()))
                    output.append(stack.pop());
                stack.push(token);

                // left parenthesis
            } else if (token.equals("(")) {
                stack.push(token);

                // right parenthesis
            } else if (token.equals(")")) {
                while ( ! stack.peek().equals("("))
                    output.append(stack.pop());
                stack.pop();

                // digit or x
            } else {
                output.append(token);
            }
        }

        while ( ! stack.isEmpty())
            output.append(stack.pop());

        return output.toString();
    }

    private String formatString(String infix){
        infix = infix.toLowerCase();
        infix = infix.replace("arcsin", "w");
        infix = infix.replace("arccos", "q");
        infix = infix.replace("arctan", "f");
        infix = infix.replace("sinh", "z");
        infix = infix.replace("cosh", "h");
        infix = infix.replace("tanh", "r");
        infix = infix.replace("csc", "v");
        infix = infix.replace("sin", "s");
        infix = infix.replace("cos", "c");
        infix = infix.replace("tan", "t");
        infix = infix.replace("sec", "$");
        infix = infix.replace("cot", "u");
        infix = infix.replace("log", "l");
        return infix.replace("ln", "n");
    }
}
