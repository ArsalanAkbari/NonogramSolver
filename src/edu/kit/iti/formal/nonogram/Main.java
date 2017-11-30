package edu.kit.iti.formal.nonogram;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.sat4j.specs.TimeoutException;

public class Main {

	public static void main(String[] args) throws IOException, TimeoutException {
		
		NonogramSolver nonogramSolver = new NonogramSolver();
		nonogramSolver.getGameInfo();

	}

}
