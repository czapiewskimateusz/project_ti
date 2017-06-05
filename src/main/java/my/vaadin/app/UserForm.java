package my.vaadin.app;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class UserForm extends FormLayout {

	private TextField firstName = new TextField("Imię");
	private TextField lastName = new TextField("Naziwsko");
	private TextField email = new TextField("E-mail");
	private TextField password = new TextField("Hasło");
	private DateField birthdate = new DateField("Data urodzin");
	private Button save = new Button("Zapisz");
	private Button delete = new Button("Usuń");
	private Button hide = new Button(FontAwesome.ARROW_LEFT);

	private UserService service = UserService.getInstance();
	private User customer;
	private HomeUI myUI;
	private Binder<User> binder = new Binder<>(User.class);

	public UserForm(HomeUI myUI) {
		this.myUI = myUI;

		setSizeUndefined();
		
		HorizontalLayout buttons = new HorizontalLayout(save, delete, hide);
		addComponents(firstName, lastName, email, password, birthdate, buttons);

		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(KeyCode.ENTER);

		binder.bindInstanceFields(this);

		save.addClickListener(e -> this.save());
		delete.addClickListener(e -> this.delete());
		hide.addClickListener(e -> this.hide());
	}

	public void setCustomer(User customer) {
		this.customer = customer;
		binder.setBean(customer);

		// Show delete button for only customers already in the database
		delete.setVisible(customer.isPersisted());
		setVisible(true);
		firstName.selectAll();
	}

	private void delete() {
		service.delete(customer);
		myUI.updateList();
		setVisible(false);
	}

	private void save() {
		service.save(customer);
		myUI.updateList();
		setVisible(false);
	}
	
	private void hide() {
		setVisible(false);
	}

}
