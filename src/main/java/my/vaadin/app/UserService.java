package my.vaadin.app;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {

	private static UserService instance;
	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

	private final HashMap<Long, User> contacts = new HashMap<>();
	private long nextId = 0;

	private UserService() {
	}

	public static UserService getInstance() {
		if (instance == null) {
			instance = new UserService();
			instance.ensureTestData();
		}
		return instance;
	}

	public synchronized List<User> findAll() {
		return findAll(null);
	}

	public synchronized List<User> findAll(String stringFilter) {
		ArrayList<User> arrayList = new ArrayList<>();
		for (User contact : contacts.values()) {
			try {
				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
				if (passesFilter) {
					arrayList.add(contact.clone());
				}
			} catch (CloneNotSupportedException ex) {
				Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Collections.sort(arrayList, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		return arrayList;
	}

	public synchronized List<User> findAll(String stringFilter, int start, int maxresults) {
		ArrayList<User> arrayList = new ArrayList<>();
		for (User contact : contacts.values()) {
			try {
				boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
						|| contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
				if (passesFilter) {
					arrayList.add(contact.clone());
				}
			} catch (CloneNotSupportedException ex) {
				Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Collections.sort(arrayList, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});
		int end = start + maxresults;
		if (end > arrayList.size()) {
			end = arrayList.size();
		}
		return arrayList.subList(start, end);
	}

	public synchronized long count() {
		return contacts.size();
	}

	public synchronized void delete(User value) {
		contacts.remove(value.getId());
	}

	public synchronized void save(User entry) {
		if (entry == null) {
			LOGGER.log(Level.SEVERE, "User is null");
			return;
		}
		if (entry.getId() == null) {
			entry.setId(nextId++);
		}
		try {
			entry = (User) entry.clone();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		contacts.put(entry.getId(), entry);
	}

	public void ensureTestData() {
		if (findAll().isEmpty()) {
			
			final String[] names = new String[] { "Agata Makowska", "Zofia Wasilewska", "Damian Sobczak",
					"Marcin Nowak", "Klaudia Chrzanowska", "Maciej Sawicki", "Gabriela Malinowska", "Lena Marciniak",
					"Aleksander Maj", "Zuzanna Janiszewska", "Adrian Kurek", "Maja Jankowska", "Aleksandra Michalska",
					"Jakub Tomczyk", "Kamil Dudek", "Jan Sikorski", "Dawid Marcinkowski", "Oliwia Wolska", "Julia Duda",
					"Pola Maj", "Julia Sawicka", "Szymon Makowski", "Hanna Wesołowska", "Bartek Szewczyk",
					"Damian Lewandowski", "Jan Białek", "Alicja Kuczyńska", "Oliwia Stefańska", "Miłosz Baran",
					"Sebastian Włodarczyk" };
			
			
			Random r = new Random(0);
			for (String name : names) {
				String[] split = name.split(" ");
				User c = new User();
				c.setFirstName(split[0]);
				c.setLastName(split[1]);
				c.setEmail(split[0].toLowerCase() + split[1].toLowerCase() + "@gmail.com");
				int daysOld = 0 - r.nextInt(365 * 15 + 365 * 60);
				c.setBirthDate(LocalDate.now().plusDays(daysOld));
				c.setPassword(split[0].toLowerCase() + "123");
				save(c);
			}
		}
	}

	public boolean saveToFile() {

		List<User> list = findAll();

		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new FileOutputStream("D://data.txt"));

			for (User user : list)
				pw.println(user.toString());

		} catch (FileNotFoundException e) {
			System.out.println("Błąd tworzenia pliku");
		} finally {
			pw.close();
		}
		
		return true;

	}

}