package code.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WeatherResponse extends Response {

	@PostMapping("/weather-data")
	private String getWeatherData(@RequestParam("location") String LOCATION,
			Model web, HttpServletRequest request) {
		try {
			String weatherURL = "https://api.openweathermap.org/data/2.5/weather?q="
	                     + LOCATION + "&appid=" + API_KEY + "&units=metric";		

			String result = restTemplate.getForObject(weatherURL, String.class);
			
			Map<String, Object> all = JSON.toHashMap.apply(result);
			Map<String, Object> main = JSON.toHashMap.apply(all.get("main").toString());
			Map<String, Object> sys = JSON.toHashMap.apply(all.get("sys").toString());
			Map<String, Object> coord = JSON.toHashMap.apply(all.get("coord").toString());
			Map<String, Object> wind = JSON.toHashMap.apply(all.get("wind").toString());
			List<Map<String, Object>> weather = (List<Map<String, Object>>) all.get("weather");
            
			String countryCode = (String) sys.get("country");
			String countryURL = "https://restcountries.eu/rest/v2/alpha/"+countryCode;
			String countryInfo = restTemplate.getForObject(countryURL, String.class);
			String countryName = (String) JSON.toHashMap.apply(countryInfo).get("name");
			
			Map<String, Object> attributes = new HashMap<String, Object>() {{
				// MAIN
				put("temp", ((Double) main.get("temp")).intValue());
				put("temp_min", ((Double) main.get("temp_min")).intValue());
				put("temp_max", ((Double) main.get("temp_max")).intValue());
				put("feels_like", ((Double) main.get("feels_like")).intValue());
				put("humidity", main.get("humidity"));
				// SYS
				put("country", countryName);
				// COORD
				put("longitude", coord.get("lon"));
				put("latitude", coord.get("lat"));
				// WEATHER
				put("description", weather.get(0).get("description"));
				put("main", weather.get(0).get("main"));
				// WIND
				put("speed", wind.get("speed"));
			    put("degree", wind.get("deg"));
				// LOCATION
				put("location", all.get("name"));
			}};
			
			web.addAllAttributes(attributes);			
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
	
}
