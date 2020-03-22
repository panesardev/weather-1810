package code.response;

import java.util.List;
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
public class RootResponse {
	
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
		try {
			final String weatherURL = "https://api.openweathermap.org/data/2.5/weather?q="
	                     + LOCATION + "&appid=" + API_KEY + "&units=metric";		

			String result = restTemplate.getForObject(weatherURL, String.class);
			
			Map<String, Object> all = JSON.toHashMap.apply(result.toString());
			Map<String, Object> main = JSON.toHashMap.apply(all.get("main").toString());
			Map<String, Object> sys = JSON.toHashMap.apply(all.get("sys").toString());
			Map<String, Object> coord = JSON.toHashMap.apply(all.get("coord").toString());
			Map<String, Object> wind = JSON.toHashMap.apply(all.get("wind").toString());
			List<Map<String, Object>> weather = (List<Map<String, Object>>) all.get("weather");
            
			String countryCode = (String) sys.get("country");
			String countryURL = "https://restcountries.eu/rest/v2/alpha/"+countryCode;
			String countryInfo = restTemplate.getForObject(countryURL, String.class);
			String countryName = (String) JSON.toHashMap.apply(countryInfo).get("name");
			
			// MAIN
			web.addAttribute("temp", ((Double) main.get("temp")).intValue());
			web.addAttribute("temp_min", ((Double) main.get("temp_min")).intValue());
			web.addAttribute("temp_max", ((Double) main.get("temp_max")).intValue());
			web.addAttribute("feels_like", ((Double) main.get("feels_like")).intValue());
			web.addAttribute("humidity", main.get("humidity"));
			// SYS
			web.addAttribute("country", countryName);
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
