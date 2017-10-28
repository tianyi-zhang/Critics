# Critics
An Eclipse plug-in to search similar program changes and detect missing/inconsistent edits.

## Set up
1. Install Subclipse ([instruction](http://web.mit.edu/6.005/www/fa10/labs/procedural_java/subclipse.html)) 

2. Install GEF Zest
	- Copy all jar files in the gef-zest folder into your Eclipse plugins folder, e.g., /home/troy/eclipse-J2EE/plugins.
	- Restart your Eclipse with the clean option by adding "-clean" in the first line of the eclipse.ini file.
	- Remember to remove the added line after restarting the Eclipse.

3. Check for missing or incompatible Eclipse bundles
	- Incompatible org.eclipse.core.expressions plugin in Eclipse Luna. This plugin no longer includes the extension point schema files in Eclipse Luna, which will cause a dependency error in Critics, **Referenced element 'enablement' is not defined**.
		- Go to your Eclipse plugins folder and search for org.eclipse.core.expressions. Then find out the version of the org.eclipse.core.expressions plugin in your Eclipse. For example, in Eclipse Luna Java EE SR2 (4.4.2), the expressions plugin is org.eclipse.core.expressions_3.4.600.v20140128-0851.jar and therefore its version is 3.4.600.
		- Find the expressions source plugin with the same version (the version matters!) and copy it to your Eclipse plugins folder. In the previous example, the expressions source plugin is org.eclipse.core.expressions.source_3.4.600.v20140128-0851.jar. We have already included three versions of expressions source plugin in Eclipse Luna and Kepler in the expressions.schema folder. So you can also directly copy the right version from there. You can also search in http://grepcode.com/.
		- Restart your Eclipse.

**Notes:** 
- Please do not install GEF Zest using the Eclipse Install New Software wizard. Critics depends on an old version of GEF Zest, which is no longer available in the GEF software site.
- The setup instructions have been tested on Eclipse Kepler and Luna. If you are using other Eclipse versions, you may find some Eclipse plugin bundle issues, since some bundles thatCritics depends on may not be available in more recent Eclipse versions. Please email me (tianyi.zhang@cs.ucla.edu) with the Eclipse version you have trouble with.

## Install Critics as an Eclipse plug-in

Download the released plug-in jar. You can simply drop this jar file in the **dropin** folder in the Eclipse home directory and restart your Eclipse.

## Run/Debug Critics from the source code

1. Check out our code
```bash
git clone https://github.com/tianyi-zhang/Critics.git
```
2. Import the following eight projects into your Eclipse workspace as Java projects.
	- edu.cmu.cs.crystal includes the source code of the program analysis framework.
	- edu.utexas.seal.plugins includes the source code of the Critics plugin.
	- org.eclipse.compare includes the source code of the extended Eclipse Compare plugin.
	- org.eclipse.jdt.core includes the source code of the extendded JDT Core plugin.
	- org.eclipse.jdt.ui includes the source code of the JDT UI plugin that the JDT Core plugin depends on.
	- UT includes the source code of the extended RTED tree matching algorithm and also the changedistiller algorithm.
	- UT.INPUT includes the resource files such as icon images of Critics.
	- UT.CONFIG includes the configuration file.

3. Configure file paths in UT.CONFIG/config.txt.

4. Launch a separate Eclipse application
	- Click **Run > Run Configuration**.
	- Double click **Eclipse Application** in the tree viewer to the left to create a new Eclipse Application launch configuration.
	- Change the Eclipse Application configuration name and the workspace.
	- Click **Run**.

![run_configuration](tutorial/run_configuration.png?raw=true)

5. Add Critics views to the current perspective
	- Click **Windows > Show View -> Other**.
	- Find and add **Diff Template View (New Rev.)**,  **Diff Template View (Old Rev.)**, and **Matching Result**.
		- **Diff Template View (New Rev.)** shows the visualized abstract syntax tree of the new revision of the selected edits.
		- **Diff Template View (Old Rev.)** shows the visualized abstract syntax tree of the old revision of the selected edits.
		- **Matching Result** shows the correct edits that match the selected edits as well as the missing/inconsistent edits.

![add_critics_views](tutorial/add_view.png?raw=true)

6. Re-arrange the Critics views via drag&drop

![rearrange_critics_views](tutorial/rearrange_view.png?raw=true)

## Evaluation Dataset

Download the [evaluation dataset](https://www.dropbox.com/s/p2ikyu3iwm7lfww/DataSet.zip?dl=0). The evaluation dataset includes 7 diff patches from Eclipse JDT and SWT. Each diff patch include two Eclipse JDT/SWT revisions. Note that Eclipse SWT includes multiple Java projects, including GTK, MOTIF, PHOTON, WIN, CARBON.

| ID |      Revision       | Size (LOC)| Change Description |
|:--:|:-------------------:|----------:|--------------------|
| 1  | JDT 9800 vs. 9801   |       190 |initialize a variable in a for loop instead of using a hashmap|
| 2  | JDT 10610 vs. 10611 |       680 |extract the logic of unicode traitement to a method|
| 3  | JDT 10640 vs. 10641 |       680 |extract the logic of unicode traitement to a method|
| 4  | SWT 14344 vs. 14345 |       621 |removing a switch statement and its body|
| 5  | SWT 13515 vs. 13516 |       450 |refactor the local variable for ExpandItem object to a field|
| 6  | SWT 14502 vs. 14503 |       484 |add a statement to assign this to the field, dndWidget  of a typedListener object|
| 7  | SWT 16738 vs. 16739 |       216 |refactor the way to get the region of selected text by first getting a start index instead of 0|

## Investigate Diff Patches with Critics 

**Note:** You can also watch a [demo video](https://www.youtube.com/watch?v=F2D7t_Z5rhk).

1. Import a patch into your Eclipse workspace. 
	- Make sure you **copy the projects in a patch into the workspace of the launched Eclipse application** first. Otherwise Critics will not be able to find the selected changes.
	- Import the project revisions in the selected patch into the workspace of the launched Eclipse application. Ignore any compilation errors in the imported projects.

2. Select a program change. 
	- Select two Java project revisions and right click **Compare With > Each Other**. You can also compare corresponding Java files in two revisions.
	- In the side-by-side diff view, select a change, right click, and choose **Select Diff Region**.

![compare_visualize_edits](tutorial/compare_visualize.png?raw=true)

Critics identifies unchanged program statements that are dependent to the selected change (i.e., *change context*) and visualizes the selected change and context as a change template in a side-by-side **Diff Temaplte View**.

3. Customize the change template.
	- **Generalize Variables.** Right click a tree node in the visualized change template, select **Generalize**, and then click a variable you want to generalize. Once a variable name is  generalized, it can be matched to any other variables. You can also right click a blank area in **Diff Template View**, select **Generalize > Open Generalization Dialog**, and generalize multiple variables at once.
	- **Exclude/Include Statements.** You can exclude a tree node by double clicking it. The excluded node will not be used when searching for similar changes. You can double click an excluded node to include it again. 

4. Right click and select **Summarize Changes and Detect Anomalies**. 


