package my.vaadin.app;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class LoginForm extends FormLayout {

	TextField email = new TextField("Email");
	PasswordField password = new PasswordField("Password");
	Button login = new Button("Log in");

	private HomeUI myUI;

	public LoginForm(HomeUI myUI) {
		this.myUI = myUI;
		login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		
		setSizeUndefined();
		HorizontalLayout buttons = new HorizontalLayout(login);
		addComponents(email, password, buttons);
	}
	
	
}
