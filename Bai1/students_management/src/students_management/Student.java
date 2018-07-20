package students_management;

import java.io.Serializable;
import java.util.Comparator;

/*
 * 
 * Supposed that Student class has many Attributes to practicing Builder Pattern
 * 
 * 
 */

public class Student  implements Serializable{ 
	private static final long serialVersionUID = 1L;
	/**
	 * @param ID represent Unique Identity for each Student
	 */
	private int ID;
	private String Name;
	private float GPA;
	private String AvarUrl;
	private String Address;
	private String Note;
	
//	public Student(String Name, float GPA, String AvatarUrl, String Address, String Note)
//	{
//		super();
//		setName(Name);
//		setGPA(GPA);
//		setAvarUrl(AvatarUrl);
//		setAddress(Address);
//		setNote(Note);
//	}
	
	private Student(Builder builder)
	{
		this.ID = builder.ID;
		this.Name = builder.Name;
		this.GPA = builder.GPA;
		this.AvarUrl = builder.AvarUrl;
		this.Address = builder.Address;
		this.Note = builder.Note;
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public float getGPA() {
		return GPA;
	}
	public void setGPA(float gPA) {
		GPA = gPA;
	}
	public String getAvarUrl() {
		return AvarUrl;
	}
	public void setAvarUrl(String avarUrl) {
		AvarUrl = avarUrl;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getNote() {
		return Note;
	}
	public void setNote(String note) {
		Note = note;
	}
	
	public static Comparator<Student> StudentID_ASC = new Comparator<Student>() {
		public int compare(Student s1, Student s2) {
			return Integer.compare(s1.getID(),s2.getID());
			}
		};
		
	public static Comparator<Student> StudentID_DESC= new Comparator<Student>() {
		public int compare(Student s1, Student s2) {
			  return Integer.compare(s2.getID(),s1.getID());
			}
		};
		
	public static Comparator<Student> StudentGPA_ASC = new Comparator<Student>() {
		public int compare(Student s1, Student s2) {
			return Float.compare(s1.getGPA(),s2.getGPA());
		}
	};
	
	public static Comparator<Student> StudentGPA_DESC = new Comparator<Student>() {
		public int compare(Student s1, Student s2) {
			return Float.compare(s2.getGPA(),s1.getGPA());
		}
	};
	
	@Override
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("ID: "+ID);
		buffer.append("\n");
		buffer.append("Name: "+Name);
		buffer.append("\n");
		buffer.append("GPA: "+GPA);
		buffer.append("\n");
		buffer.append("AvatarUrl: "+AvarUrl);
		buffer.append("\n");
		buffer.append("Address: "+Address);
		buffer.append("\n");
		buffer.append("Note: "+Note);
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * The Builder class
	 *
	 *
	 */
	
	public static class Builder{
		
		private int ID;
		private String Name;
		private float GPA;
		private String AvarUrl;
		private String Address;
		private String Note;
		
		/** 
		 * 
		 * The constructor
		 * 
		 */
		
		public Builder(int ID, String Name) {
			if(Name == null) {
				throw new IllegalArgumentException("Name of student could not be null");
			} else {
				this.ID = ID;
				this.Name = Name;
			}
		}
		
		public Builder withGPA(float gpa) {
			this.GPA = gpa;
			return this;
		}
		
		public Builder withAddress(String addr) {
			this.Address = addr;
			return this;
		}
		
		public Builder withAvatarUrl(String avatar) {
			this.AvarUrl = avatar;
			return this;
		}
		
		public Builder withNote(String note) {
			this.Note = note;
			return this;
		}
		
		
		public Student build() {
			return new Student(this);
		}
		
	}
	
}
