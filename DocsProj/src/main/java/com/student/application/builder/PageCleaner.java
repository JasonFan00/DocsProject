package com.student.application.builder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.MalformedInputException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/**
 * Cleans up some extra html that grip puts into the html files, in future will use it to possibly add some extra elements
 * @author Jason Fan
 *
 */
@Component
public class PageCleaner {
	
	public void cleanup(File file) {
		boolean changes = false;
		try {
			Document doc = Jsoup.parse(file, "UTF-8");
			Elements eles = doc.getElementsByClass("octicon-book");
			
			doc.select("link[rel='icon']").attr("href", "");  //  Get the favicon returned by github api, set it to blank

			for (Element ele : eles) {
				//  Select the inner octicon then get its parent <h3>, and then can remove
				if (ele.hasParent()) {
					ele.parent().remove();
					changes = true;
				}
			}
			
			if (changes) {
				String result = doc.html();
				PrintWriter pwriter = new PrintWriter(file);
				pwriter.println(result);  //  Override contents of file with result
				pwriter.close();
			}
		} catch (FileNotFoundException e) {
			
		} catch (SecurityException e) {
			
		} catch (IOException e ) {
			e.printStackTrace();
		}
		
		
		
	}
}