package com.student.application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.student.application.PostPropertyConfig;
import com.student.application.builder.BuilderService;
import com.student.application.structure.HomeStructure;

import org.apache.commons.io.FilenameUtils;


import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

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
	
	@Autowired
	HomeStructure structure;
	
	@RequestMapping("/repo/**")
	public String repoController(HttpServletRequest request) {
		String uri = request.getRequestURI().toString();
		String repoPath = config.getRepoPath();
		String[] subPaths = uri.split("/");
		
		if (subPaths.length > 1) {
			if (subPaths[1].equals("repo")) {  // Check if it's a request to a page from the repo
				int docPageSeparator = uri.lastIndexOf("/");
				String docName = uri.substring(docPageSeparator + 1).toLowerCase();  //  Lowercase for url lowercase convention
				String fullPathURL = uri.replace("/", File.separator); //  clean up the url a bit
				String dirPathURL = fullPathURL.substring(5, docPageSeparator).replace('-', ' '); //  let dashes represent spaces
				File file = new File(repoPath + dirPathURL);
				
				if (file.exists()) {
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
				}
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
			}
		}
		
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
	}
	
	
	@RequestMapping("/refresh-ping")
	public String refreshController(HttpServletRequest request, @RequestHeader("X-Hub-Signature") String reqSha1, @Value("${repoEnvName}") String var) { 		
		try {
			String body = request.getReader().lines().collect(Collectors.joining());  
			String key = System.getenv(var);

			String hash = "sha1=" + new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmacHex(body);  //  Create new hmac instance using SHA1 and env key, then get the digest of request body
			
			if (hash.equals(reqSha1)) {  // .equals secure?
				//  Verified the ping is from github 
				this.builder.generateHTMLFromDir(new File(config.getRepoPath())); // for now regenerates whole thing, in future only regen starting from dir that changed
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	@Override
	public void run(String... args) {
		this.builder.generateHTMLFromDir(new File(config.getRepoPath()));
	}
}