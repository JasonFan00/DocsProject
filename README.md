# Docs (WIP)

Docs is a web application that will take a set of .md files organized in a file structure, and generate a navigable, styled, and scalable website.  In a nutshell, here's how it works:

1.  Docs retrieves the collection of .md files and folders to generate a website out of from the repository where it is hosted at.
2.  Every .md file is converted to a .html.  We use GRIP so flavored markdown is also supported.
3.  An in-memory representation/model of the file structure (and files) is created and we use thymeleaf to create the view out of it

The home page contains a section that is an ordered listing of all the markdown files, now web pages, all organized intuitively based on the file structure in which they are organized in.  

Some notable things:
1.  Docs supports nested categories.  Every folder is a category, so folders within folders are supported.
2.  The organization of the .md files' hyperlinks in each category (called category items) is ordered lexicographically.
3.  Each category (folder in the file structure) can have a "CategoryDescriptor.txt", which becomes a little sub-heading to each category heading.
4.  When a change is made to the content repository (content repo stores the file structure/folders/.md files that make up the content of the site), it sends a ping to the web app to update itself.
5.  The important bit for the front-end is "core", aka the section of the home page that is dynamically generated based on the content.  It is a thymeleaf fragment, so can easily be plugged into other web pages/needs!

Our goal is to make this useful to the Brandeis CS dept!

Content repo:  https://github.com/JasonFan00/Content

Spring MVC, Thymeleaf, HTML/CSS
