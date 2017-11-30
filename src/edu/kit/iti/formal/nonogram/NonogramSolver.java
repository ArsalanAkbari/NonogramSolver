package edu.kit.iti.formal.nonogram;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale.FilteringMode;
import java.io.*;
import java.util.*;

import javax.print.attribute.standard.RequestingUserName;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.minisat.constraints.cnf.Clauses;
import org.sat4j.minisat.core.Solver;
import org.sat4j.minisat.core.SolverStats;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.lang.Object;

public class NonogramSolver {

	private String[] allGamesArray;
	private ArrayList<ArrayList<Integer>> gamesRow = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> gamesLine = new ArrayList<ArrayList<Integer>>();
	private ArrayList<String> gamesID = new ArrayList<>();

	private String[][] cell ;

	public static  ISolver solver ;



	public void getGameInfo() throws IOException, TimeoutException{


		solver = SolverFactory.newDefault();
		solver.newVar(1000000);
		solver.setExpectedNumberOfClauses(500000);
		getNonogramsFromFile();

		System.out.println(allGamesArray[3]);
		//for(String game : allGamesArray){// baraye har game anjam midahim nobati

		getNonogramsID_Line_Row( allGamesArray[3] );
		//}

		initialVariableForEachCell();
		asignVariableForEachRowArrayMembers();


		printGame();
	}


	private void getNonogramsFromFile() throws IOException {

		String everything = "";
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\arsal\\eclipse-Project\\Nonogram\\files\\nonograms.txt"));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} finally {
			br.close();
		}

		allGamesArray = everything.split("!");


		/*for (int i = 0; i < allGamesArray.length; i++) {
			//System.out.println(allGamesArray[3]);
		}*/
	}


	private void getNonogramsID_Line_Row(String game) {

		String[] idSpliter ;
		String[] memberSpliter;
		String[] lineRowSpliter;
		String[] rowSpliter;
		String[] lineSpliter;

		try{
			//if(game.length() <= 6)//214 khali ba size 2 chap mikone

			idSpliter = game.split(" ");
			gamesID.add(idSpliter[0]);
			System.out.println("ID  " +idSpliter[0]);

			memberSpliter = game.split(":");

			lineRowSpliter = memberSpliter[1].split("\\.");

			System.out.println("Row   " + lineRowSpliter[0]); 
			addToRowArrayList(lineRowSpliter[0]);

			lineSpliter = lineRowSpliter[1].split(";");
			System.out.println("line   " + lineRowSpliter[1]);
			addToLineArrayList(lineRowSpliter[1]);
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println(e.getMessage());
		}


	}


	private void addToRowArrayList(String lineRowSpliter) {

		String[] rowSpliter = lineRowSpliter.split(";");

		for (int i = 0; i < rowSpliter.length; i++) {

			ArrayList<Integer> row = new ArrayList<Integer>();

			if(rowSpliter[i].equals("")){//vaghti ke hich adadi nabashe -;5;6;6
				row.add(0);
				gamesRow.add(row);
			}

			if(rowSpliter[i].contains(",")){
				for (int j = 0; j < rowSpliter[i].length(); j++) {
					if(rowSpliter[i].charAt(j) == ',')
						continue;
					else
						row.add(Character.getNumericValue(rowSpliter[i].charAt(j))); 
				}
				gamesRow.add(row);
			}

			if(!rowSpliter[i].contains(",") && !rowSpliter[i].equals("")){
				row.add(Integer.parseInt(rowSpliter[i]));
				gamesRow.add(row);
			}
			//row[i] = Integer.parseInt(lineRowSpliter[i]);

		}
		System.out.println("games Row  " +gamesRow.clone() +"\n");
	}


	private void addToLineArrayList(String lineRowSpliter) {

		String[] lineSpliter = lineRowSpliter.split(";");

		for (int j = 0; j < lineSpliter.length; j++) {

			ArrayList<Integer> row = new ArrayList<Integer>();

			if(lineSpliter[j].equals("")){//vaghti ke hich adadi nabashe -;5;6;6
				row.add(0);
				gamesLine.add(row);
			}

			if(lineSpliter[j].contains(",")){
				for (int k = 0; k < lineSpliter[j].length(); k++) {
					if(lineSpliter[j].charAt(k) == ',')
						continue;
					else
						row.add(Character.getNumericValue(lineSpliter[j].charAt(k))); 
				}
				gamesLine.add(row);
			}

			if(!lineSpliter[j].contains(",") && !lineSpliter[j].equals("")){
				row.add(Integer.parseInt(lineSpliter[j]));
				gamesLine.add(row);
			}
			//row[i] = Integer.parseInt(lineRowSpliter[i]);

		}
		System.out.println("games line  " +gamesLine.clone() +"\n");

	}


	private void initialVariableForEachCell() {

		cell = new String[gamesRow.size()][ gamesLine.size()];

		//Assign an integer variable xij ∈ {0, 1} for each cell (xij = 1means black)

		for(int i=0 ; i<gamesRow.size() ; i++){//row
			for(int j=0 ; j<gamesLine.size() ; j++){//line

				cell[i][j] = "-";
			}
		}
	}


	private void asignVariableForEachRowArrayMembers() throws TimeoutException {
		/*
		 * if cell[0][j] = true    vaghti ke masalan currentRowMember[0][0] {4}
		 * then (h[0][0] <= j && j<h[0][0]+4)  //j number of cuurentLineMember
		 */

		for(ArrayList<Integer> rowMember : gamesRow){

			findAllPossibleLineToBlackPositions(rowMember);
		}

		System.out.println("_____________________________");

		/*for(ArrayList<Integer> list :allPossibleLineToBlackPositions){//be tedade azaye {2,3}
			for(int j=0 ; j<list.size() ; j++){//line

				System.out.print(list.get(j) +" ");
			}
			System.out.println();
		}*/
	}


	private void findAllPossibleLineToBlackPositions( ArrayList<Integer> rowMember) throws TimeoutException {

		ArrayList<ArrayList<Integer>> allPossibleLineToBlackPositions = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> currentRowsLineToBlackPositions;

		int rowNumber = gamesRow.indexOf(rowMember);

		for(int k=0 ; k<rowMember.size() ; k++){//be tedade azaye {2,3}

			currentRowsLineToBlackPositions = new ArrayList<Integer>();

			for(int j=1 ; j<=gamesLine.size() ; j++){//line  //az 1 shoro mikonim chon solver 0 nadare

				if(j + rowMember.get(k) -1 <= gamesLine.size()){
					currentRowsLineToBlackPositions.add(j) ;//hamon h00  j yani inke az koja shoro kone siyah kone
					//System.out.print(currentRowsLineToBlackPositions.get(possibleSolutionNum) + " ");
				}
			}
			allPossibleLineToBlackPositions.add(currentRowsLineToBlackPositions);

			makingClause(allPossibleLineToBlackPositions , rowNumber);

			System.out.println("allPossibleLineToBlackPositions  " +allPossibleLineToBlackPositions.clone());
		}
	}


	private void makingClause(ArrayList<ArrayList<Integer>> allPossibleLineToBlackPositions , int rowNumber) throws TimeoutException {
		//making clause

		ArrayList<int[]> allPossibleLineToBlackPositionsInArrayForm = new ArrayList<int[]>();
		int[] rowMemberPssibleSolution;

		for(ArrayList<Integer> currentPossibleLineToBlackPositions : allPossibleLineToBlackPositions){

			rowMemberPssibleSolution = new int [currentPossibleLineToBlackPositions.size()];

			for (int i = 0; i < currentPossibleLineToBlackPositions.size(); i++) {

				rowMemberPssibleSolution[i] =  currentPossibleLineToBlackPositions.get(i);
			}
			allPossibleLineToBlackPositionsInArrayForm.add(rowMemberPssibleSolution);
		}

		blackCellsRow(allPossibleLineToBlackPositionsInArrayForm ,rowNumber );

	}



	private void blackCellsRow(ArrayList<int[]> allPossibleLineToBlackPositionsInArrayForm ,int rowNumber) throws TimeoutException {

		int membersCount= allPossibleLineToBlackPositionsInArrayForm.size();



		//for(int[] rowMemberPssibleSolution :allPossibleLineToBlackPositionsInArrayForm){


		System.out.println(allPossibleLineToBlackPositionsInArrayForm.get(0));

		//int rowMemberNum = allPossibleLineToBlackPositionsInArrayForm.indexOf(rowMemberPssibleSolution);
		int rowMemberNum = 0;
		System.out.println("rowMemberNum " +rowMemberNum);

		int[] model = new int[50];
				addClause(allPossibleLineToBlackPositionsInArrayForm.get(0) ,model);

		for (int i = 0; i < model.length; i++) {

			if(model[i] > 0){
				System.out.println("sdfdsfsfsfs");

				for(int j = 0 ;j<gamesLine.size(); j++ ){ //size =10

					if(model[i]-1 <=j  &&  j < model[i]-1 +gamesRow.get(rowNumber).get(rowMemberNum) ){

						if( rowMemberNum == 0 ){
							cell[rowNumber][j] = "#";

						}

						else if(rowMemberNum!=0 ){

							for(int k = 0 ;k<gamesLine.size(); k++ ){
								if(cell[rowNumber][k] !="#" && model[i]-1 +gamesRow.get(rowNumber).get(rowMemberNum-1) < gamesRow.get(rowNumber).get(rowMemberNum) ){
									System.out.println("dd");
									cell[rowNumber][j] = "#";
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	//}



	private void addClause(int[] rowMemberPssibleSolution ,int[] model) throws TimeoutException {


		
		int curentMember = 0;
		int[] clause = new int[rowMemberPssibleSolution.length]; 

		for (int i = 0; i < rowMemberPssibleSolution.length; i++) {

			for (int j = 0; j < rowMemberPssibleSolution.length; j++) {

				clause[j]=rowMemberPssibleSolution[j];
			}

			clause[curentMember] = rowMemberPssibleSolution[curentMember];

			for (int j = 0; j < clause.length; j++) {

				if(j!= curentMember){
					clause[j] = (-1)*clause[j];
				}

				System.out.print(clause[j] +" ");

			}
			curentMember++;
			System.out.println();
			//System.out.println(model.length);
			
			model = solver(clause);
			

		}

		
	}


	private int[] solver(int[] clause) throws TimeoutException{


		int[] model = new int[50];
		VecInt IVecInt = new VecInt(clause);
		try {
			solver.addClause(IVecInt);
			
		} catch (ContradictionException e) {
			
			e.printStackTrace();
		}
		
		
		IProblem problem = solver;
		System.out.println("IVecInt" +IVecInt);

		if (problem.isSatisfiable()) {
			
			model = problem.model();
			
			for (int i = 0; i < model.length; i++) {
				
				System.out.print(model[i] + " ");
			}
			System.out.println();
		} 

		else {
			System.out.println("not ");
		}
		return model;




	}


	/*private void blackCellsRow(int[] model, int rowNumber , int rowMemberNum) {


		System.out.println("rowNumber " + rowNumber);
		System.out.println("gamesRow member "+ gamesRow.get(rowNumber).get(rowMemberNum));
		System.out.println();

		try{

			for (int i = 0; i < model.length; i++) {

				if(model[i] > 0){

					for(int j = 0 ;j<gamesLine.size(); j++ ){ //size =10

						if(model[i] <=j  &&  j < model[i] +gamesRow.get(rowNumber).get(rowMemberNum) ){

							cell[rowNumber][j] = 1;

							/*if(k == 0){
							cell[rowNumber][j] = 1;
						}

						else if(k!=0   && rowRunBlack[rowNumber][k] +rowMember.get(k) < rowRunBlack[rowNumber][k] ){
							System.out.println("dd");
							cell[rowNumber][j] = 1;
						}*/
	/*		}
					}
				}
			}
		}
		catch(NullPointerException e){

			e.printStackTrace();
		}

	}*/

	private void printGame() {

		System.out.println();

		for(int i=0 ; i<gamesRow.size() ; i++){//row
			for(int j=0 ; j<gamesLine.size() ; j++){//line
				System.out.print(cell[i][j]);
			}
			System.out.println();
		}
	}
}
