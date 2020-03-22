package code.components;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class IPCollector {

	private List<String> headers = Arrays.asList("X-Forwarded-For", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR");
	
	public Consumer<HttpServletRequest> logAddress = request -> {
		headers.forEach(header -> {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip))
	            System.out.println(ip);
	    });
	    System.out.println(request.getRemoteAddr());
	};
	
}
