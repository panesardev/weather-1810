package code.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component @Lazy
public class Logger {

	private File logs = new File("application.log");
	
	public Supplier<PrintWriter> getWriter = () -> {
		try {
			return new PrintWriter(new FileWriter(logs, true));
		} catch (Exception e) {
			System.out.println("Error occured" + e.getLocalizedMessage());
			return null;
		}
	};
	
	public Consumer<String> print = (event) -> {
		PrintWriter writer = getWriter.get();
		writer.println("["+new Date().toString()+"] "+event);
		writer.close();
	};
	
	public Runnable clear = () -> {
		try {
			logs.delete();
			new File("application.log").createNewFile();
		} catch(IOException e) {
			System.out.println("Error occured" + e.getLocalizedMessage());
		}
	};
	
	public Supplier<List<String>> list = () -> {
		List<String> logList = new ArrayList<>();
		try {
			Scanner input = new Scanner(logs);
			while (input.hasNextLine())
				logList.add(input.nextLine());
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error occured" + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return logList;
	};
}
