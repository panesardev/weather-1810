package code.resources;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import code.components.HistoryManager;
import code.components.JsonConvertor;
import code.components.Logger;

@Controller
public class RootResource {
	
	@Autowired private JsonConvertor JSON;
	@Autowired private HistoryManager history;
	@Autowired private RestTemplate restTemplate;
	@Autowired private Logger log;

	@Value("${api.key}") 
	private String API_KEY;
	
	@GetMapping("/")
	private String bootUp(Model web) {
		if (history.list.get().isEmpty())
			web.addAttribute("notEmptyHistory", false);
		else
			web.addAttribute("notEmptyHistory", true);
		
		if (log.list.get().isEmpty())
			web.addAttribute("notEmptyLogs", false);
		else
			web.addAttribute("notEmptyLogs", true);

		web.addAttribute("historyList", history.list.get());
		web.addAttribute("logs", log.list.get());
		return "root.html";
	}
	
	@PostMapping("/weather-data")
	private String getWeatherData(Model web, @RequestParam("location") String LOCATION) {

		final String URL = "https://api.openweathermap.org/data/2.5/weather?q="
	                     + LOCATION + "&appid=" + API_KEY + "&units=metric";		
		try {
			String result = restTemplate.getForObject(URL, String.class);
			
			Map<String, Object> all, main, sys, coord, wind;
			ArrayList<Map<String, Object>> weather;
			
			all 	= JSON.toMap.apply(result.toString());
			main 	= JSON.toMap.apply(all.get("main").toString());
			sys 	= JSON.toMap.apply(all.get("sys").toString());
			coord 	= JSON.toMap.apply(all.get("coord").toString());
			wind 	= JSON.toMap.apply(all.get("wind").toString());
			weather = (ArrayList<Map<String, Object>>) all.get("weather");
			
			// MAIN
			web.addAttribute("temp", ((Double) main.get("temp")).intValue());
			web.addAttribute("temp_min", ((Double) main.get("temp_min")).intValue());
			web.addAttribute("temp_max", ((Double) main.get("temp_max")).intValue());
			web.addAttribute("feels_like", ((Double) main.get("feels_like")).intValue());
			web.addAttribute("humidity", main.get("humidity"));
			// SYS
			web.addAttribute("country", sys.get("country"));
			// COORD
			web.addAttribute("longitude", coord.get("lon"));
			web.addAttribute("latitude", coord.get("lat"));
			// WEATHER
			web.addAttribute("description", weather.get(0).get("description"));
			web.addAttribute("main", weather.get(0).get("main"));
			// WIND
			web.addAttribute("speed", wind.get("speed"));
			web.addAttribute("degree", wind.get("deg"));
			// LOCATION
			web.addAttribute("location", all.get("name"));
			
			history.insertData.accept(LOCATION);
		} catch (Exception e) {
			web.addAttribute("error", true);
			return "root.html";
		} 
		return "weather-data.html";
	}
	
	@GetMapping("/clear-history")
	private String clearHistory() {
		history.clear.run();
		return "redirect:/";
	}
	
	@GetMapping("/clear-logs")
	private String clearLogs() {
		log.clear.run();
		return "redirect:/";
	}
	
}
