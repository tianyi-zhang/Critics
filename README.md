# Critics
An Eclipse plug-in to search similar program changes and detect missing/inconsistent edits.

## Set up
1. Install Subclipse 1.8.x ([instruction](https://devjack.de/how-to-install-subclipse-1-8-x-in-eclipse-luna/)). It is recommended to use Subclipse 1.8.x instead of 1.6.x because Subclipse 1.6.x may introduce circular dependencies when exporting Critics as deployable plugin jars. 

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
- **You need follow this setup instruction to install Subclipse and GEF Zest, no matter you install Critics from plugin jars or build Critics from the source code.**

## Install Critics from plugin jars

Critics is exported to 6 deployable plugin jars in the plugin-jars folder in this repository. Critics customizes three existing plugins in Eclipse in order to capture user interaction with Eclipse UI components, e.g, user selections in Eclipse compare editor. However, different versions of Eclipse have different constraints on plugin versions. So far, Critics plugin jars only work with Eclipse Luna SR2 ([download](http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/lunasr2)). If you want to install Critics plugin jars in other Eclipse versions, you either need to make sure they accept our customized plugin versions or have to modify the bundle versions in Critics's source code and export Critics to jars again.

To install Critics from plugin jars, you need to (1) place org.eclipse.compare\_3.5.501.v20140817-1445.jar, org.eclipse.jdt.core\_3.10.2.v20150120-1634.jar, org.eclipse.jdt.ui\_3.10.2.v20141014-1419.jar in the eclipse/plugins folder to override the original plugins in Eclipse Luna SR2, and (2) place UT\_1.0.0.201712041123.jar, edu.cmu.cs.crystal\_3.4.2.jar, edu.utexas.seal.plugins\_1.0.0.201712041123.jar in the eclipse/dropins folder. Then add "-clean" in the first line of eclipse.ini and start your Eclipse.

**Notes:**
- If you want to cusomize Critics, installing Critics from plugin jars is not a good option. Because it is inconvenient to update and test your customizations using plugin jars. Please read the next section and learn how to build and run Critics from source code. 
- If you want to install Critics in other Eclipse versions, please read the deployment section in this tutorial.


## Run/Debug Critics from the source code

1. Check out our code
```bash
git clone https://github.com/tianyi-zhang/Critics.git
```
2. Import the following six projects into your Eclipse workspace as Java projects.
	- edu.cmu.cs.crystal includes the source code of the program analysis framework.
	- edu.utexas.seal.plugins includes the source code of the Critics plugin.
	- org.eclipse.compare includes the source code of the extended Eclipse Compare plugin.
	- org.eclipse.jdt.core includes the source code of the extendded JDT Core plugin.
	- org.eclipse.jdt.ui includes the source code of the JDT UI plugin that the JDT Core plugin depends on.
	- UT includes the source code of the extended RTED tree matching algorithm and also the changedistiller algorithm.

3. Launch a separate Eclipse application
	- Click **Run > Run Configuration**.
	- Double click **Eclipse Application** in the tree viewer to the left to create a new Eclipse Application launch configuration.
	- Change the Eclipse Application configuration name and the workspace.
	- Click **Run**.

![run_configuration](tutorial/run_configuration.png?raw=true)

4. Add Critics views to the current perspective
	- Click **Windows > Show View -> Other**.
	- Find and add **Diff Template View (New Rev.)**,  **Diff Template View (Old Rev.)**, and **Matching Result**.
		- **Diff Template View (New Rev.)** shows the visualized abstract syntax tree of the new revision of the selected edits.
		- **Diff Template View (Old Rev.)** shows the visualized abstract syntax tree of the old revision of the selected edits.
		- **Matching Result** shows the correct edits that match the selected edits as well as the missing/inconsistent edits.

![add_critics_views](tutorial/add_view.png?raw=true)

5. Re-arrange the Critics views via drag&drop

![rearrange_critics_views](tutorial/rearrange_view.png?raw=true)

## Evaluation Dataset

Download the [evaluation dataset](https://www.dropbox.com/s/p2ikyu3iwm7lfww/DataSet.zip?dl=0). The evaluation dataset includes 7 diff patches from Eclipse JDT and SWT. Each diff patch includes two Eclipse JDT/SWT revisions. Note that Eclipse SWT contains multiple Java projects, including GTK, MOTIF, PHOTON, WIN, CARBON.

| ID |      Revision       | Size (LOC) | Change Description |
|:--:|:-------------------:|-----------:|--------------------|
| 1  | JDT 9800 vs. 9801   |       190  | initialize a variable in a for loop instead of using a hashmap |
| 2  | JDT 10610 vs. 10611 |       680  | extract the logic of unicode traitement to a method |
| 3  | JDT 10640 vs. 10641 |       680  | extract the logic of unicode traitement to a method |
| 4  | SWT 14344 vs. 14345 |       621  | removing a switch statement and its body |
| 5  | SWT 13515 vs. 13516 |       450  | refactor the local variable for ExpandItem object to a field |
| 6  | SWT 14502 vs. 14503 |       484  | add a statement to assign this to the field, dndWidget of a typedListener object |
| 7  | SWT 16738 vs. 16739 |       216  | refactor the way to get the region of selected text by first getting a start index instead of 0 |

## Investigate Diff Patches with Critics 

**Note:** You can also watch a [demo video](https://www.youtube.com/watch?v=F2D7t_Z5rhk).

1. Import a patch into your Eclipse workspace. 
	- Make sure you **copy the projects in a patch into the workspace of the launched Eclipse application** first. Otherwise Critics will not be able to find Java files in the patch.
	- Import the projects in the selected patch into the workspace of the launched Eclipse application. Ignore any compilation errors in the imported projects.

2. Select a program change. 
	- Select two project revisions and right click **Compare With > Each Other**. You can also compare corresponding Java files in two revisions.
	- In the side-by-side diff view, select a change, right click, and choose **Select Diff Region**.

![compare_visualize_edits](tutorial/compare_visualize.png?raw=true)

Critics identifies unchanged program statements that are dependent to the selected change (i.e., *change context*) and visualizes the selected change and context as a change template in a side-by-side **Diff Temaplte View**.

3. Customize the change template.
	- **Generalize Variables.** Right click a tree node in the visualized change template, select **Generalize**, and then click a variable you want to generalize. Once a variable name is  generalized, it can be matched to any other variables. You can also right click a blank area in **Diff Template View**, select **Generalize > Open Generalization Dialog**, and generalize multiple variables at once.
	- **Exclude/Include Statements.** You can exclude a tree node by double clicking it. The excluded node will not be used when searching for similar changes. You can double click an excluded node to include it again. 

4. Right click and select **Summarize Changes and Detect Anomalies**. All similar edits and anomalies are displayed in **Matching Results View**. If you want to view the source code of each change, you can double click an entry in the list, it will jump to the source code location of the change.

![summarize_similar_edits_and_detect_anomalies](tutorial/search_results.png?raw=true)

## Deploy Critics as plugin jars

To export Critics from source code to plugin jars, click **File > Export** and then select **Deployable plugins and fragments**. 

![deploy_step1](tutorial/deploy_step1.png?raw=true)

Click **Next**, select all 6 plugins, choose the **Directory** option, and specify the destination directory in the **destination** tab. 

![deploy_step2](tutorial/deploy_step2.png?raw=true)

There are two ways to deploy Critics to other Eclipse versions.

**Option 1.** Modify our customized plugin versions as the ones in your own Eclipse so that your Eclipse will accept our customized plugins. This option is easy but risky because if there are major and critical differences between our customized plugin version and the original plugin version, your Eclipse may break at runtime.
- Step 1. Go to the **plugins** folder in your eclipse installation directory and figure out the versions of org.eclipse.compare, org.eclipse.jdt.ui, and org.eclipse.jdt.core.
- Step 2. For each of these three customized plugins, update the **Bundle-Version** entry in the MANIFEST.MF file to the corresponding version you just find in the previous step.
- Step 3. Export all 6 Critics projects again as plugin jars.

**Option 2.** Port our customization to the corresponding plugins in your own Eclipse. This option involves many manual edits but is safe.
- Step 1. Create a new workspace for finding out customization edits (we don't want to corrupt the existing workspace of Critics).
- Step 2. Import the three plugin projects we want to customize, org.eclipse.jdt.core, org.eclispe.jdt.ui, and org.eclipse.compare. Ignore any build errors since our goal is to find the customization edits instead of building them.
- Step 3. Import the source code of the original plugins that we initially edited. Do this by clicking **File > Import**, selecting **Plug-ins and Fragments**, choose the **Directory** option, and specify the folder where the initial plugins are (see screenshots below). You can find the original plugins with source code in each of the plugin project folder, e.g, org.eclipse.jdt.core.source\_3.9.1.v20130905-0837.jar and org.eclipse.jdt.core\_3.9.1.v20130905-0837.jar in org.eclipse.jdt.core. Also select the **Select from all plug-ins and fragments found at the specific location** and the **Projects with source code** option. Click **Next**. Then select the plugin name and click **Add** (see screenshots below). Click **Finish**.
- Step 4. Compare each pair of the initial and customized plugin projects and figure out the customization edits.
- Step 5. In the existing workspace of Critics, delete the plugin projects we want to customize, org.eclipse.jdt.core, org.eclispe.jdt.ui, and org.eclipse.compare. 
- Step 6. Import the three exising plugins from your Eclipse **plugins** folder following the same step in Step 3.
- Step 7. Manually port the customizatione edits you found in Step 4 to these plugin projects you just imported.

![import_plugin_source_step1](tutorial/import_plugin_source.png?raw=true)

![import_plugin_source_step2](tutorial/import_plugin_source_2.png?raw=true)

