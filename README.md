# Critics

## Set up Criticts
1. check out our code: git clone https://github.com/tianyi-zhang/Critics.git
2. import all projects into your Eclipse workspace as Java projects
3. install Subclipse
4. install Zest 
	- add update site http://download.eclipse.org/tools/gef/gef4/updates/releases
	- choose GEF4 Zest
5. intall the Zest Layout plugin (it is not available anymore in the current GEF4 Zest framework so we have to install it manually)
	- find the org.eclipse.zest.layout jar file in the zest.layout folder
	- copy the jar file to the plugins folder in your Eclipse, e.g., /home/troy/eclipse-J2EE/plugins
	- restart your Eclipse with the clean option by adding "-clean" in the first line of the eclipse.ini file
6. resolve extension point schema issues, e.g., a reference to "Referenced element 'enablement' is not defined" (this issue may occur in recent Eclipse versions since the default org.eclipse.core.expressions plugin no longer includes the extension point schema and we have to import the schema manually from a jar file that includes the schema source code)
	- find the org.eclipse.core.expressions.source jar file in the expressions.schema folder
	- copy the jar file to the plugins folder in your Eclipse, e.g., /home/troy/eclipse-J2EE/plugins
	- restart your Eclipse with the clean option by adding "-clean" in the first line of the eclipse.ini file
