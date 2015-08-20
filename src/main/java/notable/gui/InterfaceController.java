package notable.gui;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import notable.core.impl.NoteFactory;
import notable.core.impl.NoteManager;
import notable.core.impl.TagManager;
import notable.core.interfaces.Taggable;
import notable.core.interfaces.iNote;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class InterfaceController implements Initializable {
	
	private static final Logger LOG = LogManager.getLogger(InterfaceController.class);
	
	Timeline fadeText = new Timeline();

	Timeline fadeText1 = new Timeline();

	private boolean isEnabled;

	enum ApplicationMode {
		Note, Tag_Overview, Tag_SpecificTag
	};

	private ApplicationMode appMode;

	@FXML SplitPane splitpane;
	
	@FXML
	Text notags_added_prompt;

	@FXML
	Label tagMode_label;

	@FXML
	Button goBack_Button;

	@FXML
	Button addNote_Button;

	@FXML
	Button removeNote_Button;

	@FXML
	ToggleButton tags_togglebutton;

	@FXML
	Text tags_added_prompt;

	@FXML
	ListView<iNote> listOfNotes;

	@FXML
	ListView<String> listOfTags;

	@FXML
	AnchorPane root;

	@FXML
	TextArea noteText;

	@FXML
	TextField enteredTags;

	@FXML
	TextField searchField;

	@FXML
	Text dateText;

	private ObservableList<iNote> noteData;

	private ObservableList<String> tagData;

	private NoteManager nManager;

	private TagManager tManager;

	private iNote clickedNote;

	private ExecutorService exe = Executors.newSingleThreadExecutor();
	
	ObservableList<iNote> getListOfNotes(){
		return noteData;
	}

	public void updateDate(iNote clicked) {
		dateText.setText(clicked.getDate().format(
				DateTimeFormatter.ofPattern("d MMM uuuu - HH:mm")));
	}

	public void updateNoteText(iNote clicked) {
		noteText.setText(clicked.getNote());
	}

	public void updateEnteredTags(iNote clicked) {
		if (clicked instanceof Taggable) {
			enteredTags.setText(tManager.getFormattedTags((Taggable) clicked));
		}
	}

	public void updateListOfNotes() {
		noteData.setAll(nManager.getNotes());
	}

	public void updateListOfTags() {
		tagData.setAll(tManager.getTags());
	}


	public void chooseNote(iNote note) {
		LOG.trace(">> chooseNote()");
		clickedNote = note;
		updateNoteText(note);
		updateDate(note);
		updateEnteredTags(note);
		listOfNotes.getSelectionModel().select(note);
	}

	
	public void addTags() {
		LOG.trace(">> addTags()");
		if (clickedNote instanceof Taggable) {
			try {
				TagManager.getInstance().addTagToNote(enteredTags.getText(),
						(Taggable) clickedNote);
				enteredTags.setText(tManager
						.getFormattedTags((Taggable) clickedNote));
				fadeText.play();
			} catch (IllegalArgumentException e) {
				notags_added_prompt.setText("No Tags entered!");
				//LOG.catching(e);
				LOG.error(e.getMessage());
				fadeText1.play();
			}

		}
		root.requestFocus();
	}

	public void initializeFadeAnimation_tagsAdded() {
		fadeText.getKeyFrames().addAll(
				new KeyFrame(Duration.seconds(0), new KeyValue(
						tags_added_prompt.opacityProperty(), 0)),
				new KeyFrame(Duration.seconds(0.9), new KeyValue(
						tags_added_prompt.opacityProperty(), 1)),
				new KeyFrame(Duration.seconds(2), new KeyValue(
						tags_added_prompt.opacityProperty(), 0)));
		fadeText.setCycleCount(1);
	}

	public void initializeFadeAnimation_notagsAdded() {
		fadeText1.getKeyFrames().addAll(
				new KeyFrame(Duration.seconds(0), new KeyValue(
						notags_added_prompt.opacityProperty(), 0)),
				new KeyFrame(Duration.seconds(0.9), new KeyValue(
						notags_added_prompt.opacityProperty(), 1)),
				new KeyFrame(Duration.seconds(2), new KeyValue(
						notags_added_prompt.opacityProperty(), 0)));
		fadeText1.setCycleCount(1);
	}

	public void enableApplication() {
		
		LOG.trace(">> enableApplication()");
		listOfNotes.setVisible(true);
		noteText.setVisible(true);
		enteredTags.setDisable(false);
		searchField.setDisable(false);
		dateText.setVisible(true);
		tags_togglebutton.setDisable(false);
		removeNote_Button.setDisable(false);
		isEnabled = true;
		appMode = ApplicationMode.Note;
	}

	public void disableApplication() {
		LOG.trace(">> disableApplication()");
		clickedNote = null;
		noteText.clear();
		noteText.setVisible(false);
		listOfNotes.setVisible(false);
		enteredTags.clear();
		enteredTags.setDisable(true);
		searchField.setDisable(true);
		dateText.setVisible(false);
		tags_togglebutton.setDisable(true);
		removeNote_Button.setDisable(true);
		isEnabled = false;
		
	}

	public void setTagMode_Overview() {
		LOG.trace(">> setTagMode_Overview()");
		noteData.clear();
		goBack_Button.setVisible(false);
		addNote_Button.setVisible(false);
		removeNote_Button.setVisible(false);
		listOfNotes.setVisible(false);
		listOfTags.setVisible(true);
		noteText.setVisible(false);
		dateText.setVisible(false);
		enteredTags.clear();
		enteredTags.setDisable(true);
		updateListOfTags();
		searchField.setPromptText("Search Tags...");
		appMode = ApplicationMode.Tag_Overview;
		LOG.debug("-- setTagMode_Overview() > Set appMode: " + appMode);

	}

	public void setTagMode_SpecificTag(String tag) {
		LOG.trace(">> setTagMode()");
		noteData.setAll(tManager.getNotesToTag(tag));
		chooseNote(noteData.get(0));
		listOfTags.setVisible(false);
		listOfNotes.setVisible(true);
		goBack_Button.setVisible(true);
		tagMode_label.setVisible(true);
		dateText.setVisible(true);
		noteText.setVisible(true);
		enteredTags.setDisable(false);
		tagMode_label.setText(tag);
		noteText.requestFocus();
		appMode = ApplicationMode.Tag_SpecificTag;
		LOG.debug("-- setTagMode_SpecificTag() > Set appMode: " + appMode);
	}

	public void setNoteMode() {
		LOG.trace(">> setTagMode()");
		noteData.setAll(nManager.getNotes());
		goBack_Button.setVisible(false);
		tagMode_label.setVisible(false);
		addNote_Button.setVisible(true);
		listOfTags.setVisible(false);
		listOfNotes.setVisible(true);
		removeNote_Button.setVisible(true);
		noteText.setVisible(true);
		dateText.setVisible(true);
		enteredTags.setDisable(false);
		updateListOfNotes();
		searchField.setPromptText("Search Notes...");
		appMode = ApplicationMode.Note;
		LOG.debug("-- setNoteMode() > Set appMode: " + appMode);
	}

	@FXML
	public void goBackToTags(ActionEvent event) {
		LOG.trace(">> goBackToTags()");
		setTagMode_Overview();
		tagMode_label.setVisible(false);
		goBack_Button.setVisible(false);
	}

	@FXML
	public void tagFieldKeyPressed(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			addTags();
		}
	}

	@FXML
	public void noteChosen(MouseEvent event) {
		LOG.trace(">> noteChosen()");
		if (listOfNotes.getSelectionModel().getSelectedItem() != null) {
			chooseNote((iNote) listOfNotes.getSelectionModel()
					.getSelectedItem());
		}

	}

	@FXML
	public void addNote(ActionEvent event) {
		LOG.trace(">> addNote()");
		if (nManager.isEmpty()) {
			enableApplication();

		}
		iNote temp = NoteFactory.createTagNote();
		clickedNote = temp;
		temp.setDate(LocalDateTime.now());
		nManager.addNote(temp);
		chooseNote(temp);
		updateListOfNotes();
	}

	@FXML
	public void tagChosen(MouseEvent event) {
		LOG.trace(">> tagChosen()");
		String tag = listOfTags.getSelectionModel().getSelectedItem();
		if (tag != null) {
			setTagMode_SpecificTag(tag);
		}

	}

	@FXML
	public void removeNote(ActionEvent event) {
		LOG.trace(">> removeNote()");
		if (clickedNote != null) {
			int removeIndex = noteData.indexOf(clickedNote);
			nManager.removeNote(clickedNote);
			if (clickedNote instanceof Taggable) {
				exe.execute(() -> tManager
						.deleteTagsToNote((Taggable) clickedNote));
			}
			updateListOfNotes();
			if (nManager.isEmpty()) {
				disableApplication();
			} else {
				if (removeIndex == noteData.size() && removeIndex != 0) {
					clickedNote = noteData.get(removeIndex - 1);
					noteText.setText(clickedNote.getNote());
				} else {
					clickedNote = noteData.get(removeIndex);
					noteText.setText(clickedNote.getNote());
				}
			}
		}
	}

	@FXML
	public void toggleTags(ActionEvent event) {
		LOG.trace(">> toggleTags()");
		if (tags_togglebutton.isSelected()) {
			if (isEnabled) {
				setTagMode_Overview();
			}
		} else {
			if (isEnabled) {
				setNoteMode();
			}
		}
	}

	public void setNoteTextListener() {
		noteText.textProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							if (clickedNote != null) {
								clickedNote.setNote(newValue);
								clickedNote.setDate(LocalDateTime.now());
								updateDate(clickedNote);
								int index  = noteData.indexOf(clickedNote);
								if(index >= 0){
								noteData.set(noteData.indexOf(clickedNote),
										clickedNote);
								}
							}
						});
	}

	public void setSearchFieldListener() {
		searchField.textProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (appMode.equals(ApplicationMode.Note)) {
						List<iNote> tempNotes = nManager.searchNotes(newValue);
						if (!tempNotes.isEmpty()) {
							noteData.setAll((nManager.searchNotes(newValue)));
						}
					} else if (appMode.equals(ApplicationMode.Tag_Overview)) {
						Set<String> tempTags = tManager.searchTags(newValue);
						if (!tempTags.isEmpty()) {
							tagData.setAll((tManager.searchTags(newValue)));
						}
					}
				});
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		noteData = FXCollections.observableArrayList();
		tagData = FXCollections.observableArrayList();
		listOfNotes.setItems(noteData);
		listOfTags.setItems(tagData);
		nManager = NoteManager.getInstance();
		tManager = TagManager.getInstance();
		initializeFadeAnimation_tagsAdded();
		initializeFadeAnimation_notagsAdded();
		setNoteTextListener();
		setSearchFieldListener();
		LOG.info("Application started");
	}

}
