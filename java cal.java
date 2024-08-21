import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class CalculatorGUI {
    private JFrame frame;
    private JTextField display;
    private StringBuilder currentInput = new StringBuilder();
    private StringBuilder expression = new StringBuilder();

    public CalculatorGUI() {
        frame = new JFrame("Calculator");
        display = new JTextField();
        display.setEditable(false);
        display.setHorizontalAlignment(JTextField.RIGHT);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4)); // Layout for calculator buttons
        
        String[] buttonLabels = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C" // Clear button
        };

        ActionListener buttonListener = new ButtonClickListener();
        
        // Create buttons and add them to the panel
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(buttonListener);
            panel.add(button);
        }
        
        // Arrange components in the frame
        frame.setLayout(new BorderLayout());
        frame.add(display, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);
        
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            String text = source.getText();
            
            // Handle numeric and decimal input
            if (text.matches("[0-9]")) {
                currentInput.append(text);
                display.setText(currentInput.toString());
            } else if (text.equals(".")) {
                if (currentInput.indexOf(".") == -1) {
                    currentInput.append(".");
                    display.setText(currentInput.toString());
                }
            } else if (text.equals("=")) {
                if (expression.length() > 0) {
                    expression.append(currentInput.toString());
                    try {
                        double result = evaluateExpression(expression.toString());
                        display.setText(expression + " = " + result);
                    } catch (Exception ex) {
                        display.setText("Error");
                    }
                    currentInput.setLength(0);
                    expression.setLength(0);
                }
            } else if (text.equals("C")) {
                clear();
            } else {
                // Handle operators and build the expression
                if (currentInput.length() > 0) {
                    expression.append(currentInput.toString());
                    expression.append(" ").append(text).append(" ");
                    currentInput.setLength(0);
                    display.setText(expression.toString());
                }
            }
        }
        
        private void clear() {
            currentInput.setLength(0);
            expression.setLength(0);
            display.setText("");
        }

        private double evaluateExpression(String expr) throws Exception {
            // Split expression into tokens
            String[] tokens = expr.split(" ");
            Stack<Double> values = new Stack<>();
            Stack<Character> operators = new Stack<>();
            
            for (String token : tokens) {
                if (token.matches("[0-9.]+")) {
                    values.push(Double.parseDouble(token));
                } else if (token.matches("[+\\-*/]")) {
                    while (!operators.isEmpty() && precedence(token.charAt(0)) <= precedence(operators.peek())) {
                        values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                    }
                    operators.push(token.charAt(0));
                }
            }

            // Apply remaining operators
            while (!operators.isEmpty()) {
                values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
            }

            return values.pop();
        }

        private int precedence(char op) {
            switch (op) {
                case '+':
                case '-':
                    return 1;
                case '*':
                case '/':
                    return 2;
                default:
                    return -1;
            }
        }

        private double applyOperator(char op, double b, double a) {
            switch (op) {
                case '+':
                    return a + b;
                case '-':
                    return a - b;
                case '*':
                    return a * b;
                case '/':
                    if (b != 0) {
                        return a / b;
                    } else {
                        throw new ArithmeticException("Division by zero.");
                    }
                default:
                    return 0;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorGUI::new);
    }
}
