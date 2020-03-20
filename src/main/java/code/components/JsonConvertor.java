package code.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component @Lazy
public class JsonConvertor {
 
	private Gson GSON = new Gson();
	
	public Function<String, Map<String, Object>> toMap = 
		data -> 
			GSON.fromJson(data, new TypeToken<HashMap<String, Object>>()
			{}.getType());
		
}
