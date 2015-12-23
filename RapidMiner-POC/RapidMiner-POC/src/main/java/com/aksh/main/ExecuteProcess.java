package com.aksh.main;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.XMLException;

public class ExecuteProcess {
	public static void main(String[] args) throws Exception {
		ExampleSet resultSet1 = null;
		IOContainer ioInput = null;
		IOContainer ioResult;
		try {
			RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
			RapidMiner.init();
			com.rapidminer.Process pr = new Process(
					new File(
							"D:\\other-projects\\analytics-nlp\\rapid-miner\\process\\ScoreEval.rmp"));
			/*Operator op = pr.getOperator("Read Excel");
			op.setParameter(ExcelExampleSource.PARAMETER_EXCEL_FILE,
					"C:\\Users\\MP-TEST\\Desktop\\Rapid_Test\\HaendlerRatings_neu.xls");*/
			ioResult = pr.run(ioInput);
			if (ioResult.getElementAt(0) instanceof ExampleSet) {
				resultSet1 = (ExampleSet) ioResult.getElementAt(0);

				int rowNum=0;
				for (Example example : resultSet1) {
					Iterator<Attribute> allAtts = example.getAttributes()
							.allAttributes();
					System.out.print(rowNum+++",");
					while (allAtts.hasNext()) {
						Attribute a = allAtts.next();
						if (a.isNumerical()) {
							double value = example.getValue(a);
							System.out.print(a.getName()+"-"+value+"|");

						} else {
							String value = example.getValueAsString(a);
							System.out.print(a.getName()+"-"+value+"|");
						}
					}
					System.out.println();
				}
			}
		} catch (IOException | XMLException | OperatorException e) {
			e.printStackTrace();
		}

	}
}
