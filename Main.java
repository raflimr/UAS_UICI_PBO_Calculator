// Importing necessary libraries for the program
import javax.swing.*; // For creating GUI components like frames, buttons, and text fields.
import java.awt.*; // For graphical elements such as layouts, fonts, and colors.
import java.awt.event.ActionEvent; // For handling events like button clicks.
import java.awt.event.ActionListener; // Interface for handling ActionEvent (e.g., button clicks).
import java.awt.event.KeyAdapter; // Simplifies handling keyboard events by overriding necessary methods.
import java.awt.event.KeyEvent; // Represents keypress events (e.g., key pressed, released, or typed).

// Main class to create a calculator application
public class Main {
    public static void main(String[] args) {
        // Create the main frame for the calculator
        JFrame frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        // Create a text field for user input/output and configure its properties
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 24));
        textField.setHorizontalAlignment(JTextField.RIGHT);
        frame.add(textField, BorderLayout.NORTH);

        // Add key listener to handle Enter and Delete key functionality
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Evaluate the expression when Enter is pressed
                    textField.setText(evaluate(textField.getText()));
                } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    // Clear the text field when Delete is pressed
                    textField.setText("");
                }
            }
        });

        // Create a panel for calculator buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 4, 10, 10));

        // Define the calculator buttons
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C", "(", ")", "^"
        };

        // Add buttons to the panel and set their action listeners
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            panel.add(button);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String command = e.getActionCommand();
                    if (command.equals("=")) {
                        // Evaluate the expression when "=" button is pressed
                        textField.setText(evaluate(textField.getText()));
                    } else if (command.equals("C")) {
                        // Clear the text field when "C" button is pressed
                        textField.setText("");
                    } else {
                        // Append the button text to the current text field content
                        if (textField.getText().equals("0")) {
                            textField.setText(command);
                        } else {
                            textField.setText(textField.getText() + command);
                        }
                    }
                }
            });
        }

        // Add the button panel to the frame
        frame.add(panel);
        frame.setVisible(true);
    }

    // Method to evaluate the mathematical expression
    private static String evaluate(String expression) {
        try {
            // Calculate the result using a custom evaluation function
            double result = eval(expression);
            if (result == (long) result) {
                // Return result as integer if it's a whole number
                return String.format("%d", (long) result);
            } else {
                // Return result as double
                return String.format("%s", result);
            }
        } catch (Exception e) {
            // Return "Error" for invalid expressions
            return "Error";
        }
    }

    // Method to parse and evaluate the expression
    private static double eval(final String str) {
        class Parser {
            int pos = -1, c;

            // Advance to the next character
            void nextChar() {
                c = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            // Check and consume a specific character
            boolean eat(int charToEat) {
                while (c == ' ') nextChar();
                if (c == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            // Parse the entire expression
            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) c);
                return x;
            }

            // Parse an expression (handles addition and subtraction)
            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm(); // Addition
                    else if (eat('-')) x -= parseTerm(); // Subtraction
                    else return x;
                }
            }

            // Parse a term (handles multiplication and division)
            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor(); // Multiplication
                    else if (eat('/')) x /= parseFactor(); // Division
                    else return x;
                }
            }

            // Parse a factor (handles parentheses, numbers, and exponentiation)
            double parseFactor() {
                if (eat('+')) return parseFactor(); // Unary plus
                if (eat('-')) return -parseFactor(); // Unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // Parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((c >= '0' && c <= '9') || c == '.') { // Numbers
                    while ((c >= '0' && c <= '9') || c == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) c);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // Exponentiation

                return x;
            }
        }
        return new Parser().parse();
    }
}
