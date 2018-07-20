package students_management;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

public class Bai1 {
	public static void PrintList(ArrayList<Student> sts)
	{
		for(Student s: sts) {
			System.out.println(s.toString());
		}
	}
	public static void main(String[] args)
	{
		
		ArrayList<Student> students = new ArrayList<Student>();
		
		int menuChoice = 0;
		try (Scanner input = new Scanner(System.in)){
			do
			{
				Menu.Sketch();
				try {
					System.out.println("Enter a choice: ");
					menuChoice = Integer.parseInt(input.nextLine());
				} catch(NumberFormatException e) {
					continue;
				}
				
				/* Add actions */
				if (menuChoice == 1)
				{
					int ID = 0;
					float GPA = -1;
					String name,
						   avatar,
						   address,
						   note;
					// take input ID -- required parameter
					do
					{
						try {
							System.out.println("ID: ");
							ID = Integer.parseInt(input.nextLine());
						}
						catch(NumberFormatException e) {
							System.out.println("Please enter a number!");
							continue;
						
						} 
					}
					while(ID <= 0);
					
					do {
						System.out.println("Name: ");
						name = input.nextLine();
					} while(name == null || name.isEmpty());
					
					
					// take GPA input -- required parameter
					do {
						try {
							System.out.println("GPA: ");
							GPA = Float.parseFloat(input.nextLine());
							
						}catch(NumberFormatException e) {
							System.out.println("Please enter a number!");
							continue;
						
						} 
					
					} while(GPA<(float)0 || GPA > (float)10);
					
					// take avatar input -- required parameter
					do {
						System.out.println("Avatar: ");
						avatar = input.nextLine();
					} while(avatar == null || avatar.isEmpty());
					
					// take address input -- required parameter
					do {
						System.out.println("Address: ");
						address = input.nextLine();
					}while(address == null || address.isEmpty());
					
					
					// take note input -- optional parameter
					System.out.println("Note: ");
					note = input.nextLine();
					
					Student s = new Student.Builder(ID, name)
							.withGPA(GPA)
							.withAddress(address)
							.withAvatarUrl(avatar)
							.withNote(note)
							.build();
					
					students.add(s);
				}
				
				/* View all actions */
				else if (menuChoice == 2) {
					
					if (students.size() == 0) {
						System.out.println("Currently no record found! Please add new One!");
					} else {
						int i = 0;
						Iterator<Student> it = students.iterator();
						while(it.hasNext())
						{
						    i++;
							System.out.println("#"+ i +".");
							Student s = it.next();
							System.out.println(s.toString());
						}
					}
					
	 			}
				
				/* Find and view a specified student by ID */
				else if (menuChoice == 3) {
					boolean found = false;
					
					int targetID = 0;
					do {
						System.out.println("Find by ID: ");
	
						try {
							targetID = Integer.parseInt(input.nextLine());
						} catch(NumberFormatException e) {
							System.out.println("Please enter a number!");
							continue;
						}
					}while(targetID < (int)0);
					
					Iterator<Student> It = students.iterator();
					while(It.hasNext())
					{
						Student s = It.next();
						if (s.getID() == targetID) {
							// Update Operation HERE
							found = true;
							System.out.println("Result: Found");
							System.out.println(s.toString());
							System.out.println("Do you want to make change with this student Record");
							System.out.println("Press U/u(Update)");
							char type = input.nextLine().trim().charAt(0);
							System.out.println(type);
							if (type == 'u' || type == 'U')
							{
								System.out.println("Update choosen!");
								// allow only these fields can be update
								float GPA = -1;
								String avatar,
									   address,
									   note;
								do {
									try {
										System.out.println("GPA: ");
										GPA = Float.parseFloat(input.nextLine());
										
									}catch(NumberFormatException e) {
										System.out.println("Please enter a number!");
										continue;
									
									} 
									if(GPA<(float)0 || GPA > (float)10) {
										System.out.println("Out of range!");
									}
								
								} while(GPA<(float)0 || GPA > (float)10);
								
								do
								{
									System.out.println("Avatar: ");
									avatar = input.nextLine();
								} while(avatar == null || avatar.isEmpty());
								
								do {
									
									System.out.println("Address: ");
									address = input.nextLine();
								} while(address == null || address.isEmpty());
								
								do {
									System.out.println("Note: ");
									note = input.nextLine();
								} while(note == null || note.isEmpty());
								
								// updating these field
								s.setGPA(GPA);
								s.setAvarUrl(avatar);
								s.setAddress(address);
								s.setNote(note);
							}
							else {
								System.out.println("It's Okay!. Let's do other stuffs!");
							}
							
						}
					}
					
					if (found == false) {
						System.out.println("Student with ID "+ targetID + " doesn't exist");
					}
					
				}
				
				/* Remove specified Student */
				else if (menuChoice == 4) {
					
					int ID = 0;
					do
					{
						System.out.println("Find by ID: ");
						try {
							ID = Integer.parseInt(input.nextLine());
						}
						catch(NumberFormatException e) {
							System.out.println("Please enter a number!");
							continue;
						
						} 
						if (ID <= 0)
							System.out.println("invalid number!");
					}
					while(ID <= 0);
					
					Iterator<Student> itr = students.iterator();
					boolean found = false;
					//boolean deleted = false;
					while(itr.hasNext())
					{
						
						if (itr.next().getID() == ID)
						{
							found = true;
							itr.remove();
//							try {
//								itr.remove();
//								deleted = true;
//							} catch()
							break;
						}
					}
					
//					if (found == true && deleted) {
//						
//					}
					
					if (found == true) {
						System.out.println("Student with ID "+ID + "found and deleted!");
					}
					else {
						System.out.println("Student with ID "+ID +"not existed!");
					}
				}
				/* Remove all Student */
				else if (menuChoice == 5) {
					System.out.println("Do you really want to delete entire DB ?");
					System.out.println("Press Y/y to confirm!)");
					char type = input.nextLine().trim().charAt(0);
					System.out.println(type);
					if (type == 'y' || type == 'Y')
					{
						students.clear();
					}
					
				}
				/* 
				 * Default input and output file is demo.bin 
				 * Export FILE
				 * 
				*/
				else if (menuChoice == 6)
				{
					String fileName = "demo.bin";
					FileOutputStream outFile=null;
					ObjectOutputStream outStream=null;
					
					try {
						outFile = new FileOutputStream(fileName);
						outStream = new ObjectOutputStream(outFile);
						Iterator<Student> it = students.iterator();
						while(it.hasNext()) {
							Student s = it.next();
							outStream.writeObject(s);
						}
						outStream.close();
					} catch(IOException e) {
						System.out.println(e.getMessage().toString());
					}
					finally {
						if (outFile != null) {
							try {
								outFile.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (outStream != null) {
							try {
								outStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				
				/* 
				 * Default input and output file is demo.bin 
				 * IMPORT FILE
				 * 
				*/
				else if (menuChoice == 7)
				{
					String fileName = "demo.bin";
					FileInputStream inFile=null;
					ObjectInputStream inStream = null;
					
					try {
						inFile = new FileInputStream(fileName);
						inStream = new ObjectInputStream(inFile);
						
						//ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(fileName))
						
						for(;;)
						{
							Student s = (Student)inStream.readObject();
							students.add(s);
							//System.out.println(s);
						}
						
					}
	
					catch(FileNotFoundException _404ex) 
					{
						System.out.println(_404ex.getMessage().toString());
					} 
					
					catch(EOFException EOFex)
					{
						
						try {
							// System.out.println("Reach EOF. Then Reading have finished!");
							inStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} 
					
					catch(IOException ioex)
					{
//						System.out.println(ioex.getMessage().toString());
						ioex.printStackTrace();
					} 
					
					catch(ClassNotFoundException Class404ex)
					{
						System.out.println(Class404ex.getMessage().toString());
					}
					finally {
						if (inFile != null) {
							try {
								inFile.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (inStream != null) {
							try {
								inStream.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				/* SORTING */
				else if (menuChoice == 8)
				{
					/* ORDER BY GPA_DESC */
					System.out.println("1. ORDER BY GPA_DESC");
					Collections.sort(students, Student.StudentGPA_DESC);
					PrintList(students);
					
					/* ORDER BY GPA_ASC */
					System.out.println("2. ORDER BY GPA_ASC");
					Collections.sort(students, Student.StudentGPA_ASC);
					PrintList(students);
					
					/* ORDER BY ID_DES */
					System.out.println("3. ORDER BY ID_DES");
					Collections.sort(students, Student.StudentID_DESC);
					PrintList(students);
					
					/* ORDER BY ID_ASC */
					System.out.println("4. ORDER BY ID_ASC");
					Collections.sort(students, Student.StudentID_ASC);
					PrintList(students);
					
				}
		} while(menuChoice != 9);
		}
		return;
		
		
  }
}
