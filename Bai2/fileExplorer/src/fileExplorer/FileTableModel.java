package fileExplorer;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;


public class FileTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 1L;
	private File[] files;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns = { "Icon", "File", "Path"};
	
	FileTableModel(File[] files) 
	{ 
		this.files = files; 
	}
	FileTableModel() 
	{ 
		this(new File[0]); 
	}

	@Override
	public int getColumnCount() 
	{
		return columns.length;
	}

	@Override
	public int getRowCount() 
	{
		return files.length;
	}

	@Override
	public Object getValueAt(int row, int column) 
	{
		File file = files[row];
		switch(column) 
		{
			case 0: return fileSystemView.getSystemIcon(file);
			case 1: return fileSystemView.getSystemDisplayName(file);
			case 2: return file.getPath();
			default: System.err.println("Invalid Column!");
		}
		return "";
	}
	
	public Class<?> getColumnClass(int column) 
	{
		switch(column) 
		{
		case 0: return ImageIcon.class;
		} 
		return String.class;
	}
	
	public String getColumnName(int column) 
	{
		return columns[column];
	}
	
	public File getFile(int row) 
	{
		return files[row];
	}
	
	public void setFiles(File[] files) 
	{
		this.files = files;
		fireTableDataChanged();
	}

}
