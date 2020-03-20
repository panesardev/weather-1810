package code.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Lazy
public class HistoryManager {

	@Autowired private Logger log;
	
	private File history = new File("history.txt");
	
	private Supplier<PrintWriter> getWriter = () -> {
		try {
			return new PrintWriter(new FileWriter(history, true));
		} catch (Exception e) {
			log.print.accept("Error occured" + e.getLocalizedMessage());
			return null;
		}
	};
	
	public Consumer<String> insertData = (city) -> {
		PrintWriter writer = getWriter.get();
		writer.println(city);
		writer.close();
		log.print.accept("new location searched : " + city);
	};
	
	public Runnable clear = () -> {
		try {
			history.delete();
			new File("history.txt").createNewFile();
			log.print.accept("history cleared");
		} catch (IOException e) {
			log.print.accept("Error occured" + e.getLocalizedMessage());
		}
	};

	public Supplier<List<String>> list = () -> {
		List<String> cities = new ArrayList<>();
		try {
			Scanner input = new Scanner(history);
			while (input.hasNextLine())
				cities.add(input.nextLine());
			input.close();
		} catch (FileNotFoundException e) {
			log.print.accept("Error occured" + e.getLocalizedMessage());
		}
		return cities;
	};
	
}
