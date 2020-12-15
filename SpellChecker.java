/**
 * @author Nico Neiman 15-12-2020
 * @context Project created for the Class CS1103. B.Sc. Computer Science. University of the People.
 */
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.JFileChooser;

public class SpellChecker {
	static Scanner fileIn;
	static HashSet<String> dictionary = new HashSet();
	static HashSet<String> misspelledWords = new HashSet();
	
	/**
	 * Static method used to populate a HashSet with words in a file.
	 * @param path. The path of the file to be used to populate the HashSet 
	 * @throws IOException if the file does not exist.
	 */
	public static void populateHashSet(String path) throws IOException {
		try {
			fileIn = new Scanner (new File(path));
			while (fileIn.hasNext()) {
				String wordOnFile = fileIn.next();
				dictionary.add(wordOnFile.toLowerCase());
			}
		} catch ( IOException e) {
			System.out.print(e);
		}
	}

	/**
	 * Lets the user select an input file using a standard file
	 * selection dialog box.  If the user cancels the dialog
	 * without selecting a file, the return value is null.
	 */
	static File getInputFileNameFromUser() {
		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle("Select File for Input");
		int option = fileDialog.showOpenDialog(null);
		if (option != JFileChooser.APPROVE_OPTION)
			return null;
		else
			return fileDialog.getSelectedFile();
	}

	static TreeSet corrections(String badWord, HashSet dictionary) {
		/**
		 * This method handles five possible cases to identify possible corrections to the misspelled word.
		 * The cases are detailed below.
		 * @param badWord. The misspelled word.
		 * @param disctionary. The HashSet containing all the words used as reference to provide corrections.
		 * @return a TreeSeet containing the possible corrections for the misspelled word.
		 * @return if no possible corrections are available, then the TreeSet would be empty.
		 */

		TreeSet<String> possibleCorrections = new TreeSet();

		// 		Case 1: Delete any one of the letters from the misspelled word.
		for (int i=0; i<badWord.length(); i++) {
			StringBuilder temp = new StringBuilder(badWord);
			String wordWithDeletedChar = temp.deleteCharAt(i).toString();//Manipulate and cast to String
			if(dictionary.contains(wordWithDeletedChar)) {
				/*
				 * If the after removing any letter from the bad word
				 * there is match in the dictionary,
				 * Proceed to add the word to the TreeSet of possibleCorrections
				 */
				possibleCorrections.add(wordWithDeletedChar);
			}
		}
		
		//		Case 2: Change any letter in the misspelled word to any other letter.
		for (int i=0; i<badWord.length(); i++) {
			for (char ch='a'; ch<='z'; ch++) {
				String wordWithChangedChar = badWord.substring(0, i) + ch + badWord.substring(i+1);
				if(dictionary.contains(wordWithChangedChar)) {
					/*
					 * If the after changing any letter from the bad word
					 * there is match in the dictionary,
					 * Proceed to add the word to the TreeSet of possibleCorrections
					*/
					possibleCorrections.add(wordWithChangedChar);
				}
			}
		}
		
		//		Case 3: Insert any letter at any point in the misspelled word.
		for (int i=0; i<=badWord.length(); i++) {
			for (char ch='a'; ch<='z'; ch++) {
				String wordWithChangedChar = badWord.substring(0, i) + ch + badWord.substring(i, badWord.length());
				
				if(dictionary.contains(wordWithChangedChar)) {
					/*
					 * If the after inserting any letter at any point in the bad word
					 * there is match in the dictionary,
					 * Proceed to add the word to the TreeSet of possibleCorrections
					*/
					possibleCorrections.add(wordWithChangedChar);
				}
			}
		}
		
		//		Case 4: Swap any two neighboring characters in the misspelled word.
		for (int i=1; i<badWord.length(); i++) {
			StringBuilder mutableString = new StringBuilder(badWord);
			
			mutableString.setCharAt(i-1, badWord.charAt(i));//Switch the char a from mutableString with b from badWord
			mutableString.setCharAt(i, badWord.charAt(i-1));//Switch the char b from mutableString with a from badWord
			String wordWithSwappedChars = mutableString.toString();//Cast to String
			
			if(dictionary.contains(wordWithSwappedChars)) {
				/*
				 * If there is match in the dictionary,
				 * Proceed to add the word to the TreeSet of possibleCorrections
				*/
				possibleCorrections.add(wordWithSwappedChars);
			}
		}
		
		//		Case 5: Insert a space at any point in the misspelled word
		//		(and check that both of the words that are produced are in the dictionary)
		for (int i=0; i<badWord.length(); i++) {
			StringBuilder mutableString = new StringBuilder(badWord);
			mutableString.setCharAt(i, ' ');
			String[] arrayOfWords = mutableString.toString().split(" ");
			
			for(String word:arrayOfWords) {
				if(dictionary.contains(word)) {
					/*
					 * If there is match in the dictionary,
					 * Proceed to add the word to the TreeSet of possibleCorrections
					*/
					possibleCorrections.add(word);
				}
			}

		}
		return possibleCorrections;
	}
	public static void main (String[] args) throws IOException {
		populateHashSet(".\\src\\words.txt"); // Populate the HashSet
		File fileFromUser = getInputFileNameFromUser(); //Get and read the file from User
		Scanner readFileFromUser = new Scanner(fileFromUser);
		readFileFromUser.useDelimiter("[^a-zA-Z]+");// Ignore any non-letter characters
		
		while (readFileFromUser.hasNext()) {
			String wordOnFile = readFileFromUser.next().toLowerCase();
			if(!dictionary.contains(wordOnFile)) {

				/**
				 * If the word was already detected,
				 * then continue to next words to avoid duplication
				 */
				if(misspelledWords.contains(wordOnFile)) {
					continue;
				} else {
					misspelledWords.add(wordOnFile);
				}

				System.out.println("\nThe following word was misspelled: " + wordOnFile);
				System.out.print("Possible corrections:");

				TreeSet corrections = corrections(wordOnFile, dictionary);

				if(corrections.size() == 0) {
					System.out.print("(no suggestions)");
				} else {
					Iterator<String> iteration = corrections.iterator(); //Convert to Iterator
					while(iteration.hasNext()){  
						System.out.print(" " + iteration.next());  
					}  
				}

			}
		}
		readFileFromUser.close(); //Close Scanner to preserve memory resources
	}
}
