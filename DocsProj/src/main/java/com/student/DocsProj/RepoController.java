package com.student.DocsProj;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.apache.commons.io.FilenameUtils;

@RestController
public class RepoController implements CommandLineRunner {
	
	@Autowired
	BuilderService builder;
	
	@Value("${relativeRepoPath}")
	String repoPath;
	
	@RequestMapping("/**") 
	public String controller(HttpServletRequest request) {
		String uri = request.getRequestURI().toString();
		
		String[] subPaths = uri.split("/");
		
		if (subPaths.length > 1) {
			if (subPaths[1].equals("repo")) {  // Check if it's a request to a page from the repo
				int docPageSeparator = uri.lastIndexOf("/");
				String docPath = uri.substring(docPageSeparator + 1).toLowerCase();  //  Lowercase for url lowercase convention
				String fullPath = uri.replace("/", File.separator);
				String dirPath = fullPath.substring(1, docPageSeparator);
				File file = new File(dirPath);
				
				if (file.isDirectory()) { //  Search local repository for the corresponding html file
					File[] files = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						String fileNameNoExt = FilenameUtils.removeExtension(files[i].getName()).toLowerCase();
						String fileExt = FilenameUtils.getExtension(files[i].getName());
						if (fileNameNoExt.equals(docPath) && fileExt.equals("html")) {
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
		
		return "";
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		this.builder.generateHTMLFromDir(new File(repoPath));
	}
}