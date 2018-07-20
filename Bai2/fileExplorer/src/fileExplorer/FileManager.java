package fileExplorer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class FileManager {
	
    private Desktop desktop;
    private FileSystemView fileSystemView;
    private File currentFile;
    private JPanel gui;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private JTable table;
    private JProgressBar progressBar;
    private FileTableModel fileTableModel;
    private ListSelectionListener listSelectionListener;
    private boolean cellSizesSet = false;
    private int rowIconPadding = 3;

    // File Actions
    private JButton openFile;
    private JButton editFile;
    private JButton deleteFile;
    private JButton newFile;
    
    // Some details of currentFile
    private JLabel fileName;
    private JTextField path;
    private JLabel size;
    private JRadioButton isDirectory;
    private JRadioButton isFile;

    // GUI to make new File/Directory
    private JPanel newFilePanel;
    private JRadioButton newTypeFile;
    private JTextField name;

    public Container getGui() 
    {
        if (gui==null) 
        {
            gui = new JPanel(new BorderLayout(3,3));
            gui.setBorder(new EmptyBorder(5,5,5,5));

            fileSystemView = FileSystemView.getFileSystemView();
            desktop = Desktop.getDesktop();

            JPanel detailView = new JPanel(new BorderLayout(3,3));

            table = new JTable();
            
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setAutoCreateRowSorter(true);
            table.setShowVerticalLines(false);

            listSelectionListener = new ListSelectionListener() 
            {
                @Override
                public void valueChanged(ListSelectionEvent lse) 
                {
                    int row = table.getSelectionModel().getLeadSelectionIndex();
                    setFileDetails( ((FileTableModel)table.getModel()).getFile(row) );
                }
            };
            
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
            JScrollPane tableScroll = new JScrollPane(table);
            Dimension d = tableScroll.getPreferredSize();
            tableScroll.setPreferredSize(new Dimension((int)d.getWidth(), (int)d.getHeight()/2));
            detailView.add(tableScroll, BorderLayout.CENTER);

            
            DefaultMutableTreeNode root = new DefaultMutableTreeNode();
            treeModel = new DefaultTreeModel(root);

            TreeSelectionListener treeSelectionListener = new TreeSelectionListener() 
            {
                public void valueChanged(TreeSelectionEvent tse)
                {
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                    showChildren(node);
                    setFileDetails((File)node.getUserObject());
                }
            };

            File[] roots = fileSystemView.getRoots();
            for (File fileSystemRoot : roots) 
            {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
                root.add( node );
                File[] files = fileSystemView.getFiles(fileSystemRoot, true);
                for(File file : files) 
                {
                    if (file.isDirectory()) 
                    {
                        node.add(new DefaultMutableTreeNode(file));
                    }
                }
            }

            tree = new JTree(treeModel);
            tree.setRootVisible(false);
            tree.addTreeSelectionListener(treeSelectionListener);
            tree.setCellRenderer(new FileTreeCellRenderer());
            tree.expandRow(0);
            JScrollPane treeScroll = new JScrollPane(tree);

            tree.setVisibleRowCount(10);

            Dimension preferredSize = treeScroll.getPreferredSize();
            Dimension widePreferred = new Dimension(200, (int)preferredSize.getHeight());
            
            treeScroll.setPreferredSize( widePreferred );

            JPanel fileMainDetails = new JPanel(new BorderLayout(4,2));
            fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

            JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

            JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
            fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

            fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
            fileName = new JLabel();
            fileDetailsValues.add(fileName);
            fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
            path = new JTextField(5);
            path.setEditable(false);
            fileDetailsValues.add(path);
            fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
            size = new JLabel();
            fileDetailsValues.add(size);
            fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));

            JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING,4,0));
            isDirectory = new JRadioButton("Directory");
            isDirectory.setEnabled(false);
            flags.add(isDirectory);

            isFile = new JRadioButton("File");
            isFile.setEnabled(false);
            flags.add(isFile);
            fileDetailsValues.add(flags);

            int count = fileDetailsLabels.getComponentCount();
            for (int i=0; i<count; i++) 
            {
                fileDetailsLabels.getComponent(i).setEnabled(false);
            }

            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);

            openFile = new JButton("Open");
            openFile.setMnemonic('o');

            openFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
                    try 
                    {
                        desktop.open(currentFile);
                    } catch(Throwable t) 
                    {
                        showThrowable(t);
                    }
                    gui.repaint();
                }
            });
            toolBar.add(openFile);

            editFile = new JButton("Edit");
            editFile.setMnemonic('e');
            editFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
                    try {
                        desktop.edit(currentFile);
                    } catch(Throwable t) {
                        showThrowable(t);
                    }
                }
            });
            
            toolBar.add(editFile);

            openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
            editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));

            toolBar.addSeparator();

            newFile = new JButton("New");
            newFile.setMnemonic('n');
            newFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
                    newFile();
                }
            });
            
            toolBar.add(newFile);

            JButton renameFile = new JButton("Rename");
            renameFile.setMnemonic('r');
            renameFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
                    renameFile();
                }
            });
            
            toolBar.add(renameFile);

            deleteFile = new JButton("Delete");
            deleteFile.setMnemonic('d');
            deleteFile.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ae) 
                {
                    deleteFile();
                }
            });
            
            toolBar.add(deleteFile);

            toolBar.addSeparator();

            JPanel fileView = new JPanel(new BorderLayout(3,3));

            fileView.add(toolBar,BorderLayout.NORTH);
            fileView.add(fileMainDetails,BorderLayout.CENTER);

            detailView.add(fileView, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treeScroll,
                detailView);
            gui.add(splitPane, BorderLayout.CENTER);

            JPanel simpleOutput = new JPanel(new BorderLayout(3,3));
            progressBar = new JProgressBar();
            simpleOutput.add(progressBar, BorderLayout.EAST);
            progressBar.setVisible(false);

            gui.add(simpleOutput, BorderLayout.SOUTH);

        }
        return gui;
    }

    public void showRootFile() {
        tree.setSelectionInterval(0,0);
    }

    private TreePath findTreePath(File find) 
    {
        for (int i=0; i<tree.getRowCount(); i++) 
        {
            TreePath treePath = tree.getPathForRow(i);
            Object object = treePath.getLastPathComponent();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)object;
            File nodeFile = (File)node.getUserObject();

            if (nodeFile == find) 
            {
                return treePath;
            }
        }
        return null;
    }

    private void renameFile() 
    {
        if (currentFile==null) 
        {
            showErrorMessage("No file choosen to rename.","Choose File");
            return;
        }

        String renameTo = JOptionPane.showInputDialog(gui, "New Name");
        
        if (renameTo != null) {
        	
            try {
                boolean directory = currentFile.isDirectory();
                TreePath parentPath = findTreePath(currentFile.getParentFile());
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();

                boolean renamed = currentFile.renameTo(new File(currentFile.getParentFile(), renameTo));
                if (renamed) 
                {
                    if (directory) 
                    {
                        TreePath currentPath = findTreePath(currentFile);
                        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)currentPath.getLastPathComponent();

                        treeModel.removeNodeFromParent(currentNode);

                    }

                    showChildren(parentNode);
                } else 
                {
                    String msg = "FILE " + currentFile +" couldnot be renamed";
                    showErrorMessage(msg,"Rename Failed");
                }
            } catch(Throwable t) 
            {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

    private void deleteFile() 
    {
        if (currentFile==null) 
        {
            showErrorMessage("No file choosen to delete.","choose File");
            return;
        }

        int result = JOptionPane.showConfirmDialog(
            gui,
            "Do you really want to delete this file?",
            "Deletion Confirm",
            JOptionPane.ERROR_MESSAGE
            );
        
        if (result==JOptionPane.OK_OPTION) 
        {
            try {
                TreePath parentPath = findTreePath(currentFile.getParentFile());
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();

                boolean directory = currentFile.isDirectory();
                boolean deleted = currentFile.delete();
                if (deleted) {
                    if (directory) 
                    {
                        TreePath currentPath = findTreePath(currentFile);
                        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)currentPath.getLastPathComponent();

                        treeModel.removeNodeFromParent(currentNode);
                    }

                    showChildren(parentNode);
                } else {
                    String msg = "FILE" + currentFile + " could not be deleted";
                    showErrorMessage(msg,"Delete Failed");
                }
            } catch(Throwable t) {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

    private void newFile() 
    {
        if (currentFile==null) {
            showErrorMessage("No Destination choosen for new file.","Choose Destination");
            return;
        }

        if (newFilePanel==null) {
            newFilePanel = new JPanel(new BorderLayout(3,3));

            JPanel southRadio = new JPanel(new GridLayout(1,0,2,2));
            newTypeFile = new JRadioButton("File", true);
            JRadioButton newTypeDirectory = new JRadioButton("Directory");
            ButtonGroup bg = new ButtonGroup();
            bg.add(newTypeFile);
            bg.add(newTypeDirectory);
            southRadio.add( newTypeFile );
            southRadio.add( newTypeDirectory );

            name = new JTextField(15);

            newFilePanel.add( new JLabel("Name"), BorderLayout.WEST );
            newFilePanel.add( name );
            newFilePanel.add( southRadio, BorderLayout.SOUTH );
        }

        int result = JOptionPane.showConfirmDialog(
            gui,
            newFilePanel,
            "Create File",
            JOptionPane.OK_CANCEL_OPTION);
        if (result==JOptionPane.OK_OPTION) 
        {
            try {
                boolean created;
                File parentFile = currentFile;
                if (!parentFile.isDirectory()) {
                    parentFile = parentFile.getParentFile();
                }
                File file = new File( parentFile, name.getText() );
                if (newTypeFile.isSelected()) {
                    created = file.createNewFile();
                } else {
                    created = file.mkdir();
                }
                if (created) {

                    TreePath parentPath = findTreePath(parentFile);
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();

                    if (file.isDirectory()) 
                    {
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);

                        TreePath currentPath = findTreePath(currentFile);
                        DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)currentPath.getLastPathComponent();

                        treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
                    }

                    showChildren(parentNode);
                } else 
                {
                    String msg = "FILE " + file + " could not be created.";
                    showErrorMessage(msg, "Creation Failed");
                }
            } catch(Throwable t) 
            {
                showThrowable(t);
            }
        }
        gui.repaint();
    }

    private void showErrorMessage(String errorMessage, String errorTitle) 
    {
        JOptionPane.showMessageDialog(gui,errorMessage,errorTitle,JOptionPane.ERROR_MESSAGE);
    }

    private void showThrowable(Throwable t) 
    {
        t.printStackTrace();
        JOptionPane.showMessageDialog(gui,t.toString(),t.getMessage(),JOptionPane.ERROR_MESSAGE);
        gui.repaint();
    }

    private void setTableData(final File[] files) 
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	
                if (fileTableModel==null) 
                {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
                
                if (!cellSizesSet) {
                    Icon icon = fileSystemView.getSystemIcon(files[0]);

                    table.setRowHeight( icon.getIconHeight()+rowIconPadding );
                    setColumnWidth(0,-1);
                    setColumnWidth(1,160);
                    cellSizesSet = true;
                }
            }
        });
    }

    private void setColumnWidth(int column, int width) 
    {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        if (width<0) 
        {
            JLabel label = new JLabel( (String)tableColumn.getHeaderValue() );
            Dimension preferred = label.getPreferredSize();
            width = (int)preferred.getWidth()+10;
        }
        tableColumn.setPreferredWidth(width);
        tableColumn.setMaxWidth(width);
        tableColumn.setMinWidth(width);
    }

    private void showChildren(final DefaultMutableTreeNode node) 
    {
        tree.setEnabled(false);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() 
        {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) 
                {
                    File[] files = fileSystemView.getFiles(file, true);
                    if (node.isLeaf()) 
                    {
                        for (File child : files) 
                        {
                            if (child.isDirectory()) 
                            {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> lists) 
            {
                for (File child : lists) 
                {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() 
            {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                tree.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void setFileDetails(File file) 
    {
        currentFile = file;
        
        Icon icon = fileSystemView.getSystemIcon(file);
        fileName.setIcon(icon);
        fileName.setText(fileSystemView.getSystemDisplayName(file));
        path.setText(file.getPath());
       
        size.setText((float)(file.length()/1024.0) + " KB");
        isDirectory.setSelected(file.isDirectory());

        isFile.setSelected(file.isFile());

        JFrame f = (JFrame)gui.getTopLevelAncestor();
        if (f != null) 
        {
            f.setTitle(fileSystemView.getSystemDisplayName(file));
        }

        gui.repaint();
    } 
}