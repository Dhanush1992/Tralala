package MusicApplication;
import MusicBox.*;
import MusicBox.RepeatState;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jdesktop.xswingx.PromptSupport;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Monkey D Alok
 */
public class MusicApplication implements Serializable {
    
//Here begin the declarations. 

//First we have the gui elements. Arranged in indented hierarchy for convienience 


	JFrame frame;
	JPanel panel;

			JPanel northPanel;

				JPanel menuAndVolumePanel;
					JMenuBar menuBar;
						JMenu file,edit,action,help;
							JMenuItem newPlayList,add,open,exit;
							JMenuItem cut,copy,paste,selectAll;
							JMenuItem play,pause,shuff,rep,prev,next,recent,nextAlbum,volumeUp,volumeDown;
							JMenuItem hummerHelp,restoreSettings,onlineService,aboutHummer;
					JSlider volume;

				JPanel songScrollPanel;
					JLabel songLabel;
					JSlider songScroller;

				JPanel playPanel;
					JButton previous,playPause,nextSong;
					
				JPanel searchPanel;	
					JTextField search;
					JComboBox<String> searchFilter;
					String searchOptions[] = {"Album","Playlist","Song"};

				JButton hMode;
					HummingMode hummer;
						

			JSplitPane centerSplitPanel;
				JPanel optionsPanel;
					JButton library;
					JPanel playListPanel;
						JList playListList;
						DefaultListModel<String> playListModel;
						ListSelectionModel playListSelectionModel;

					JPanel buttonPanel;
						JButton addPlayList,shuffle,repeat,nowPlaying;

				JPanel mainPanel;
					JSplitPane libraryPane;
						JPanel cdPanel;
						JPanel rackPanel;
							JList albumList;
							JScrollPane albumListScroller;
							ListSelectionModel albumSelectionModel;

			JPanel southPanel;
				JPanel bottomPanel;
				
// The music box below is the crux of the app. It is the manger for all of the music
	MusicBox theBox = new MusicBox();
	Font allTextFont;
//These are subsidary items to add on to the gui hierarchy. But they don't have a definite place
	JFileChooser openFile;
	Timer moveScroller;
	//Thread scrollTheSong;
	JScrollPane tableScrollPane;
	JTable songListTable;
	String currentSongList = "Library";
	boolean isPlayList = false;
	//boolean isSongPlaying = false;
	boolean autoScroll = false;
	//int scrollerPosition = 0;
	//boolean songChanged = false;
	String currentSong;
	boolean firstTime=true;
	final int REPEAT_STATES = 4;

	
	 
//The constructor. It calls the initiate app method.				
	public MusicApplication(){
		theBox.changeVolume((float)0.5);
		initiateApp();
               
	}
    
 //The initial gui constructor. Calls subsidary methods and constructs all the panels.
    	/**
	 *
	 */
	public final void initiateApp(){
		allTextFont = new Font("Tempus Sans ITC", Font.BOLD, 12);
        //Creating the main frame and a panel

		frame = new JFrame("Tra Laa Laa !!!");
		panel = new JPanel();

        //Setting up the panel
		frame.setIconImage(new ImageIcon("resources\\images\\app icon.png").getImage());
		frame.setFont(allTextFont);
		panel.setLayout(new BorderLayout());

        //Preparing the north, center and south panel

		
            
		northPanel = new JPanel(new GridBagLayout());
		northPanelSetup();
		
		centerSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerPanelSetup();
		
		southPanel = new JPanel(new GridLayout(1,1));
		southPanelSetup();

        //Adding the three sub panels to the panel

		panel.add(BorderLayout.NORTH,northPanel);
		panel.add(BorderLayout.CENTER,centerSplitPanel);
		panel.add(BorderLayout.SOUTH,southPanel);

        //Setting configurations for the frame
        //Setting panel as the content pane of the frame

		frame.addWindowListener(new MainWindowListener());
		frame.setContentPane(panel);
		frame.setMinimumSize(new Dimension(840,480));
		frame.pack();
		frame.setVisible(true);

        //Calling the startup init method which fills the main panel on startup
		setupDefaultMainPanel();

	}

	private void northPanelSetup(){
		//Setting constraints for the northPanel GridBag items - menu&buttons, volume, songscroll, playPanel, hummingMode

		GridBagConstraints[] c = new GridBagConstraints[5];
		for(int i = 0;i < 5;i++){
                    

			c[i] = new GridBagConstraints();
			c[i].gridx = i;
			c[i].gridy = 0;
			c[i].fill = GridBagConstraints.BOTH;
		}
                                                     
		c[0].weightx = 0.05; 
		c[1].weightx = 0.75;
		c[3].weightx = 0.20;
               
                
                
                                                                    

		//Setting the constraints for the seperator to be added b/w the north and the center panels

		GridBagConstraints sep = new GridBagConstraints();
		sep.gridy = 1;
		sep.gridx = 0;
		sep.gridwidth = 5;
		sep.fill = GridBagConstraints.HORIZONTAL;
                
                

		//Setting up the menus and volume panel

		menuAndVolumePanel = new JPanel();
		menuAndVolumePanel.setLayout(new GridLayout(2,1));

		menuBarSetup();
						     
		//Setting up the volume button				     
		volume = new JSlider(JSlider.HORIZONTAL,0,20,10);
		volume.addChangeListener(new VolumeListener());
		moveScroller = new Timer(1000 , new Scroller());
		moveScroller.setInitialDelay(1000);
		

		menuAndVolumePanel.add(menuBar);
		menuAndVolumePanel.add(volume);

		//Setting up the song scroller
		
		songScrollPanel = new JPanel(new GridLayout(2,1));
		songScrollPanel.setBackground(Color.LIGHT_GRAY);
		
		songLabel = new JLabel("Tra Laa Laa !!! ");
		songLabel.setFont(allTextFont);
		songLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		songScrollPanel.add(songLabel);

		//Setting up the play panel

		playPanel = new JPanel(new GridLayout(1,3,0,0));

		previous = new JButton();
		previous.setIcon(new ImageIcon("resources\\images\\previous.png"));
		
		previous.setFont(allTextFont);
		previous.setFocusable(false);
		previous.addActionListener(new PlayPreviousSong());

		playPause = new JButton();
		playPause.setIcon(new ImageIcon("resources\\images\\play.png"));
		playPause.setFont(allTextFont);
		playPause.setFocusable(false);
		playPause.addActionListener(new PlayPanelListener());

		nextSong = new JButton();
		nextSong.setIcon(new ImageIcon("resources\\images\\next.png"));
		nextSong.setFont(allTextFont);
		nextSong.setFocusable(false);
		nextSong.addActionListener(new PlayNextSong());

		playPanel.add(previous);
		playPanel.add(playPause);
		playPanel.add(nextSong);
                
                
                
		searchPanel = new JPanel(new GridLayout(2,1));
		search = new JTextField();
		search.setFont(allTextFont);
		PromptSupport.setPrompt("Search", search);
		search.addKeyListener(new SearchLibrary());
               
                
               
		searchFilter = new JComboBox<>();
		searchFilter.setFont(allTextFont);
		searchFilter.addItem("Album");
		searchFilter.addItem("Playlist");
		searchFilter.addItem("Song");
		
		
		searchPanel.add(search);
		searchPanel.add(searchFilter);
                

		//Setting up the humming mode panel

		hMode = new JButton();
		hMode.setIcon(new ImageIcon("resources\\images\\humming.png"));
		hMode.setFont(allTextFont);
		hMode.setFocusable(false);
		hummer = new HummingMode();
		hMode.addActionListener(hummer);

		//Adding all the different panels to the GridBag of the north panel (including the seperator)

		northPanel.add(menuAndVolumePanel,c[0]);
		northPanel.add(songScrollPanel,c[1]);
		northPanel.add(playPanel,c[2]);
		northPanel.add(searchPanel,c[3]);
		northPanel.add(hMode,c[4]);
		northPanel.add(new JSeparator(SwingConstants.HORIZONTAL),sep);

	}

	private void menuBarSetup(){

		//Making menus for the menubar

		menuBar = new JMenuBar();
		
		file = new JMenu("File");
		file.setFont(allTextFont);
		edit = new JMenu("Edit");
		edit.setFont(allTextFont);
		action = new JMenu("Action");
		action.setFont(allTextFont);
		help = new JMenu("Help");
		help.setFont(allTextFont);

                 //Filling the menus with menuitems
		newPlayList = new JMenuItem("New PlayList");
		add = new JMenuItem("Add");
		open = new JMenuItem("Open");
		exit = new JMenuItem("Exit");

		add.addActionListener(new AddFileListener());
                
		file.add(newPlayList);
		file.add(add);
		file.add(open);
		file.add(exit);

		cut = new JMenuItem("Cut");
		copy = new JMenuItem("Copy");
		paste = new JMenuItem("Paste");
		selectAll = new JMenuItem("Select All");

		edit.add(cut);
		edit.add(copy);
		edit.add(paste);
		edit.add(selectAll);

		play = new JMenuItem("Play");
		pause = new JMenuItem("Pause");
		shuff = new JMenuItem("Shuffle");
		rep = new JMenuItem("Repeat");
		prev = new JMenuItem("Previous");
		next = new JMenuItem("Next");
		recent = new JMenuItem("Recent");
		nextAlbum = new JMenuItem("Next Album");
		volumeUp = new JMenuItem("Volume Up");
		volumeDown = new JMenuItem("Volume Down");

		action.add(play);
		action.add(pause);
		action.add(shuff);
		action.add(rep);
		action.add(prev);
		action.add(next);
		action.add(recent);
		action.add(nextAlbum);
		action.add(volumeUp);
		action.add(volumeDown);

		hummerHelp = new JMenuItem("Tra Laa Laa !!! Help");
		restoreSettings = new JMenuItem("Restore Factory Settings");
		onlineService = new JMenuItem("Online Help");
		aboutHummer = new JMenuItem("Credits");

		help.add(hummerHelp);
		help.add(restoreSettings);
		help.add(onlineService);
		help.add(aboutHummer);

        //Adding the menus to the menubar

		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(action);
		menuBar.add(help);


	}

	private void centerPanelSetup(){

		optionsPanel = new JPanel(new GridBagLayout());
		centerSplitPanel.add(optionsPanel);
		optionsPanelSetup();
		
		mainPanel = new JPanel(new BorderLayout());
		DragAndDrop AddDndSongListener = new DragAndDrop();
		DropTarget target;
		target = new DropTarget(mainPanel , AddDndSongListener);
		centerSplitPanel.add(mainPanel);


		centerSplitPanel.setOneTouchExpandable(true);
		centerSplitPanel.setDividerLocation(200);

		Dimension minimumSize = new Dimension(200, 440);
		optionsPanel.setMinimumSize(minimumSize);

		minimumSize = new Dimension(500, 440);
		mainPanel.setMinimumSize(minimumSize);
		

	}

	private void optionsPanelSetup(){


		library = new JButton("Library");
		library.setFont(allTextFont);
		library.addActionListener(new LibraryButtonListener());
		library.setFocusable(false);
		
		playListPanel = new JPanel();
		playListPanelSetup();
		
		buttonPanelSetup();
		

		GridBagConstraints a,b,c;

		a = new GridBagConstraints();
		a.gridx = 0;
		a.gridy = 0;
		a.fill = GridBagConstraints.BOTH;
		a.weightx = 1;

		b = new GridBagConstraints();
		b.gridx = 0;
		b.gridy = 1;
		b.fill = GridBagConstraints.BOTH;
		b.weightx = 1;
		b.weighty = 1; 

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;

		optionsPanel.add(library,a);
		optionsPanel.add(playListPanel,b);
		optionsPanel.add(buttonPanel,c);


	}
	
	private void playListPanelSetup(){
		playListPanel.setBorder(BorderFactory.createTitledBorder("PlayLists"));
		playListPanel.setLayout(new GridLayout(1,1));
		
		playListModel = new DefaultListModel<>();
		playListList = new JList(playListModel);
                
		String[] listOfPlayLists = theBox.getPlayListList();
                
		for(String playList : listOfPlayLists){
			playListModel.addElement(playList);
		}
		
		playListList.setFont(allTextFont);
		playListList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		playListList.setLayoutOrientation(JList.VERTICAL);
		playListList.setOpaque(true);
		playListList.setBackground(Color.white);
		playListList.addMouseListener(new ListListener());
		
		JScrollPane playListScroller = new JScrollPane(playListList);
		
		playListPanel.add(playListScroller);
		
		playListSelectionModel = playListList.getSelectionModel();
		playListSelectionModel.addListSelectionListener(new ListListener());
		
	}
	
	private void buttonPanelSetup(){

        //Making a panel to hold the buttons and setting the layout

		buttonPanel = new JPanel(new GridLayout(1,4,0,0));
               

        //Creating buttons

		addPlayList = new JButton("+");
		addPlayList.setFont(allTextFont);
		addPlayList.setFocusable(false);
		addPlayList.addActionListener(new AddPlayList());

		shuffle = new JButton("S");
		shuffle.setFont(allTextFont);
		shuffle.setFocusable(false);
		shuffle.addActionListener(new Shuffle());

		repeat = new JButton("R");
		repeat.setFont(allTextFont);
		repeat.setFocusable(false);
		repeat.addActionListener(new Repeat());

		nowPlaying = new JButton("^");
		nowPlaying.setFont(allTextFont);
		nowPlaying.setFocusable(false);

        //Adding buttons to the panel

		buttonPanel.add(addPlayList);
		buttonPanel.add(shuffle);
		buttonPanel.add(repeat);
		buttonPanel.add(nowPlaying);
	}

	private void southPanelSetup(){
		
		JLabel reserved = new JLabel("All Rights Reserved To DexterNDeeDee Inc");
		
		reserved.setFont(allTextFont);
		reserved.setHorizontalAlignment(SwingConstants.CENTER);
		southPanel.add(reserved);
		southPanel.setBackground(Color.LIGHT_GRAY);

	}

	private void setupDefaultMainPanel(){

		libraryPaneSetup();
		mainPanel.removeAll();
		mainPanel.add(libraryPane);
		mainPanel.revalidate();
		mainPanel.repaint();
		

	}

	private void libraryPaneSetup(){
		
		libraryPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		cdPanelSetup();
		libraryPane.add(cdPanel);
		
		rackPanelSetup();
		libraryPane.add(rackPanel);

		

		libraryPane.setOneTouchExpandable(true);
		libraryPane.setDividerLocation(centerSplitPanel.getHeight()/2);
		libraryPane.setResizeWeight(0.6);


	}

	private void cdPanelSetup(){

		cdPanel = new JPanel();
		JLabel defaultIcon = new JLabel(new ImageIcon("resources//coverart//Default Cover Art.png"));
		cdPanel.add(BorderLayout.CENTER,defaultIcon);
	}

	private void rackPanelSetup(){

		rackPanel = new JPanel(new GridBagLayout());
						     
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.BOTH;
		
		JLabel wood = new JLabel();
		rackPanel.add(wood, c);
		
		c.gridx = 1;
		c.weightx = 1;
		
		JPanel albums = new JPanel(new GridLayout(1,1));
		rackPanel.add(albums, c);
		
		c.gridx = 2;
		c.weightx = 0;
		
		rackPanel.add(wood, c);
		
		String[] listOfAlbums = theBox.getAlbumList();
		
		DefaultListModel albumListModel = new DefaultListModel<>();
		albumList = new JList(albumListModel);
                
                
		for(String album : listOfAlbums){
			albumListModel.addElement(album);
		}
		
		albumListScroller = new JScrollPane(albumList);
		albums.add(albumListScroller);
		albumList.addMouseListener(new AlbumListListener());
	}
			  
	boolean mainPanelContains(Component c){
		Component[] components = mainPanel.getComponents();
		for (Component component : components) {
			if (c== component) {
				return true;
			}
		}
		return false;
	}
			  
	public void setupSongListTable(boolean isPlayList){
		String[][] tableContents;
		tableContents = theBox.getTableContents(currentSongList,isPlayList);
		
		makeTable(tableContents , mainPanel);
	
	}
	
	private void makeTable(String[][] tableContents , JPanel resultPanel){
		String[] tableHeader;
		tableHeader = theBox.getTableHeader();
		
		
		if(tableContents == null){
			DefaultTableModel model = new DefaultTableModel(0, tableHeader.length) ;
			model.setColumnIdentifiers(tableHeader);
			 songListTable = new JTable(model);
		}
		else{
			songListTable = new JTable(tableContents, tableHeader);
			songListTable.setFillsViewportHeight(true);
			
		}
		
		tableScrollPane = new JScrollPane(songListTable);
		resultPanel.removeAll();
		resultPanel.add(tableScrollPane);
		resultPanel.revalidate();
		resultPanel.repaint();
		if(frame.getContentPane() == panel){
			songListTable.getSelectionModel().addListSelectionListener(new TableListener());
		}
	}
	
	private void playTheSong(){
		boolean change = theBox.playSong();
		if(change){
			int length = theBox.getCurrentSongLength();
			if(length == -1){
				songLabel.setText("Tra Laa Laa !!!");
				songScrollPanel.removeAll();
				songScrollPanel.add(songLabel);
				return;
			}
			songLabel.setText(theBox.getCurrentSongTitle());
			songScroller = new JSlider(JSlider.HORIZONTAL,0,length,0);
			songScroller.addChangeListener(new SongScrollerListener());
			songScrollPanel.removeAll();
			songScrollPanel.add(songLabel);
			songScrollPanel.add(songScroller);
		}
		
		playPause.setIcon(new ImageIcon("resources\\images\\Pause.png"));
		moveScroller.start();
		
	}

	private class DragAndDrop implements DropTargetListener
	{

		public DragAndDrop() {
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {

		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {

		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {

		}

		@Override
		public void dragExit(DropTargetEvent dte) {

		}

		@Override
		public void drop(DropTargetDropEvent event) {
			
			event.acceptDrop(DnDConstants.ACTION_COPY);

			Transferable transferable = event.getTransferable();
			DataFlavor[] flavors = transferable.getTransferDataFlavors();

			for(DataFlavor flavor : flavors){
					if(flavor.isFlavorJavaFileListType()){
						try {
							java.util.List files;
							files = (java.util.List) transferable.getTransferData(flavor);
							for (Object file : files) {
								File song;
								song = (File) file;
								theBox.addSong(currentSongList, song, isPlayList);
							}
							if(mainPanelContains(tableScrollPane)){						       
								setupSongListTable(isPlayList);
							}
							else{
								setupDefaultMainPanel();  
							}
						} 
						catch (UnsupportedFlavorException | IOException ex) {
							Logger.getLogger(MusicApplication.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

			}

			event.dropComplete(true);

		}
		
	}



	

	
		
	// Here start the inner classes implementing listener interfaces
        
	class MainWindowListener extends WindowAdapter implements WindowListener
	{

			@Override
			public void windowClosing(WindowEvent e) {
                        
				theBox.save();
				System.exit(0); 
			}
	}
	
    
	class PlayPanelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			JButton playButt = (JButton)e.getSource();
			
			if(theBox.isPlaying()){
				moveScroller.stop();
				theBox.pauseSong();
				playButt.setIcon(new ImageIcon("resources\\images\\Play.png"));
			}
			else{
				playTheSong();
			}
		}

	}
	
	private class PlayPreviousSong
		implements ActionListener
	{

		public PlayPreviousSong() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			theBox.updatePreviousSong();
			playTheSong();
		}
	}

	private class PlayNextSong
		implements ActionListener
	{

		public PlayNextSong() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			theBox.updateNextSong();
			playTheSong();
		}
	}
    
	class AddFileListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			openFile = new JFileChooser();
			openFile.setCurrentDirectory(new File("E:"));
			int val = openFile.showOpenDialog(frame);
		
			if(val == JFileChooser.APPROVE_OPTION){
				File song = openFile.getSelectedFile();
				theBox.addSong(currentSongList, song, isPlayList);
				if(mainPanelContains(tableScrollPane)){						       
					setupSongListTable(isPlayList);
				}
				else{
					setupDefaultMainPanel();  
				}	  
			 }
                       
		  }
	}
    
	class ListListener extends MouseAdapter implements ListSelectionListener{
		
		@Override
		public void valueChanged(ListSelectionEvent e) {  
			if(!playListList.getValueIsAdjusting()){
				currentSongList = (String)playListList.getSelectedValue();
				isPlayList = true;
				setupSongListTable(true);	
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e){
			int index = playListList.locationToIndex(e.getPoint());
			currentSongList = (String)playListList.getModel().getElementAt(index);
			isPlayList = true;
			setupSongListTable(true);
			
		}
	}
 
	class AddPlayList implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			String inputValue = JOptionPane.showInputDialog("Name your new PlayList");
			if(inputValue != null){
				playListModel.addElement(inputValue); 
			}	 
		}
	}
	
	class Shuffle implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JButton shuffle = (JButton)e.getSource();
			if(shuffle.getText().equals("S")){
				theBox.setShuffleState(true);
				shuffle.setText("SS");
			}
			else{
				theBox.setShuffleState(false);
				shuffle.setText("S");
			}
			
		}
		
	}
	
	class Repeat implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			String[] states;
			int index = -1;
			states = new String[REPEAT_STATES];
			states[0] = "R";
			states[1] ="R1";
			states[2] = "RA";
			states[3] = "RN";
			JButton repeat = (JButton)e.getSource();
			String text = repeat.getText();
			for(int i =0; i < states.length; i++){
				if((states[i].equals(text))){
					repeat.setText(states[(i+1)%REPEAT_STATES]);
					index =(i+1)%REPEAT_STATES;
					break;
				}
			}
			switch(index){
				case 0 : theBox.setRepeatState(RepeatState.REPEATLISTONCE);
					break;
				case 1 : theBox.setRepeatState(RepeatState.REPEATSONG); 
					break;
				case 2 : theBox.setRepeatState(RepeatState.REPEATLIST);
					break;
				case 3 : theBox.setRepeatState(RepeatState.NOREPEAT);
					break;
				default : System.out.println("Aaaaaaaaarrrrrrrrrrrrggggggggghhhhhhhhh");
			}
		}
		
	}
 
	class TableListener implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			 if(!e.getValueIsAdjusting())
			{
				int r = songListTable.getSelectedRow();
				currentSong = (String)songListTable.getValueAt(r, 2);
				theBox.findSong(currentSongList, currentSong, isPlayList);

			}
		}	
	}

	class LibraryButtonListener implements ActionListener{
		@Override
		 public void actionPerformed(ActionEvent e){
			 isPlayList = false;
			currentSongList = "Library";
			 setupDefaultMainPanel();
		}
	}

	class AlbumListListener extends MouseAdapter{
		String selectedAlbum = null;
		@Override
		public void mouseClicked(MouseEvent e){
			
			if(!albumList.getValueIsAdjusting()){
				int index = albumList.locationToIndex(e.getPoint());
				String currentAlbum = (String)albumList.getModel().getElementAt(index);
				if(selectedAlbum == null || !currentAlbum.equals(selectedAlbum)){
					JLabel defaultIcon = null;
					selectedAlbum = currentAlbum;
					File coverArt = new File("resources//coverart//" + selectedAlbum + ".png");
					if(coverArt.exists()){
						defaultIcon = new JLabel(new ImageIcon("resources//coverart//" + selectedAlbum + ".png"));
					}
					else{
						defaultIcon = new JLabel(new ImageIcon("resources//coverart//Default Cover Art.png"));
					}
					cdPanel.removeAll();
					cdPanel.add(BorderLayout.CENTER,defaultIcon);
					cdPanel.revalidate();
					cdPanel.repaint();
				}
				else{
					currentSongList = selectedAlbum;
					isPlayList = false;
					selectedAlbum = null;
					setupSongListTable(isPlayList);
				}	
			}
				
		}
	}
	
	class VolumeListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e){
			JSlider volSlide = (JSlider) e.getSource();
			float vol = (float)volSlide.getValue();
			theBox.changeVolume(vol);
		}
	}
	
	class SongScrollerListener implements ChangeListener{
		
		int scrollerPosition;
		@Override
		 @SuppressWarnings("empty-statement")
		public void stateChanged(ChangeEvent e){
			if(autoScroll){
				autoScroll = false;;
				return;
			}
			//isSongPlaying = false;
			//moveScroller.stop();
			JSlider songSlide = (JSlider) e.getSource();
			scrollerPosition = songSlide.getValue();
			
			if(scrollerPosition > 0 && scrollerPosition <= theBox.getCurrentSongLength() ){
				theBox.scrollTo(scrollerPosition);
			}
			//moveScroller.start();
			//isSongPlaying = true;
			
		}
	}
	
	
	class SearchLibrary extends KeyAdapter{
        
		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER)
			{
				int id=searchFilter.getSelectedIndex();
				String text = search.getText();
				String option = searchOptions[id];
			//System.out.println("enter pressed!"+search.getText()+" "+searchOptions[id]);
				String[][] searchResults = theBox.search(text,option);
				makeTable(searchResults , mainPanel);
				search.setText("");
			} 
		}
	}
	
	class Scroller implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e){
			
			if(songScroller.getValue() == theBox.getCurrentSongLength()){
				theBox.updateNextSong();
				songScroller = null;
				playPause.setText("PLAY");
				playTheSong();
			}
			else{
				int scrollerPosition = songScroller.getValue();
				scrollerPosition++;
				songScroller.setValue(scrollerPosition);
				autoScroll = true;
			}
		}
		
	}
	
	private class HummingMode 
		implements ActionListener

	{
		JPanel hummingPanel;
			JPanel capturePanel;
				JButton searchButton;
					String[][] hits;
					JPanel resultPanel; 
						JSplitPane resultSplitPane;
							JPanel detailsPanel;
							JPanel songListPanel;
				JButton playbackButton;
					Timer checkIfFinished;
				JButton back;
				
			JButton captureFromMic;
				
			
			
			
		public HummingMode()
		{
			hummingPanel = new JPanel(new GridLayout(1,2));
			
			capturePanel = new JPanel(new GridLayout(3,1));
			
			captureFromMic = new JButton();
			captureFromMic.setIcon(new ImageIcon("resources//images//Capture.png"));
			playbackButton = new JButton();
			playbackButton.setIcon(new ImageIcon("resources//images//Play.png"));
			searchButton = new JButton();
			searchButton.setIcon(new ImageIcon("resources//images//search.png"));
			back = new JButton();
			back.setIcon(new ImageIcon("resources//images//back.png"));
			
			capturePanel.add(searchButton);
			searchButton.addActionListener(new songSearch());
			searchButton.setEnabled(false);
			
			capturePanel.add(playbackButton);
			playbackButton.addActionListener(new playback());
			playbackButton.setEnabled(false);
			
	
			capturePanel.add(back);
			back.addActionListener(new BackToInit());
			
			//capturePanel.add(captureFromMic);
			captureFromMic.addActionListener(new Capture());
			
			hummingPanel.add(capturePanel);
			hummingPanel.add(captureFromMic);
			
			resultPanel = new JPanel(new GridLayout(1,1));
			
			resultSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			resultSplitPane.setOneTouchExpandable(true);
			resultSplitPane.setDividerLocation(200);
				
			detailsPanel = new JPanel(new GridBagLayout());
			Dimension minimumSize = new Dimension(200, 440);
			detailsPanel.setMinimumSize(minimumSize);
			resultSplitPane.add(detailsPanel);
				
			
			songListPanel = new JPanel(new GridLayout(1,1));
			minimumSize = new Dimension(500, 440);
			songListPanel.setMinimumSize(minimumSize);
			resultSplitPane.add(songListPanel);
			
			resultPanel.add(resultSplitPane);
		}

		@Override
		public void actionPerformed(ActionEvent e){
			
			frame.setContentPane(hummingPanel);
			hummingPanel.revalidate();
			hummingPanel.repaint();
			theBox.letTheHummingBegin();
			
		}
		
		private void returnToDefault(){
			frame.setContentPane(panel);
			panel.revalidate();
			panel.repaint();
			captureFromMic.setIcon(new ImageIcon("resources//images//Capture.png"));
			playbackButton.setEnabled(false);
			searchButton.setEnabled(false);
			theBox.endOfHumming();
		}
		
		private class Capture
			implements ActionListener
		{
			
			
			public Capture()
			{
			}

			@Override
			public void actionPerformed(ActionEvent e)	
			{
				switch (theBox.micStatus()) {
					case IDLE:
						captureFromMic.setIcon(new ImageIcon("resources//images//stop.png"));
						playbackButton.setEnabled(false);
						searchButton.setEnabled(false);
						theBox.captureMicInput();
						break;
					case CAPTURING:
						captureFromMic.setIcon(new ImageIcon("resources//images//discard.png"));
						playbackButton.setEnabled(true);
						searchButton.setEnabled(true);
						theBox.stopCapture();
						break;
					default:
						captureFromMic.setIcon(new ImageIcon("resources//images//Capture.png"));
						theBox.cleanUpMic();
						break;
				}
				
				
			}
		}


		private class playback
			implements ActionListener
		{

			public playback()
			{
			}

			@Override
			public void actionPerformed(ActionEvent e){
				
				if(theBox.isMicDone()){
					captureFromMic.setEnabled(false);
					playbackButton.setIcon(new ImageIcon("resources//images//Pause.png"));
					searchButton.setEnabled(false);
					theBox.playCapturedSong();
					checkIfFinished = new Timer(100 , new CheckForFinish());
					checkIfFinished.setInitialDelay(100);
					checkIfFinished.start();
				}
				else{
					checkIfFinished.stop();
					captureFromMic.setEnabled(true);
					playbackButton.setIcon(new ImageIcon("resources//images//Play.png"));
					searchButton.setEnabled(true);
					theBox.stopPlaying();
				}
			}
		}

		private class songSearch
			implements ActionListener
		{	

			public songSearch()
			{
			}

			@Override
			public void actionPerformed(ActionEvent e){

				frame.setContentPane(resultPanel);
				
				
				hits = theBox.searchTheSong();
				theBox.cleanUpMic();
				makeTable(hits , songListPanel);
				setupDetails();
				
				resultPanel.revalidate();
				resultPanel.repaint();
				
				
				
				
			}
			
			private void setupDetails(){
				GridBagConstraints a,b,c;

				a = new GridBagConstraints();
				a.gridx = 0;
				a.gridy = 0;
				a.fill = GridBagConstraints.BOTH;
				a.weightx = 1;

				b = new GridBagConstraints();
				b.gridx = 0;
				b.gridy = 1;
				b.fill = GridBagConstraints.BOTH;
				b.weightx = 1;
				b.weighty = 1; 

				c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = 2;
				c.fill = GridBagConstraints.BOTH;
				c.weightx = 1;
				
				JButton goToAlbum = new JButton(hits[0][1]);
				goToAlbum.addActionListener(new GoToAlbumListener());
				detailsPanel.add(goToAlbum,a);
				
				JPanel playListPanel = new JPanel();
				playListPanel.setBorder(BorderFactory.createTitledBorder("PlayLists"));
				playListPanel.setLayout(new GridLayout(1,1));

				DefaultListModel<String> playListModel = new DefaultListModel<>();
				JList playListList = new JList(playListModel);

				String[] listOfPlayLists = theBox.getPlayLists(hits[0][2]);
				if(listOfPlayLists != null){
					for(String playList : listOfPlayLists){
						playListModel.addElement(playList);
					}
				}
				playListList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
				playListList.setLayoutOrientation(JList.VERTICAL);
				playListList.setOpaque(true);
				playListList.setBackground(Color.white);
				playListList.addMouseListener(new ListListener());

				JScrollPane playListScroller = new JScrollPane(playListList);

				playListPanel.add(playListScroller);

				ListSelectionModel playListSelectionModel = playListList.getSelectionModel();
				playListSelectionModel.addListSelectionListener(new GoToPlayListListener());
				
				detailsPanel.add(playListPanel , b);
				
				JButton goBack = new JButton("Go Back");
				goBack.addActionListener(new BackToInit());
				
				detailsPanel.add(goBack , c);
				
				
			}
		}
	
		private class GoToPlayListListener 
			implements ListSelectionListener
		{

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!playListList.getValueIsAdjusting()){
					returnToDefault();
					currentSongList = (String)playListList.getSelectedValue();
					isPlayList = true;
					setupSongListTable(true);	
				
				}
				
			}
			
		}
		
		private class GoToAlbumListener 
			implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				returnToDefault();
				 JButton currentButton = (JButton)e.getSource();
				currentSongList = currentButton.getText();
				setupSongListTable(false);
			}

			
			
		}
		
		private class BackToInit
			implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent e){
				returnToDefault();
			}

		}
		
		
		private class CheckForFinish 
			implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				if(theBox.isMicDone()){
					captureFromMic.setEnabled(true);
					playbackButton.setText("PlayMic");
					searchButton.setEnabled(true);
					checkIfFinished.stop();
				}
			}
			
		}
		

	}
}
