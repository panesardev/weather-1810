package code.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import code.components.HistoryManager;
import code.components.JsonConvertor;
import code.components.Logger;

public class Response {

	@Autowired 
	protected JsonConvertor JSON;
	@Autowired 
	protected HistoryManager history;
	@Autowired 
	protected RestTemplate restTemplate;
	@Autowired 
	protected Logger log;
	
	@Value("${api.key}") 
	protected String API_KEY;
	
}
