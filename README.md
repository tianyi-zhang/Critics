# Critics

## Set up Critics
1. Check out our code
```bash
git clone https://github.com/tianyi-zhang/Critics.git
```
2. Import all the following eight projects into your Eclipse workspace as Java projects.
	- edu.cmu.cs.crystal includes the source code of the program analysis framework.
	- edu.utexas.seal.plugins includes the source code of the Critics plugin.
	- org.eclipse.compare includes the source code of the extended Eclipse Compare plugin.
	- org.eclipse.jdt.core includes the source code of the extendded JDT Core plugin.
	- org.eclipse.jdt.ui includes the source code of the JDT UI plugin that the JDT Core plugin depends on.
	- UT includes the source code of the extended RTED tree matching algorithm and also the changedistiller algorithm.
	- UT.INPUT includes the resource files such as icon images of Critics.
	- UT.CONFIG includes the configuration file.
3. Install Subclipse ([instruction](http://web.mit.edu/6.005/www/fa10/labs/procedural_java/subclipse.html)) 
4. Install GEF Zest
	- Copy all jar files in the gef-zest folder into your Eclipse plugins folder, e.g., /home/troy/eclipse-J2EE/plugins.
	- Restart your Eclipse with the clean option by adding "-clean" in the first line of the eclipse.ini file.
	- Remember to remove the added line after restarting the Eclipse.
5. Resolve potential Eclipse plugin bundle issues
	- the extension point schema issue, e.g., "Referenced element 'enablement' is not defined" in Eclipse luna
		- Eclipse luna no longer ships the schema source files along with the org.eclipse.core.expressions plugin.
		- Go to your Eclipse plugins folder and search for org.eclipse.core.expressions. Then find out the version of the org.eclipse.core.expressions plugin in your Eclipse. For example, in Eclipse Luna Java EE SR2 (4.4.2), the expressions plugin is org.eclipse.core.expressions_3.4.600.v20140128-0851.jar and therefore its version is 3.4.600.
		- Find the expressions source plugin with the same version (the version matters!) and copy it to your Eclipse plugins folder. In the previous example, the expressions source plugin is org.eclipse.core.expressions.source_3.4.600.v20140128-0851.jar. We have already included three versions of expressions source plugin in Eclipse Luna and Kepler in the expressions.schema folder. So you can also directly copy the right version from there. You can also search in http://grepcode.com/.
		- Restart your Eclipse.

**Notes:** 
- Please do not install GEF Zest using the Eclipse Install New Software wizard. Critics depends on an old version of GEF Zest, which is no longer available in the GEF software site.
- The setup instructions have been tested on Eclipse Kepler and Luna. If you are using other Eclipse versions, you may find some Eclipse plugin bundle issues, since some bundles thatCritics depends on may not be available in more recent Eclipse versions. Please email me (tianyi.zhang@cs.ucla.edu) with the Eclipse version you have trouble with.

## Run/Debug Critics
1. Update all paths in the configuration file, config.txt in UT.CONFIG.
2. Launch a separate Eclipse application
	- Click **Run > Run Configuration**.
	- A new Eclipse Application launch configuration can be created by double-clicking on the **Eclipse Application** node in the tree viewer to the left.
	- Change the Eclipse Application configuration name and the workspace as preferred.
	- Click **Run**.
![run_configuration](tutorial/run_configuration.png?raw=true)
3. Add Critics views to the current perspective
	- Click Windows > Show View -> Other
	- Find and add **Diff Template View (New Rev.)**,  **Diff Template View (Old Rev.)**, and **Matching Result**.
		- "Diff Template View (New Rev.)" shows the visualized abstract syntax tree of the new revision of the selected edits.
		- "Diff Template View (Old Rev.)" shows the visualized abstract syntax tree of the old revision of the selected edits.
		- "Matching Result" shows the correct edits that match the selected edits as well as the missing/inconsistent edits.
![add_critics_views](tutorial/add_view.png?raw=true)
4. Re-arrange the added views as preferred
![rearrange_critics_views](tutorial/rearrange_view.png?raw=true)
5. Run Critics with sample data

## Deploy Critics
