package com.aksh.pa.process.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.aksh.pa.process.ProcessWrapper;
import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.parameter.UndefinedParameterError;

import common.Logger;

public class RapidMinerProcessImpl
		implements
			ProcessWrapper,
			ApplicationContextAware {

	private File processXmlDefinition;
	Process processInstance;
	IOContainer ioInput = null;
	IOContainer ioResult;
	private static final Logger logger = Logger
			.getLogger(RapidMinerProcessImpl.class);

	@Override
	public void loadProcess() {
		try {
			RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
			RapidMiner.init();
			processInstance = new Process(processXmlDefinition);
		} catch (Exception e) {
			logger.error("Error", e);
			throw new RuntimeException(e);
		}

	}

	@Override
	public void setParameter(Map<String, Object> parameters) {
		this.parametersMap = parameters;
	}

	@Override
	public void preProcess() {
		try {
			logger.info(processInstance.getAllOperatorNames());
			Operator operatorProcessDocs = processInstance
					.getOperator("Process Documents from Files");

			printFileParamList(operatorProcessDocs);
			List<String> emailFileList = (List<String>) this.parametersMap
					.get(KEY_EMAIL_FILE_LIST);
			List<String[]> fileParamList = new ArrayList<String[]>();
			int id = 1;
			for (String string : emailFileList) {
				fileParamList.add(new String[]{"" + id++, string});
			}
			operatorProcessDocs.setListParameter("text_directories",
					fileParamList);
			printFileParamList(operatorProcessDocs);

			Operator operatorWriteCSV = processInstance
					.getOperator("Write CSV");
			operatorWriteCSV.setParameter("csv_file",
					(String) this.parametersMap.get(KEY_RESULT_FILE));
			logger.info(processInstance.toString());

		} catch (Exception e) {
			logger.error("Error", e);
			throw new RuntimeException(e);
		}

	}

	private void printFileParamList(Operator operator)
			throws UndefinedParameterError {
		List<String[]> params = operator.getParameterList("text_directories");
		for (String[] strings : params) {
			logger.info(ArrayUtils.toString(strings));
		}
	}

	@Override
	public void run() {
		try {
			ioResult = processInstance.run(ioInput);
		} catch (OperatorException e) {
			logger.error("Error", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void processResults() {
		if (ioResult.getElementAt(0) instanceof ExampleSet) {
			ExampleSet resultSet1 = (ExampleSet) ioResult.getElementAt(0);

			int rowNum = 0;
			for (Example example : resultSet1) {
				Iterator<Attribute> allAtts = example.getAttributes()
						.allAttributes();
				System.out.print(rowNum++ + ",");
				while (allAtts.hasNext()) {
					Attribute a = allAtts.next();
					if (a.isNumerical()) {
						double value = example.getValue(a);
						System.out.print(a.getName() + "-" + value + "|");

					} else {
						String value = example.getValueAsString(a);
						System.out.print(a.getName() + "-" + value + "|");
					}
				}
				System.out.println();
			}
		}

	}

	public void setProcessXmlDefinition(File processXmlDefinition) {
		this.processXmlDefinition = processXmlDefinition;
	}

	@Override
	public void setApplicationContext(ApplicationContext arg0)
			throws BeansException {
		this.myapApplicationContext = arg0;
	}

	private Map<String, Object> parametersMap = null;
	private ApplicationContext myapApplicationContext = null;
}
