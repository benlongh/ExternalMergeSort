import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

//ExternalMergeSort.java
//CSc 345 Spring 16 - Project 2
//Benlong Huang
//

public class ExternalMergeSort {
	// the main part is to use to call the method below to do the calculation.
	public static void main(String[] args) throws IOException {
		// scan the input name which is named input, and PrintWrite the output
		// which is named output.
		String inputname = args[0];
		String outputname = args[1];
		// scan the whole file and name it input.
		Scanner input = new Scanner(new File(inputname));
		// scan it one more time for corner case that the input value in a file
		// is less than 16.then count how many numbers in that file.
		Scanner test = new Scanner(new File(inputname));
		int number = 0;
		while (test.hasNext()) {
			test.next();
			number++;
		}
		// create a new array called save and put the value in it and put them
		// in.
		// because test is use to count number above, so scan it again to get
		// the first value in the file again.
		test = new Scanner(new File(inputname));
		int i = 0;
		String save[] = new String[16];
		if (number <= 16) {
			while (test.hasNext()) {
				save[i] = test.next();
				i++;
			}
			// use insertion sort sort the array
			insertionsort(save);
			PrintWriter out = new PrintWriter(outputname);
			for (i = 0; i < number; i++)
				out.println(save[i]);
			out.close();
			return;
		}
		// count means how many pieces can a input file break.
		int count = breakintopiece(input);
		mergeName(count);
		mergeNumber("xms.tmp.pass_" + tempx1 + ".chunk_" + tempy1,
				"xms.tmp.pass_" + tempx2 + ".chunk_" + tempy2, outputname);
	}

	// this method contains break a input file into several pieces and each of
	// it contains at most 16 words, then write the content of that 16 words
	// into the file name "xms.tmp.pass_"
	// + String.format("%04d", pass) + ".chunk_"
	// + String.format("%04d", chunk)
	public static int breakintopiece(Scanner input) {
		int pass = 0;
		int chunk = 0;
		int count = 0;
		// this part create a array and put 16 words into the array each time.
		while (input.hasNext()) {
			String[] store = new String[16];
			for (int i = 0; i < 16; i++) {
				if (input.hasNext()) {
					store[i] = input.next();
				}
			}
			// call the insertionsort method below to sort the 16 words array.
			insertionsort(store);
			// put the sorted array into the file and repeat it.
			try {
				FileWriter fr = new FileWriter("xms.tmp.pass_"
						+ String.format("%04d", pass) + ".chunk_"
						+ String.format("%04d", chunk));

				BufferedWriter br = new BufferedWriter(fr);
				PrintWriter out = new PrintWriter(br);
				for (int i = 0; i < store.length; i++) {
					if (store[i] != null) {
						out.println(store[i]);
					} else {
						break;
					}
				}
				out.close();
			} catch (IOException e) {
				System.out.println(e);
			}
			chunk++;
			count++;
		}
		// check whether the last file I merge below is still the corner case or
		// not.
		if ((chunk - 1) % 2 == 0) {
			tempx1 = "0000";
			tempy1 = String.format("%04d", chunk - 1);
		}
		// the return value of this method is how many times this loop goes,
		// every time it put 16 words in a file then count++, then we can know
		// how many files we have.
		return count;
	}

	// I divided merge into two part, this part is use to merge the name, like
	// merge 0000 0001 and 0000 0002 will get 0001 0000.
	static String tempx1 = null;
	static String tempy1 = null;
	static String tempx2 = null;
	static String tempy2 = null;

	public static void mergeName(int count) throws IOException {
		int pass1 = 0;
		int pass2 = 1;
		int chunk1 = 0;
		int chunk2 = 0;
		String inputname1 = null;
		String inputname2 = null;
		String output = null;
		// the merge name part stop when the last result of file becomes 1, so
		// if there are 75 files, then after one time merge it will become 37,
		// then next time merge this 37 files it will only have 18 files, etc.
		// then at last there will be just one output. the loop over.
		while (count > 1) {
			for (chunk1 = 0; chunk1 < count - 1; chunk1 += 2) {
				inputname1 = "xms.tmp.pass_" + String.format("%04d", pass1)
						+ ".chunk_" + String.format("%04d", chunk1);
				inputname2 = "xms.tmp.pass_" + String.format("%04d", pass1)
						+ ".chunk_" + String.format("%04d", chunk1 + 1);
				output = "xms.tmp.pass_" + String.format("%04d", pass2)
						+ ".chunk_" + String.format("%04d", chunk2);
				mergeNumber(inputname1, inputname2, output);
				chunk2++;
			}
			// this step is the corner case that, when the number of file is
			// odd, then the last file will remains, like 73 merge with 74, then
			// 75 remain. so save the XXXX YYYY of the corner case in the String
			// I create and check, then merge the file that remains every time.

			if (tempx1 != null && tempy1 != null && tempx2 != null
					&& tempy2 != null) {
				inputname1 = "xms.tmp.pass_" + tempx1 + ".chunk_" + tempy1;
				inputname2 = "xms.tmp.pass_" + tempx2 + ".chunk_" + tempy2;
				output = "xms.tmp.pass_" + String.format("%04d", pass2)
						+ ".chunk_" + String.format("%04d", chunk2);
				// merge the file that remains.Then clean the String for next
				// round use.
				mergeNumber(inputname1, inputname2, output);
				tempx1 = null;
				tempy1 = null;
				tempx2 = null;
				tempy2 = null;
				if (chunk2 % 2 == 0) {
					tempx1 = String.format("%04d", pass2);
					tempy1 = String.format("%04d", chunk2);
				}
			}
			// if the file is not even and the String I create is empty then
			// start to put the numbers in.
			if ((chunk2 - 1) % 2 == 0) {
				if (tempx1 == null && tempy1 == null) {
					tempx1 = String.format("%04d", pass2);
					tempy1 = String.format("%04d", chunk2 - 1);

				} else {
					tempx2 = String.format("%04d", pass2);
					tempy2 = String.format("%04d", chunk2 - 1);

				}
			}

			// after each time the for loop go, let YYYY + 1 , after each time
			// the while loop go, let XXXX + 1 and let YYYY be 0 again, because
			// the new round merge begin.
			pass1++;
			pass2++;
			chunk2 = 0;
			// after merge count of file should divided by 2.
			count = count / 2;
		}
	}

	// this method is to compare the number in different files and merge them
	// into a new file in order.
	public static void mergeNumber(String inputname1, String inputname2,
			String output) throws IOException {
		// scan the input that I got from the method above, and create two
		// arraylist to put all the contents in input into arraylist. I have to
		// use arraylist in this case, because I need to know list.size().
		Scanner first = new Scanner(new File(inputname1));
		Scanner second = new Scanner(new File(inputname2));
		ArrayList<String> list1 = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();
		// put the contents in.
		while (first.hasNext()) {
			list1.add(first.next());
		}
		while (second.hasNext()) {
			list2.add(second.next());
		}
		// two indexes is use to make sure whether the arraylist is over. every
		// time I write a word into the new file then the index++, it means that
		// if the index == list.size then all the contents have put in. if it's
		// not equal, then means there still have some remains.
		int index1 = 0;
		int index2 = 0;
		String word1 = list1.get(index1);
		String word2 = list2.get(index2);
		PrintWriter out = new PrintWriter(output);
		for (int i = 0; i < list1.size() + list2.size(); i++) {
			// normal case that both arraylist don't finishing putting contents
			// in. then compare the words in two files normally.
			// add one more if() after each case because there always contains
			// the
			// situation that when index scan to the last words, then index will
			// not less than list.size() next time, then it will not print the
			// last words
			// in every file, so add one more if() to print the last word of
			// each
			// file.
			if (index1 < list1.size() && index2 < list2.size()) {
				if (word1.compareTo(word2) <= 0) {
					out.println(word1);
					index1++;
					if (index1 < list1.size())
						word1 = list1.get(index1);
				} else {
					out.println(word2);
					index2++;
					if (index2 < list2.size())
						word2 = list2.get(index2);
				}
			}
			// case that list1 is over, but still have some remain contents in
			// list2, then put all the contents remain in list2 over the merge
			// file.
			if (index1 == list1.size() && index2 < list2.size()) {
				while (index2 < list2.size()) {
					out.println(word2);
					index2++;
					if (index2 < list2.size())
						word2 = list2.get(index2);
				}
			}
			// same as above, this case the list2 is over, then put all the
			// remains in list1 in.
			if (index1 < list1.size() && index2 == list2.size()) {
				while (index1 < list1.size()) {
					out.println(word1);
					index1++;
					if (index1 < list1.size())
						word1 = list1.get(index1);
				}
			}
		}
		out.close();
	}

	// this method is insertion sort which we learned in class. Compare all the
	// contents before each index and sort.
	public static void insertionsort(String[] store) {
		String temp = "";
		int j = 0;
		for (int i = 1; i < store.length; i++) {
			temp = store[i];
			if (temp == null) {
				break;
			}
			j = i - 1;
			while (j >= 0 && store[j].compareTo(temp) > 0) {
				store[j + 1] = store[j];
				j--;
			}
			store[j + 1] = temp;
		}
	}

}
