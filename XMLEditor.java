import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.contrib.output.JTreeOutputter;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLEditor extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new XMLEditor();
			}
		});
	}
	
	XMLEditor(){
		super("XML Editor");
		launch();
	}
	
	JToolBar createJToolBar(){
		JToolBar tb = new JToolBar();
		tb.add(createNewAction());
		tb.add(createOpenAction());
		tb.addSeparator();
		tb.add(createSaveAction());
		tb.addSeparator();
		tb.add(createUndoAction());
		tb.add(createRedoAction());
		tb.addSeparator();
		tb.add(createCutAction());
		tb.add(createCopyAction());
		tb.add(createPasteAction());
		tb.addSeparator();
		tb.add(createAttributeAction());
		tb.add(createElementAction());
		tb.add(createTextAction());
		tb.addSeparator();
		tb.add(createQuitAction());
		return tb;
	}
	
	JMenuBar createJMenuBar(){
		JMenuBar mb = new JMenuBar();
		mb.add(createFileJMenu());
		mb.add(createEditJMenu());
		mb.add(createHelpJMenu());
		return mb;
	}
	
	JMenu createFileJMenu(){
		JMenu m = new JMenu("File");
		m.add(createFileNewJMenuItem());
		m.add(createFileOpenJMenuItem());
		m.addSeparator();
		m.add(createFileSaveJMenuItem());
		m.add(createFileSaveAsJMenuItem());
		m.addSeparator();
		m.add(createFileQuitJMenuItem());
		m.setMnemonic(KeyEvent.VK_F);
		return m;
	}
	
	JMenuItem createFileNewJMenuItem(){
		JMenuItem mi = new JMenuItem("New");
		mi.setAction(createNewAction());
		return mi;
	}
	
	JMenuItem createFileOpenJMenuItem(){
		JMenuItem mi = new JMenuItem("Open...");
		mi.setAction(createOpenAction());
		return mi;
	}
	
	JMenuItem createFileSaveJMenuItem(){
		JMenuItem mi = new JMenuItem("Save");
		mi.setAction(createSaveAction());
		return mi;
	}
	
	JMenuItem createFileSaveAsJMenuItem(){
		JMenuItem mi = new JMenuItem("Save As...");
		mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK));
		mi.setMnemonic(KeyEvent.VK_S);
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				performSaveAsAction();
			}
		});
		return mi;
	}
	
	JMenuItem createFileQuitJMenuItem(){
		JMenuItem mi = new JMenuItem("Quit");
		mi.setAction(createQuitAction());
		return mi;
	}
	
	JMenu createEditJMenu(){
		JMenu m = new JMenu("Edit");
		m.add(createEditUndoJMenuItem());
		m.add(createEditRedoJMenuItem());
		m.addSeparator();
		m.add(createEditCutJMenuItem());
		m.add(createEditCopyJMenuItem());
		m.add(createEditPasteJMenuItem());
		m.addSeparator();
		m.add(createEditAddJMenu());
		m.setMnemonic(KeyEvent.VK_E);
		return m;
	}
	
	JMenuItem createEditUndoJMenuItem(){
		JMenuItem mi = new JMenuItem("Undo");
		mi.setAction(createUndoAction());
		return mi;
	}
	
	JMenuItem createEditRedoJMenuItem(){
		JMenuItem mi = new JMenuItem("Redo");
		mi.setAction(createRedoAction());
		return mi;
	}
	
	JMenuItem createEditCutJMenuItem(){
		JMenuItem mi = new JMenuItem("Cut");
		mi.setAction(createCutAction());
		return mi;
	}
	
	JMenuItem createEditCopyJMenuItem(){
		JMenuItem mi = new JMenuItem("Copy");
		mi.setAction(createCopyAction());
		return mi;
	}
	
	JMenuItem createEditPasteJMenuItem(){
		JMenuItem mi = new JMenuItem("Paste");
		mi.setAction(createPasteAction());
		return mi;
	}
	
	JMenuItem createEditAddJMenu(){
		JMenu m = new JMenu("Add");
		m.add(createEditAddAttributeJMenuItem());
		m.add(createEditAddElementJMenuItem());
		m.add(createEditAddTextJMenuItem());
		return m;
	}
	
	JMenuItem createEditAddAttributeJMenuItem(){
		JMenuItem mi = new JMenuItem("Attribute");
		mi.setAction(createAttributeAction());
		return mi;
	}
	
	JMenuItem createEditAddElementJMenuItem(){
		JMenuItem mi = new JMenuItem("Element");
		mi.setAction(createElementAction());
		return mi;
	}
	
	JMenuItem createEditAddTextJMenuItem(){
		JMenuItem mi = new JMenuItem("Text");
		mi.setAction(createTextAction());
		return mi;
	}
	
	JMenu createHelpJMenu(){
		JMenu m = new JMenu("Help");
		m.add(createHelpAboutJMenuItem());
		m.setMnemonic(KeyEvent.VK_H);
		return m;
	}
	
	JMenuItem createHelpAboutJMenuItem(){
		JMenuItem mi = new JMenuItem("About");
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				performAboutAction();
			}
		});
		return mi;
	}
	
	private void launch(){
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		setBounds(width / 8, height / 8, 3 * width / 4, 3 * height / 4);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setJMenuBar(createJMenuBar());
		add(createJToolBar(), BorderLayout.PAGE_START);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				performQuitAction();
			}
		});
		
		setVisible(true);
	}
	
	private DefaultMutableTreeNode processElement(Element el){
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(el);
		
		for(Object o : el.getAttributes()){
			root.add(new DefaultMutableTreeNode(o));
		}
		if(el.getTextNormalize() != null && !el.getTextNormalize().equals("")){
			root.add(new DefaultMutableTreeNode(new Text(el.getTextNormalize())));
		}
		for(Object o : el.getChildren()){
			root.add(processElement((Element)o));
		}
		
		return root;
	}
	
 	private void performNewAction(){
		String root = JOptionPane.showInputDialog(this, "Enter new root node value", "Root Node", JOptionPane.PLAIN_MESSAGE);
		
		if(root != null){
			document = new Document(new Element(root));
			chooser = null;
			
			if(view != null){
				remove(view);
			}
			
			undo = new Stack<Object[]>();
			redo = new Stack<Object[]>();
			
			tree = new JTree(processElement(document.getRootElement()));
			view = new JScrollPane(tree);
			add(view, BorderLayout.CENTER);
			validate();
		}
	}
	
	AbstractAction createNewAction(){
		if(newAction == null){
			newAction = new AbstractAction("New"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_N);
				}
				public void actionPerformed(ActionEvent e){
					performNewAction();
				}
			};
		}
		
		return newAction;
	}
	
	private void performOpenAction(){
		if(chooser == null){
			chooser = createJFileChooser();
		}
		int choice = chooser.showOpenDialog(this);
				
		if(choice == JFileChooser.APPROVE_OPTION){
			SAXBuilder builder = new SAXBuilder();
			try{
				document = builder.build(chooser.getSelectedFile());
			}
			catch(Exception e){}
			
			undo = new Stack<Object[]>();
			redo = new Stack<Object[]>();
			
			tree = new JTree(processElement(document.getRootElement()));
			view = new JScrollPane(tree);
			add(view, BorderLayout.CENTER);
			validate();
			
			String title = getTitle();
			int index;
			if((index = title.indexOf(" - ")) >= 0){
				title = title.substring(0, index);
			}
			setTitle(title + " - " + chooser.getSelectedFile());
		}
	}

	private JFileChooser createJFileChooser(){
		if(chooser == null){
			chooser = new JFileChooser();
			chooser.setFileFilter(new FileFilter() {
				public String getDescription(){
					return "XML Files";
				}
				public boolean accept(File file){
					return file.getName().endsWith(".xml");
				}
			});
		}
		
		return chooser;
	}
	
	AbstractAction createOpenAction(){
		if(openAction == null){
			openAction = new AbstractAction("Open..."){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_O);
				}
				public void actionPerformed(ActionEvent e){
					performOpenAction();
				}
			};
		}
		
		return openAction;
	}
	
	private void performSaveAction(){
		if(chooser == null){
			performSaveAsAction();
		}
		else{
			try{
				PrintWriter writer = new PrintWriter(chooser.getSelectedFile());
				XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
				output.output(document, writer);
			}
			catch(Exception e){}
		}
	}
	
	AbstractAction createSaveAction(){
		if(saveAction == null){
			saveAction = new AbstractAction("Save"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_S);
				}
				public void actionPerformed(ActionEvent e){
					performSaveAction();
				}
			};
		}
		
		return saveAction;
	}
	
	private void performSaveAsAction(){
		if(chooser == null){
			chooser = createJFileChooser();
		}
		
		int choice = chooser.showSaveDialog(this);
		
		if(choice == JFileChooser.APPROVE_OPTION){
			performSaveAction();
		}
	}
	
	private void performQuitAction(){
		int choice = JOptionPane.showConfirmDialog(this, "Do you want to save before you quit?", "Quit?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (choice == JOptionPane.YES_OPTION){
			if(chooser == null){
				chooser = createJFileChooser();
			}
			int choice2 = chooser.showSaveDialog(this);
			
			if(choice2 == JFileChooser.APPROVE_OPTION){
				performSaveAction();
			}
			System.exit(0);
		}
		if(choice == JOptionPane.NO_OPTION){
			System.exit(0);
		}
	}
	
	AbstractAction createQuitAction(){
		if(quitAction == null){
			quitAction = new AbstractAction("Quit"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
				}
				public void actionPerformed(ActionEvent e){
					performQuitAction();
				}
			};
		}
		
		return quitAction;
	}
	
	private void performAboutAction(){
		JOptionPane.showMessageDialog(null, "XML Editor Program Version 1.0.0\nAuthor: Eric Zebrowski\nStudent ID: 21675421\nCS 288-002", "About", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void performUndoAction(){
		Object[] a;
		
		try{
			a = undo.pop();
		}
		catch(EmptyStackException e){
			return;
		}
		
		Method m = (Method)a[0];
		
		try{
			m.invoke(this, a[1]);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	AbstractAction createUndoAction(){
		if(undoAction == null){
			undoAction = new AbstractAction("Undo"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_U);
				}
				public void actionPerformed(ActionEvent e){
					performUndoAction();
				}
			};
		}
		
		return undoAction;
	}
	
	public void performUndoAddAttribute(DefaultMutableTreeNode node){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
		parent.remove(node);
		Element el = (Element)parent.getUserObject();
		el.removeAttribute((Attribute)node.getUserObject());
		
		try{
			redo.push(new Object[]{getClass().getMethod("performRedoAddAttributeAction", new Class<?>[]{DefaultMutableTreeNode.class, DefaultMutableTreeNode.class}), new Object[]{parent, node}});
		}
		catch(NoSuchMethodException e){
			return;
		}
			
		tree.updateUI();
	}
	
	public void performUndoAddElement(DefaultMutableTreeNode node){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
		parent.remove(node);
		Element el = (Element)parent.getUserObject();
		el.removeContent((Content)node.getUserObject());
		
		try{
			redo.push(new Object[]{getClass().getMethod("performRedoAddElementAction", new Class<?>[]{DefaultMutableTreeNode.class, DefaultMutableTreeNode.class}), new Object[]{parent, node}});
		}
		catch(NoSuchMethodException e){
			return;
		}
			
		tree.updateUI();
	}
	
	public void performUndoAddText(DefaultMutableTreeNode node){
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
		parent.remove(node);
		Element el = (Element)parent.getUserObject();
		el.removeContent((Content)node.getUserObject());
		
		try{
			redo.push(new Object[]{getClass().getMethod("performRedoAddTextAction", new Class<?>[]{DefaultMutableTreeNode.class, DefaultMutableTreeNode.class}), new Object[]{parent, node}});
		}
		catch(NoSuchMethodException e){
			return;
		}
			
		tree.updateUI();
	}
	
	private void performRedoAction(){
		Object[] a;
		try{
			a = redo.pop();
		}
		catch(EmptyStackException e){
			return;
		}
		
		Method m = (Method)a[0];
		
		try{
			m.invoke(this, (Object[])a[1]);
		}
		catch(Exception e){}
		
	}
	
	AbstractAction createRedoAction(){
		if(redoAction == null){
			redoAction = new AbstractAction("Redo"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_R);
				}
				public void actionPerformed(ActionEvent e){
					performRedoAction();
				}
			};
		}
		
		return redoAction;
	}
	
	public void performRedoAddAttributeAction(DefaultMutableTreeNode parent, DefaultMutableTreeNode node){
		parent.add(node);
		Element el = (Element)parent.getUserObject();
		el.setAttribute((Attribute)node.getUserObject());
		
		try{
			undo.push(new Object[]{getClass().getMethod("performUndoAddAttribute", new Class<?>[]{DefaultMutableTreeNode.class}), el});
		}
		catch(NoSuchMethodException e){}
		
		tree.updateUI();
	}
	
	public void performRedoAddElementAction(DefaultMutableTreeNode parent, DefaultMutableTreeNode node){
		parent.add(node);
		Element el = (Element)parent.getUserObject();
		el.addContent((Element)node.getUserObject());
		
		try{
			undo.push(new Object[]{getClass().getMethod("performUndoAddElement", new Class<?>[]{DefaultMutableTreeNode.class}), el});
		}
		catch(NoSuchMethodException e){}
		
		tree.updateUI();
	}
	
	public void performRedoAddTextAction(DefaultMutableTreeNode parent, DefaultMutableTreeNode node){
		parent.add(node);
		Element el = (Element)parent.getUserObject();
		el.addContent((Element)node.getUserObject());
		
		try{
			undo.push(new Object[]{getClass().getMethod("performUndoAddText", new Class<?>[]{DefaultMutableTreeNode.class}), el});
		}
		catch(NoSuchMethodException e){}
		
		tree.updateUI();
	}
	
	public void performCutAction(){
		if(tree != null){
			TreePath path = tree.getSelectionPath();
			if(path != null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if(node != tree.getModel().getRoot()){
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
					if(node.getUserObject() instanceof Attribute){
						Attribute at = (Attribute)node.getUserObject();
						at.getParent().removeAttribute(at);
						cut = at;
						}
					if(node.getUserObject() instanceof Element){
						Element el = (Element)node.getUserObject();
						el.getParent().removeContent(el);
						cut = el;
						}
					if(node.getUserObject() instanceof Text){
						cut = ((Text)node.getUserObject()).getText();
						Element el = (Element)parent.getUserObject();
						el.setText("");
						}
					parent.remove(node);
					tree.updateUI();
				}
			}
		}
	}
	
	AbstractAction createCutAction(){
		if(cutAction == null){
			cutAction = new AbstractAction("Cut"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_X);
				}
				public void actionPerformed(ActionEvent e){
					performCutAction();
				}
			};
		}
		
		return cutAction;
	}
	
	private void performCopyAction(){
		if(tree != null){
			TreePath path = tree.getSelectionPath();
			if(path != null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if(node.getUserObject() instanceof Attribute){
					Attribute at = (Attribute)node.getUserObject();
					cut = at.clone();
				}
				else if(node.getUserObject() instanceof Content){
					Content co = (Content)node.getUserObject();
					cut = co.clone();
				}
			}
		}
	}
	
	AbstractAction createCopyAction(){
		if(copyAction == null){
			copyAction = new AbstractAction("Copy"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_C);
				}
				public void actionPerformed(ActionEvent e){
					performCopyAction();
				}
			};
		}
		
		return copyAction;
	}
	
	private void performPasteAction(){
		if(cut != null){
			if(tree!= null){
				TreePath path = tree.getSelectionPath();
				if(path != null){
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
					if(node.getUserObject() instanceof Element){
						Element parent = (Element)node.getUserObject();
						if(cut instanceof Attribute){
							parent.setAttribute((Attribute)cut);
							node.add(new DefaultMutableTreeNode(cut));
						}
						else if(cut instanceof Element){
							parent.addContent((Element)cut);
							node.add(processElement((Element)cut));
						}
						else if(cut instanceof String){
							parent.setText((String)cut);
							node.add(new DefaultMutableTreeNode(new Text((String)cut)));
						}
						tree.updateUI();
						tree.expandPath(path);
						cut = null;
					}
				}
			}
		}
	}
	
	AbstractAction createPasteAction(){
		if(pasteAction == null){
			pasteAction = new AbstractAction("Paste"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_V);
				}
				public void actionPerformed(ActionEvent e){
					performPasteAction();
				}
			};
		}
		
		return pasteAction;
	}
	
	private void performAttributeAction(){
		if(tree != null){
			TreePath path = tree.getSelectionPath();
			if(path != null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if(node.getUserObject() instanceof Element){
					Element parent = (Element)node.getUserObject();
					String name = JOptionPane.showInputDialog(this, "Enter the Attribute name", "Attribute Name", JOptionPane.PLAIN_MESSAGE);
					String value = JOptionPane.showInputDialog(this, "Enter the Attribute value", "Attirbute Value", JOptionPane.PLAIN_MESSAGE);
					if(name != null && value != null){
						Attribute at = new Attribute(name, value);
						parent.setAttribute(at);
						DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(at);
						node.add(childnode);
						tree.updateUI();
						tree.expandPath(path);
						
						try{
							undo.push(new Object[]{getClass().getMethod("performUndoAddAttribute", new Class<?>[]{DefaultMutableTreeNode.class}), childnode});
						}
						catch(NoSuchMethodException e){}
					}
				}
			}
		}
	}
	
	AbstractAction createAttributeAction(){
		if(attributeAction == null){
			attributeAction = new AbstractAction("Attribute"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_A);
				}
				public void actionPerformed(ActionEvent e){
					performAttributeAction();
				}
			};
		}
		
		return attributeAction;
	}
	
	private void performElementAction(){
		if(tree != null){
			TreePath path = tree.getSelectionPath();
			if(path != null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if(node.getUserObject() instanceof Element){
					Element parent = (Element)node.getUserObject();
					String child = JOptionPane.showInputDialog(this, "Enter the Element name", "Element Name", JOptionPane.PLAIN_MESSAGE);
					if(child != null){
						Element el = new Element(child);
						parent.addContent(el);
						DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(el);
						node.add(childnode);
						tree.updateUI();
						tree.expandPath(path);
						
						try{
							undo.push(new Object[]{getClass().getMethod("performUndoAddElement", new Class<?>[]{DefaultMutableTreeNode.class}), childnode});
						}
						catch(NoSuchMethodException e){}
					}
				}
			}
		}
	}
	
	AbstractAction createElementAction(){
		if(elementAction == null){
			elementAction = new AbstractAction("Element"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_E);
				}
				public void actionPerformed(ActionEvent e){
					performElementAction();
				}
			};
		}
		
		return elementAction;
	}
	
	private void performTextAction(){
		if(tree != null){
			TreePath path = tree.getSelectionPath();
			if(path != null){
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				if(node.getUserObject() instanceof Element){
					Element parent = (Element)node.getUserObject();
					String text = JOptionPane.showInputDialog(this, "Enter some text", "Enter Text", JOptionPane.PLAIN_MESSAGE);
					if(text != null){
						Text te = new Text(text);
						parent.addContent(te);
						DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(te);
						node.add(childnode);
						tree.updateUI();
						tree.expandPath(path);
						
						try{
							undo.push(new Object[]{getClass().getMethod("performUndoAddText", new Class<?>[]{DefaultMutableTreeNode.class}), childnode});
						}
						catch(NoSuchMethodException e){}
					}
				}
			}
		}
	}

	AbstractAction createTextAction(){
		if(textAction == null){
			textAction = new AbstractAction("Text"){
				{
					putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
					putValue(MNEMONIC_KEY, KeyEvent.VK_T);
				}
				public void actionPerformed(ActionEvent e){
					performTextAction();
				}
			};
		}
		
		return textAction;
	}
	
	private AbstractAction newAction;
	private AbstractAction openAction;
	private AbstractAction saveAction;
	private AbstractAction quitAction;
	private AbstractAction redoAction;
	private AbstractAction undoAction;
	private AbstractAction cutAction;
	private AbstractAction copyAction;
	private AbstractAction pasteAction;
	private AbstractAction attributeAction;
	private AbstractAction elementAction;
	private AbstractAction textAction;

	private Stack<Object[]> undo = new Stack<Object[]>();
	private Stack<Object[]> redo = new Stack<Object[]>();
	
	private Object cut;
	private JFileChooser chooser;
	private Document document;
	private Element node;
	private JTree tree;
	private JScrollPane view;
}
