package com.student.application;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Will be injected into other classes to share common variables from app properties that require a PostConstruct before being ready
 * @author Jason Fan
 *
 */
@Component
public class PostPropertyConfig {
	@Value("${repoPath}")
	private String repoPath;
	
	@PostConstruct
	public void cleanPath() {
		this.repoPath = this.repoPath.replace("/", File.separator); //  Replace file separators used in app properties file (forward-slash) with whatever system uses
		this.repoPath = System.getProperty("user.dir") + this.repoPath; //  Make an abs path, not able to do this in application properties alone so is in post construct
	}
	
	public String getRepoPath() {
		return this.repoPath;
	}
}
