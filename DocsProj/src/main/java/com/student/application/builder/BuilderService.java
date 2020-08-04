package com.student.application.builder;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
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
	
	@Value("${numberSeparatorStr}")
	private String NUMBER_SEPARATOR_STR;
	
	@Value("${repoURL}") 
	String repoURL;
	
	@Autowired
	PageCleaner pageCleaner;
	
	//  Holds common properties that need a postconstruct operation
	@Autowired
	PostPropertyConfig config;
	
	@Autowired
	HomeStructure HomeStructure;
	
	/**
	 * Deletes a directory and its contents, or a file
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public static void removeDir(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
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
	
	public static void removeDirForce(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		FileUtils.deleteQuietly(file);
	}
	
	/** 
	 *  Remove old repo, clone remote one
	 */
	@Override
	public void run(String... args) throws IOException {
		String repoPath = config.getRepoPath();
		this.repoFile = new File(repoPath);
		if (this.repoFile.exists()) {
			try {
				BuilderService.removeDirForce(this.repoFile);  //  Clean out any old local repos
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			//  Clone the remote repo
			Git.cloneRepository().setURI(repoURL).setDirectory(repoFile).call(); //  Seems this opens up some kind of handle to a file in the .git of cloned repo, so can't delete the .git after cloning bc file is "in use" here
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();	
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Starting from directory, create an HTML file for every .md file encountered.  Accounts for subdirectories as well.
	 * @throws IOException
	 */
	private void generateHTML(File file) throws IOException {  //jgit treewalk
		if (!file.exists()) return;
		if (file.isDirectory()) {
			//  if our code reaches this point, we can confirm it is a home category (because directories represent home categories)
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				generateHTML(files[i]);
			}
		} else {
			//  if our code gets here, it is a subcategory (because each html file represents a sub category)
			String fullName = file.getName();
			String ext = FilenameUtils.getExtension(fullName); 
			if (ext.equals("md")) {
				// Generate html file
				String newFileName = FilenameUtils.removeExtension(fullName).split(this.NUMBER_SEPARATOR_STR)[1];  // To do:  Add error handling here just in case a file is not named properly
				File dir = new File(file.getParent());
				ProcessBuilder pb = new ProcessBuilder("grip", fullName, "--title="+newFileName, "--export");  // grip will replace any old html files with the same name
		        pb.directory(dir); // getParent may occasionally be null, handle it
		        Process proc = pb.start();
		        
		        try {
					proc.waitFor();  //  Block current thread until done
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		        //  Find the newly made html file
		        File newFile = getNewHTML(dir, FilenameUtils.removeExtension(fullName));
		        if (newFile != null) {
		        	pageCleaner.cleanup(newFile);
		        } else {
		        	
		        }
		       		        		
		        
			}
		}
	}
	
	public void generateHTMLFromDir(File file) {
		try {
			this.generateHTML(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.HomeStructure.updateStructure();
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