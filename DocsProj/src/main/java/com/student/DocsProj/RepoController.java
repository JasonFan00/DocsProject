package com.student.DocsProj;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.io.FilenameUtils;

/**
 * Controller that handles all incoming requests with /repo in URL path
 * @author Jason Fan
 *
 */
@RestController
public class RepoController implements CommandLineRunner {
	
	@Autowired
	BuilderService builder;
	
	//  Holds common properties that need a postconstruct operation
	@Autowired
	PostPropertyConfig config;
	
	
	@RequestMapping("/repo/**")
	public String repoController(HttpServletRequest request) {
		String uri = request.getRequestURI().toString();
		String repoPath = config.getRepoPath();
		String[] subPaths = uri.split("/");
		
		if (subPaths.length > 1) {
			if (subPaths[1].equals("repo")) {  // Check if it's a request to a page from the repo
				int docPageSeparator = uri.lastIndexOf("/");
				String docName = uri.substring(docPageSeparator + 1).toLowerCase();  //  Lowercase for url lowercase convention
				String fullPath = uri.replace("/", File.separator);
				String dirPath = fullPath.substring(5, docPageSeparator);
				File file = new File(repoPath + dirPath);
				
				if (file.isDirectory()) { //  Search local repository for the corresponding html file
					File[] files = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						String fileNameNoExt = FilenameUtils.removeExtension(files[i].getName());
						String fileExt = FilenameUtils.getExtension(files[i].getName());
						if (fileNameNoExt.toLowerCase().equals(docName) && fileExt.equals("html")) {
							try {
								return Files.readString(Paths.get(files[i].getPath()), StandardCharsets.ISO_8859_1); // Java 11 , malformed exception from UTF_8 after sending a cleaned page
							} catch (IOException e) {
								
							}
						}
					}
				}
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
			}
		}
		
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
	}
	
	@RequestMapping("/refresh-ping")
	public String refreshController(HttpServletRequest request) { 
		
		return "";
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		this.builder.generateHTMLFromDir(new File(config.getRepoPath()));
	}
}