/*
 * ManageIngestController.java
 * 
 * Copyright (C) 2013
 * 
 * This file is part of Open Geoportal Harvester
 * 
 * This software is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 * 
 * As a special exception, if you link this library with other files to produce
 * an executable, this library does not by itself cause the resulting executable
 * to be covered by the GNU General Public License. This exception does not
 * however invalidate any other reasons why the executable file might be covered
 * by the GNU General Public License.
 * 
 * Authors:: Juan Luis Rodriguez Ponce (mailto:juanluisrp@geocat.net)
 */
package org.opengeoportal.harvester.mvc;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jlrodriguez
 * 
 */
@Controller
public class ManageIngestController {
	@RequestMapping("/manageIngests")
	public String test(ModelMap model) {
		return "ngView";
	}

	@RequestMapping("/rest/ingests")
	@ResponseBody
	public List<Map<String, Object>> getAllIngests(ModelMap model) {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> ingest = new HashMap<String, Object>();
		ingest.put("id", 1);
		ingest.put("name", "New York Subway");
		ingest.put("lastRun", new Date());
		ingest.put("nextRun", null);
		ingest.put("repositories", "Tufts, Harvard, MIT, Berkley");
		resultList.add(ingest);
		ingest = new HashMap<String, Object>();
		ingest.put("id", 2);
		ingest.put("name", "Urban Development");
		ingest.put("lastRun", new Date());
		ingest.put("nextRun", new Date());
		ingest.put("repositories", "Tufts, Harvard, MIT, Berkley");
		resultList.add(ingest);
		
		for(int i=3; i <=20; i++) {
			ingest = new HashMap<String, Object>();
			ingest.put("id", i);
			ingest.put("name", "Name " +i );
			ingest.put("lastRun", new Date());
			ingest.put("nextRun", new Date());
			ingest.put("repositories", "Tufts, Harvard, MIT, Berkley");
			resultList.add(ingest);
		}

		return resultList;
	}
	
	@RequestMapping("/rest/ingests/{id}")
	@ResponseBody
	public Map<String, Object> ingestDetails(ModelMap model, @PathVariable String id) {
		Map<String, Object> ingest = new HashMap<String, Object>();
		ingest.put("id", id);
		ingest.put("name", "Name " + id);
		ingest.put("lastRun", new Date());
		
		Map<String, Object> passed = new HashMap<String, Object>();
		passed.put("restrictedRecords", 745);
		passed.put("publicRecords", 120);
		passed.put("vectorRecords", 850);
		passed.put("rasterRecords", 845);
		ingest.put("passed", passed);
		
		Map<String, Object> warning = new HashMap<String, Object>();
		warning.put("unrequiredFields", 345);
		warning.put("webserviceWarnings", 200);
		ingest.put("warning", warning);
		
		Map<String, Object> errorsMap = new HashMap<String, Object>();
		errorsMap.put("requiredFields", 745);
		errorsMap.put("webServiceErrors", 120);
		errorsMap.put("systemErrors", 58);
		List<SimpleEntry<String, Integer>> requiredFieldSubcat = new ArrayList<SimpleEntry<String,Integer>>();
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("extent", 245));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("themeKeyword", 105));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("placeKeyword", 200));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("topic", 125));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("dateOfContent", 400));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("originator", 32));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("dataType", 400));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("dataRepository", 320));
		requiredFieldSubcat.add(new SimpleEntry<String, Integer>("editDate", 126));
		
		errorsMap.put("requiredFieldsList", requiredFieldSubcat);
		
		List<SimpleEntry<String, Integer>> webServiceErrorList = new ArrayList<SimpleEntry<String,Integer>>();
		webServiceErrorList.add(new SimpleEntry<String, Integer>("error1", 120));
		webServiceErrorList.add(new SimpleEntry<String, Integer>("error2", 120));
		webServiceErrorList.add(new SimpleEntry<String, Integer>("error3", 120));
		webServiceErrorList.add(new SimpleEntry<String, Integer>("error4", 120));
		webServiceErrorList.add(new SimpleEntry<String, Integer>("error5", 120));
		webServiceErrorList.add(new SimpleEntry<String, Integer>("error6", 120));
		errorsMap.put("webServiceErrorList", webServiceErrorList);
		
		List<SimpleEntry<String, Integer>> systemErrorList = new ArrayList<SimpleEntry<String,Integer>>();
		systemErrorList.add(new SimpleEntry<String, Integer>("serror1", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror2", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror3", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror4", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror5", 120));
		systemErrorList.add(new SimpleEntry<String, Integer>("serror6", 120));
		errorsMap.put("systemErrorList", systemErrorList);
				
		ingest.put("error", errorsMap);
				
		return ingest;
	}
	
	@RequestMapping("/rest/ingests/{id}/metadata") 
	public void downloadMetadata(@PathVariable String id, String[] categories, 
			Writer writer, HttpServletResponse response) {
		response.setHeader("Content-Type", "text/plain; charset=utf-8");
		response.setHeader("Content-Disposition", "attachment; filename=metadata_"+id + ".txt");
		
		
		PrintWriter outputWriter = new PrintWriter(writer);
		
		for(String category : categories) {
			outputWriter.println("Category " + category);
		}
	}

}