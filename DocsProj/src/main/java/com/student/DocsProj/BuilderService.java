package com.student.DocsProj;


import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

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
	
	@Override
	public void run(String... args) throws IOException {
		String repoPath = config.getRepoPath();
		this.repoFile = new File(repoPath);
		removeDir(this.repoFile);  //  Clean out any old local repos
		repoFile.mkdirs(); // handle if false
		try {
			Git.cloneRepository().setURI(repoURL).setDirectory(repoFile).call();
		} catch (TransportException e) {
		
		} catch (InvalidRemoteException e) {
				
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.repo = new FileRepositoryBuilder().setGitDir(new File(repoPath + File.separator + ".git")).build(); //  Account for if linux/windows (file separator)
	}
	
	/**
	 * Starting from directory, create an HTML file for every .md file encountered
	 * @throws IOException
	 */
	public void generateHTMLFromDir(File file) throws IOException {  //jgit treewalk
		if (!file.exists()) return;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				generateHTMLFromDir(files[i]);
			}
		} else {
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
		        
		        File newFile = getNewHTML(dir, FilenameUtils.removeExtension(name));
		        if (newFile != null) {
		        	pageCleaner.cleanup(newFile);
		        } else {
		        }
			}
		}
	}
	
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