package code.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootResponse extends Response {
	
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
