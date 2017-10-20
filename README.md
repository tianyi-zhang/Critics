# Critics

## Set up Critics
1. Check out our code: git clone https://github.com/tianyi-zhang/Critics.git
2. Import all projects into your Eclipse workspace as Java projects
3. Install Subclipse ([instruction](http://web.mit.edu/6.005/www/fa10/labs/procedural_java/subclipse.html)) 
4. Install GEF Zest 
	- Copy all jar files in the gef-zest folder into your Eclipse plugins folder, e.g., /home/troy/eclipse-J2EE/plugins
	- Restart your Eclipse with the clean option by adding "-clean" in the first line of the eclipse.ini file
	- Remember to remove the added line after restarting it
5. Resolve potential Eclipse plugin bundle issues.
	- Extension point schema issue, e.g., "Referenced element 'enablement' is not defined" in Eclipse luna
		- Eclipse luna no longer ships the jar with the schema source files along with the org.eclipse.core.expressions plugin.
		- Go to your Eclipse plugins folder and search for org.eclipse.core.expressions. Then find out the version of the org.eclipse.core.expressions plugin in your Eclipse. For example, in Eclipse Luna Java EE SR2 (4.4.2), the expressions plugin is org.eclipse.core.expressions_3.4.600.v20140128-0851.jar and therefore its version is 3.4.600.
		- Find the expressions source plugin with the same version (the version matters!) and copy it to your Eclipse plugins folder. In the previous example, the expressions source plugin is org.eclipse.core.expressions.source_3.4.600.v20140128-0851.jar. We have already included three versions of expressions source plugin in Eclipse Luna and Kepler in the expressions.schema folder. So you can also directly copy the right version from there. You can also search in http://grepcode.com/.
		- Restart your Eclipse.

**Note:** 
- Please do not try to install the GEF Zest using Eclipse Install New Software wizard. Critics depends on an old version of GEF Zest that is no longer available in the GEF software site.
- This setup instructions have been tested on Eclipse Kepler and Luna. If you are using other Eclipse versions, you may find some Eclipse plugin bundles issues, since some bundles Critics depends on may not be available in more recent Eclipse versions. Please email me (tianyi.zhang@cs.ucla.edu) with the Eclipse version you have trouble with.

## Run/Debug Critics
1. Update all paths in the configuration file, config.txt in UT.CONFIG.

## Deploy Critics
