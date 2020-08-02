package com.student.application.builder;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import com.student.application.PostPropertyConfig;
import com.student.application.structure.HomeStructure;

/**
 * Builds the local repository as well as the respective html files from the .md files
 * @author Jason Fan
 *
 */
@Service
public class BuilderService implements CommandLineRunner {
		
	//  The jgit repository object to represent the repo where all pages content/structure is stored
	private Repository repo;
	private File repoFile;
	
	@Value("${repoURL}") 
	String repoURL;
	
	@Autowired
	PageCleaner pageCleaner;
	
	//  Holds common properties that need a postconstruct operation
	@Autowired
	PostPropertyConfig config;
	
	
	/**
	 * Deletes a directory and its contents, or a file
	 * @param file
	 */
	public static void removeDir(File file) {
		if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();
                for (String ele : files) {
                    removeDir(new File(file, ele));
                }

                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
	}
	
	/** 
	 *  Run - Executed by spring after the application is started
	 */
	@Override
	public void run(String... args) throws IOException {
		String repoPath = config.getRepoPath();
		this.repoFile = new File(repoPath);
		removeDir(this.repoFile);  //  Clean out any old local repos
		repoFile.mkdirs(); // handle if false
		try {
			//  Clone the remote repo
			Git.cloneRepository().setURI(repoURL).setDirectory(repoFile).call();
		} catch (TransportException e) {
		
		} catch (InvalidRemoteException e) {
				
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.repo = new FileRepositoryBuilder().setGitDir(new File(repoPath)).build(); //  Account for if linux/windows (file separator)
	}
	
	/**
	 * Starting from directory, create an HTML file for every .md file encountered.  Accounts for subdirectories as well.
	 * @throws IOException
	 */
	public void generateHTMLFromDir(File file) throws IOException {  //jgit treewalk
		if (!file.exists()) return;
		if (file.isDirectory()) {
			//  if our code reaches this point, we can confirm it is a home category (because directories represent home categories)
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				generateHTMLFromDir(files[i]);
			}
		} else {
			//  if our code gets here, it is a subcategory (because each html file represents a sub category)
			String name = file.getName();
			String ext = FilenameUtils.getExtension(name);
			if (ext.equals("md")) {
				// Generate html file
				File dir = new File(file.getParent());
				ProcessBuilder pb = new ProcessBuilder("grip", name, "--title="+name, "--export");  // grip will replace any old html files with the same name
		        pb.directory(dir); // getParent may occasionally be null, handle it
		        Process proc = pb.start();
		        
		        try {
					proc.waitFor();  //  Block current thread until done
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        //  Find the newly made html file
		        File newFile = getNewHTML(dir, FilenameUtils.removeExtension(name));
		        if (newFile != null) {
		        	pageCleaner.cleanup(newFile);
		        } else {
		        	
		        }
		       		        		
		        
			}
		}
	}
	
	/**
	 * Returns an html file
	 * @param dir Directory to search for
	 * @param nameNoExt Name of html file to find
	 * @return File The html file
	 */
	private File getNewHTML(File dir, String nameNoExt) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			String fileName = file.getName();
			String fileNameNoExt = FilenameUtils.removeExtension(fileName);
			if (FilenameUtils.getExtension(fileName).equals("html") && fileNameNoExt.equals(nameNoExt)) {
				return file;
			}
		}
		return null;
	}
}