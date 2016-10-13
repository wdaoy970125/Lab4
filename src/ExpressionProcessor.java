/**
 * @author hp
 * UTF-9 encoding
 */

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class ExpressionProcessor {
	private boolean[] vars = new boolean[26];	// store the variables existing in polynomial
	private String expression = null;	// store the correct polynomial
	private int preIndexOfExpression;	// the pre index in the expressions(contain polynomial, simplify command, and derivation command)
	private int postIndexOfExpression;	// the post index in the expressions(contain polynomial, simplify command, and derivation command)
	private boolean isLegal;	// the legality of expressions(contain polynomial, simplify command, and derivation command)
	private boolean simplify = false;	// the current operation should be simplified or not	
	private boolean drivate = false;	// the current operation should be derivated or not
	private ArrayList<Element> subExpressions = new ArrayList<>();	// store the subexpressions of legal polynomial	
	
	public static final int NULL_EXPRESSION = 0;	// null expression
	public static final int GRAMMAR_ERROR = 1;	// grammar error
	public static final int NONE_VARIABLE = 2;	// no such variable(s)
	public static final int NULL_INPUT = 3;	// null input
	public static final String[] ERROR_MESSAGES = {"Error, please input an initial polynomial!", "Error, input error!", "Error, no such variable!", "Error, please input!"};
	
	
	private void proceException(int errorType) {
		System.out.println(ERROR_MESSAGES[errorType]);
	}
	
	// check the input is whether legal or not, and process it primarily
	public void expression (String tempInput) {
		this.preIndexOfExpression = 0;
		this.postIndexOfExpression = 0;
		// check the type of input and its legality
		if (tempInput.equals("")) {
			this.proceException(NULL_INPUT);
			return;
		}
		else if (tempInput.charAt(postIndexOfExpression) != '!') {	// if it's polynomial
			String input = this.deleteWhitespace(tempInput);
			while (postIndexOfExpression < input.length()) {
				if (Character.isLetter(input.charAt(postIndexOfExpression))) {	// if the current character is a letter
					if (postIndexOfExpression != input.length() - 1) {
						if (input.charAt(postIndexOfExpression + 1) != '+' && input.charAt(postIndexOfExpression + 1) != '*') {
							this.isLegal = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
					}
					postIndexOfExpression++;
				}
				else if (Character.isDigit(input.charAt(postIndexOfExpression))) {	// if the current character is a digit
					String temp = "";
					while (postIndexOfExpression < input.length() && input.charAt(postIndexOfExpression) != '+' && input.charAt(postIndexOfExpression) != '*') {
						temp += input.charAt(postIndexOfExpression);
						postIndexOfExpression++;
					}
					try {
						Double.parseDouble(temp);
					}
					catch (Exception e) {
						this.isLegal = false;
						this.proceException(GRAMMAR_ERROR);
						return;
					}
				}
				else if (input.charAt(postIndexOfExpression) == '+' || input.charAt(postIndexOfExpression) == '*') {	// if the current character is '+' or '*' 
					if (postIndexOfExpression == input.length() - 1) {
						this.isLegal = false;
						this.proceException(GRAMMAR_ERROR);
						return;
					}
					else if (!Character.isDigit(input.charAt(postIndexOfExpression + 1)) && !Character.isLetter(input.charAt(postIndexOfExpression + 1))) {
						this.isLegal = false;
						this.proceException(GRAMMAR_ERROR);
						return;
					}
					postIndexOfExpression++;
				}
				else {
					this.isLegal = false;
					this.proceException(GRAMMAR_ERROR);
					return;
				}
			}
			// process the initial polynomial
			this.expression = input;
			subExpressions.clear();
			String subTemp;	// temp store the subexpressions of polynomial
			for (postIndexOfExpression = 0; postIndexOfExpression <= expression.length(); postIndexOfExpression++) {
				if (postIndexOfExpression != expression.length() && expression.charAt(postIndexOfExpression) != '+') 
					continue;
				subTemp = expression.substring(preIndexOfExpression, postIndexOfExpression);
				Element subTempExpression = new Element();	// temp store the subexpressions of polynomial
				String doubleSubTemp;	// the factor of subexpressions
				int i, j;
				i = j = 0;
				for (; j <= subTemp.length(); j++) {
					if (j != subTemp.length() && subTemp.charAt(j) != '*')
						continue;
					doubleSubTemp = subTemp.substring(i, j);
					if (Character.isLetter(doubleSubTemp.charAt(0))) {
						subTempExpression.setVariablesAdd(doubleSubTemp);
						subTempExpression.setIsVariable(true);
						this.vars[doubleSubTemp.charAt(0) - 97] = true;
					}
					else {
						subTempExpression.setCofficientMul(Double.parseDouble(doubleSubTemp));
					}
					i = j + 1;
				}
				subTempExpression.sort();
				subExpressions.add(subTempExpression);
				preIndexOfExpression = postIndexOfExpression + 1;
			}
			this.isLegal = true;
			this.simplify = false;
			this.drivate = false;
			System.out.println(this.expression);
		}
		else if (this.expression != null) {
			if (tempInput.length() == 5 && tempInput.substring(1, 4).equals("d/d")) {
				this.isLegal = true;
				this.simplify = false;
				this.drivate = true;
			}
			else if (tempInput.length() >= 13 && tempInput.substring(1, 9).equals("simplify")) {
				// check the legality of simplify command
				postIndexOfExpression = 10;
				int[] var = new int [26];
				while (postIndexOfExpression < tempInput.length()) {
					if (Character.isLetter(tempInput.charAt(postIndexOfExpression))) {	// if the current character is a letter
						if (var[tempInput.charAt(postIndexOfExpression) - 97] == 1) {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
						else if (!this.vars[tempInput.charAt(postIndexOfExpression) - 97]) {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(NONE_VARIABLE);
							return;
						}
						else if (postIndexOfExpression == tempInput.length() - 1) {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
						else if (tempInput.charAt(postIndexOfExpression + 1) != '=') {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
						var[tempInput.charAt(postIndexOfExpression) - 97] = 1;
						postIndexOfExpression++;
					}
					else if (tempInput.charAt(postIndexOfExpression) == '=') {
						if (postIndexOfExpression == tempInput.length() - 1) {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
						else if (!Character.isDigit(tempInput.charAt(postIndexOfExpression + 1))) {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
						postIndexOfExpression++;
					}
					else if (Character.isDigit(tempInput.charAt(postIndexOfExpression))) {	// if the current character is a didit
						String temp = "";
						while (postIndexOfExpression < tempInput.length() && !Character.isWhitespace(tempInput.charAt(postIndexOfExpression))) {
							temp += tempInput.charAt(postIndexOfExpression);
							postIndexOfExpression++;
						}
						try {
							Double.parseDouble(temp);
						}
						catch (Exception e) {
							this.isLegal = false;
							this.simplify = false;
							this.drivate = false;
							this.proceException(GRAMMAR_ERROR);
							return;
						}
					}
					else if (tempInput.charAt(postIndexOfExpression) == ' ') {
						if (postIndexOfExpression != tempInput.length() - 1) {
							if (!Character.isLetter(tempInput.charAt(postIndexOfExpression + 1))) {
								this.isLegal = false;
								this.simplify = false;
								this.drivate = false;
								this.proceException(GRAMMAR_ERROR);
								return;
							}
						}
						postIndexOfExpression++;
					}
					else {
						this.isLegal = false;
						this.simplify = false;
						this.drivate = false;
						this.proceException(GRAMMAR_ERROR);
						return;
					}
				}
				this.isLegal = true;
				this.simplify = true;
				this.drivate = false;
			}
			else {
				this.isLegal = false;
				this.simplify = false;
				this.drivate = false;
				this.proceException(GRAMMAR_ERROR);
			}
		}
		else {
			this.proceException(NULL_EXPRESSION);
			this.isLegal = false;
			this.simplify = false;
			this.drivate = false;
		}
	}
	
	// clone a copy of subexpressions
	public ArrayList<Element> clone (ArrayList<Element> subExpressions) {
		ArrayList<Element> cloneExpressions = new ArrayList<>();		
		for (int i = 0; i < subExpressions.size(); i++) {
			Element temp = new Element();
			temp.setIsVariable(subExpressions.get(i).getIsVariable());
			temp.setCofficientMul(subExpressions.get(i).getCofficient());
			temp.setVariables(subExpressions.get(i).getVariables());
			cloneExpressions.add(temp);
		}
		return cloneExpressions;
	}
	
	// simplify polynomial
	public void simplify (String input) {
		ArrayList<Element> simplifyExpression = new ArrayList<>();
		simplifyExpression = this.clone(this.subExpressions);
		postIndexOfExpression = 9;
		while (postIndexOfExpression < input.length()) {
			if (!Character.isLetter(input.charAt(postIndexOfExpression))) {
				postIndexOfExpression++;
				continue;
			}
			preIndexOfExpression = postIndexOfExpression + 2;
			char var = input.charAt(postIndexOfExpression);
			for (int i = 0; i < simplifyExpression.size(); i++) {
				if (simplifyExpression.get(i).getIsVariable()) {
					int j = 0;
					int count = 0;
					while (j < simplifyExpression.get(i).getVariables().length()) {
						if (simplifyExpression.get(i).getVariables().charAt(j) == var)
							count++;
						j++;
					}
					while (postIndexOfExpression < input.length() && input.charAt(postIndexOfExpression) != ' ')
						postIndexOfExpression++;
					String temp = input.substring(preIndexOfExpression, postIndexOfExpression);
					simplifyExpression.get(i).setCofficientMul(Math.pow(Double.parseDouble(temp), count));
					String subTemp = "";
					for (int k = 0; k < simplifyExpression.get(i).getVariables().length(); k++) {
						if (simplifyExpression.get(i).getVariables().charAt(k) == var)
							continue;
						subTemp += simplifyExpression.get(i).getVariables().charAt(k);
					}
					simplifyExpression.get(i).setVariables(subTemp);
				}
			}
		}
		this.combine(simplifyExpression);
	}
	
	// derivate polynomial
	public void derivation (String input) {
		ArrayList<Element> derivateExpression = new ArrayList<>();
		derivateExpression = this.clone(this.subExpressions);
		for (int i = 0; i < derivateExpression.size(); i++) {	//遍历表达式子项
			if (derivateExpression.get(i).getIsVariable()) {	//子项存在变量
				int count = 0;
				int j = 0;
				while (j < derivateExpression.get(i).getVariables().length()) {
					if (derivateExpression.get(i).getVariables().charAt(j) == input.charAt(4))
						count++;
					j++;
				}
				if (count > 0) {
					derivateExpression.get(i).setCofficientMul(count);
					String tempDerivateExpression = "";
					for (int k = 0; k < derivateExpression.get(i).getVariables().length(); k++) {
						if (derivateExpression.get(i).getVariables().charAt(k) == input.charAt(4))
							continue;
						tempDerivateExpression += derivateExpression.get(i).getVariables().charAt(k);
					}
					if (count == 2) {
						tempDerivateExpression += input.charAt(4);
					}
					else if (count > 2) {
						tempDerivateExpression += input.charAt(4) + "^" + (count - 1);
					}
					derivateExpression.get(i).setVariables(tempDerivateExpression);
				}
				else if (count == 0) {
					derivateExpression.get(i).setCofficientMul(0);
					derivateExpression.get(i).setVariables("");
				}
			}
			else {
				derivateExpression.get(i).setCofficientMul(0);
				derivateExpression.get(i).setVariables("");
			}
		}
		this.combine(derivateExpression);
	}
	
	// merge similar subexpression
	public void combine (ArrayList<Element> expression) {
		for (int i = 0; i < expression.size(); i++) {
			for (int j = 0; j <= i; j++) {
				if (expression.get(j).getVariables().equals(expression.get(i).getVariables())) {
					if (j != i) {
						expression.get(j).setCofficientAdd(expression.get(i).getCofficient());
						expression.remove(expression.get(i));
						i--;
						break;
					}
				}
			}
		}
		String output = "";
		for (int i = 0; i < expression.size(); i++) {
			if (expression.get(i).getCofficient() == 0) {
				continue;
			}
			else if (expression.get(i).getCofficient() == 1) {
				if (i != 0)
					output += "+";
				if (expression.get(i).getIsVariable() && expression.get(i).getVariables() != "")
					output += expression.get(i).getVariables();
				else
					output += expression.get(i).getCofficient();
			}
			else {
				if (i != 0)
					output += "+";
				output += expression.get(i).getCofficient();
				if (expression.get(i).getIsVariable() && expression.get(i).getVariables() != "")
					output += "*" + expression.get(i).getVariables();
			}
		}
		if (output == "")
			System.out.println("0.0");
		else
			System.out.println(output);
	}
	
	// delete the whitespace existing in polynomial
	public String deleteWhitespace (String tempInput) {
		String input = "";
		for (int i = 0; i < tempInput.length(); i++) {
			if (Character.isWhitespace(tempInput.charAt(i)))
				continue;
			input += tempInput.charAt(i);
		}
		return input;
	}
	
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		ExpressionProcessor calculator = new ExpressionProcessor();
		while(true) {
			System.out.print("> ");
			String input = in.nextLine();
			calculator.expression(input);
			if (calculator.isLegal && calculator.simplify)
				calculator.simplify(input);
			else if (calculator.isLegal && calculator.drivate)
				calculator.derivation(input);
		}
	}
}

// the store structure of subexpression
class Element {
	private boolean isVariable = false;	// check if there exists variable or not
	private double coefficient = 1;	// the coefficient of subexpression
	private String variables = "";	// the vars of subexpression

	public void setIsVariable (boolean isVariable) {
		this.isVariable = isVariable;
	}
	
	public boolean getIsVariable () {
		return this.isVariable;
	}
	
	public void setCofficientMul (double coefficient) {
		this.coefficient *= coefficient;
	}
	
	public void setCofficientAdd (double coefficient) {
		this.coefficient += coefficient;
	}
	
	public double getCofficient () {
		return this.coefficient;
	}
	
	public void setVariablesAdd (String variables) {
		this.variables += variables;
	}
	
	public void setVariables (String variables) {
		this.variables = variables;
	}
	
	public String getVariables () {
		return this.variables;
	}
	public void sort () {
		byte[] tempStr = this.variables.getBytes();
		Arrays.sort(tempStr);
		this.variables = new String(tempStr);
	}
}