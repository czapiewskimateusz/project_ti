package my.vaadin.app;

import java.io.File;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.StyleGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.themes.ValoTheme;

@Theme("mytheme")
public class HomeUI extends UI {

	private UserService userService = UserService.getInstance();
	
	private Grid<User> grid = new Grid<>(User.class);

	private TextField tf_filterText = new TextField();
	
	private Label userLabel = new Label();

	Image image = new Image();

	Upload upload;
	
	HorizontalLayout toolbar;
	private VerticalLayout layout;

	/**
	 * Download basepath
	 */
	private String basepath = "D://data.txt";
	Resource res = new FileResource(new File(basepath));
	FileDownloader fd = new FileDownloader(res);
	
	/**
	 * Buttons
	 */
	Button btn_filterReset ;
	Button btn_addUser;
	Button btn_logOut ;
	Button btn_upload;
	Button btn_save;
	Button btn_download;
	
	/**
	 * Forms
	 */
	private UserForm userForm = new UserForm(this);
	private LoginForm loginForm = new LoginForm(this);

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		layout = new VerticalLayout();

		
		tf_filterText.setPlaceholder("szukaj...");
		tf_filterText.addValueChangeListener(e -> updateList());
		tf_filterText.setValueChangeMode(ValueChangeMode.LAZY);

		/**
		 * Buttons initialize
		 */
		btn_filterReset = new Button(FontAwesome.TIMES);
		btn_addUser = new Button("Dodaj użytkownika");
		btn_logOut = new Button("Wyloguj");
		btn_upload = new Button("Wstaw plik");
		btn_save = new Button("Zapisz tabelę");
		btn_download = new Button("Pobierz tabelę");
		btn_filterReset.setDescription("Wyczyść filtr");
		
		CssLayout filtering = new CssLayout();
		filtering.addComponents(tf_filterText, btn_filterReset);
		filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		
		setUpload();

		setButtons();

		toolbar = new HorizontalLayout(image, userLabel, filtering, btn_addUser, btn_logOut, btn_upload, btn_save, btn_download);

		grid.setColumns("id", "firstName", "lastName", "email");

		HorizontalLayout mainLayout = new HorizontalLayout(grid, userForm);

		loginForm.setVisible(true);
		loginForm.login.addClickListener(e -> login());

		upload.setVisible(false);
		grid.setVisible(false);
		toolbar.setVisible(false);
		userForm.setVisible(false);
		mainLayout.setSizeFull();
		grid.setSizeFull();
		mainLayout.setExpandRatio(grid, 1);

		layout.addComponents(toolbar, mainLayout, loginForm, upload);

		layout.setComponentAlignment(loginForm, Alignment.TOP_CENTER);
		layout.setComponentAlignment(upload, Alignment.BOTTOM_RIGHT);

		updateList();

		setContent(layout);

		grid.asSingleSelect().addValueChangeListener(event -> {
			if (event.getValue() == null) {
				userForm.setVisible(false);
			} else {
				userForm.setCustomer(event.getValue());
			}
		});
	}

	private void updateList() {
		List<User> customers = userService.findAll(tf_filterText.getValue());
		grid.setItems(customers);
	}

	private void login() {
		List<User> customers = userService.findAll();

		for (User c : customers) {
			System.out.println(c);
			if (loginForm.email.getValue().equals(c.getEmail())
					&& loginForm.password.getValue().equals(c.getPassword())) {
				loginForm.setVisible(false);
				grid.setVisible(true);
				toolbar.setVisible(true);
				userLabel.setValue("Witaj, " + c.getFirstName());
				image.setSource(new ExternalResource("https://robohash.org/" + c.getEmail()));
				image.setWidth("100px");
				image.setHeight("80px");
				return;
			}
		}
		Notification.show("Błąd", "Błędny login lub hasło", Notification.Type.ERROR_MESSAGE);
	}
	
	private void setUpload(){
		UploadReceiver receiver = new UploadReceiver();
		upload = new Upload("Wyślij plik", receiver);
		upload.setImmediateMode(false);
		upload.setButtonCaption("Wyślij");

		upload.addStartedListener(new Upload.StartedListener() {

			@Override
			public void uploadStarted(StartedEvent event) {
				if (event.getContentLength() > 20000000) {
					upload.interruptUpload();
				}

			}
		});

		upload.addSucceededListener(new Upload.SucceededListener() {

			@Override
			public void uploadSucceeded(SucceededEvent event) {
				upload.setVisible(false);
				Notification.show("Sukces", "Wysyłanie pliku zakończone powodzeniem",
						Notification.Type.TRAY_NOTIFICATION);
			}
		});

		upload.addFailedListener(new FailedListener() {

			@Override
			public void uploadFailed(FailedEvent event) {
				Notification.show("Błąd", "Nie udało się wysłać pliku", Notification.Type.ERROR_MESSAGE);

			}
		});

	}
	
	private void setButtons(){
		
		btn_addUser.addClickListener(e -> {
			grid.asSingleSelect().clear();
			userForm.setCustomer(new User());
		});

		btn_logOut.addClickListener(e -> {
			upload.setVisible(false);
			loginForm.setVisible(true);
			grid.setVisible(false);
			toolbar.setVisible(false);
			userForm.setVisible(false);
			userLabel.setValue("");
			userLabel.setEnabled(false);
			loginForm.email.setValue("");
			loginForm.password.setValue("");
		});

		btn_upload.addClickListener(e -> {
			if (upload.isVisible() == true) {
				upload.setVisible(false);
			} else {
				upload.setVisible(true);
			}
		});

		btn_save.addClickListener(e -> {
			if (userService.saveToFile()) {
				Notification.show("Sukces", "Zapisanie zakończone powodzeniem", Notification.Type.TRAY_NOTIFICATION);
			}
		});
		
		fd.extend(btn_download);
		
		btn_filterReset.addClickListener(e -> tf_filterText.clear());
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = HomeUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}
