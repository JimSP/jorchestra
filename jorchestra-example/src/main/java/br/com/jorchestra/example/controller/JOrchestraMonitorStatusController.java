package br.com.jorchestra.example.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.JOrchestraMonitorInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JOrchestraMonitorStatusController {

	@Autowired
	@Qualifier("jOrchestraMonitorInstance")
	public JOrchestraMonitorInstance jOrchestraMonitorInstance;

	@GetMapping(path="jorchestra-slave")
	public @ResponseBody Map<String, String> slaveStatus() {
		final Map<String, String> map = new HashMap<String, String>();
		map.put("slave.main", jOrchestraMonitorInstance.getAppMain());
		map.put("slave.mode", jOrchestraMonitorInstance.getSlaveMode());
		map.put("slave.port", jOrchestraMonitorInstance.getSlavePort());
		map.put("master.port", jOrchestraMonitorInstance.getMasterPort());
		return map;
	}
}
