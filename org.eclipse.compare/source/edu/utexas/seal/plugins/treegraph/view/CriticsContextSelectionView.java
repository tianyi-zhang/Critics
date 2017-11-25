/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package edu.utexas.seal.plugins.treegraph.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.swt.SWT;

import ut.seal.plugins.utils.UTLog;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import edu.utexas.seal.plugins.treegraph.event.UTKeyAdapterGNode;
import edu.utexas.seal.plugins.treegraph.example.GNode;
import edu.utexas.seal.plugins.treegraph.model.CriticsTreeGraphLabelProvider;
import edu.utexas.seal.plugins.treegraph.model.CriticsTreeGraphContentProvider;
import edu.utexas.seal.plugins.util.UTPlugin;

/**
 * @author Myoungkyu Song
 * @date Dec 19, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
@SuppressWarnings("restriction")
public class CriticsContextSelectionView extends ViewPart implements IZoomableWorkbenchPart {
	public static final String			ID							= "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView";

	private GraphViewer					mGraphViewer				= null;
	private TableViewer					mTbViewer					= null;

	private Menu						mPopupMenu					= null;
	private MenuItem					mnParam						= null;
	private Node						mNodeSelectedByMouse		= null;
	private GraphNode					mGraphNodeSelectedByMouse	= null;

	private int							mLayout						= 2;

	private Action						mExampleAction				= null;
	private Action						mDoubleClickAction			= null;
	private Action						mActTreeRefresh				= null;

	private static Map<Node, Menu>		mParamMenus;
	private Point						mPoint;
	private GraphNode					mPrvSelectedGNode			= null;

	private Map<MenuItem, SimpleName>	mParamVars;
	private Map<SimpleName, MenuItem>	mRevParamVars;
	private Map<MenuItem, Node>			mParamNodes;
	private CriticsContextSelectionView	view;

	/**
	 * 
	 */
	public CriticsContextSelectionView() {
		mParamMenus = new HashMap<Node, Menu>();
		mParamVars = new HashMap<MenuItem, SimpleName>();
		mRevParamVars = new HashMap<SimpleName, MenuItem>();
		mParamNodes = new HashMap<MenuItem, Node>();
		view = this;
	}

	/**
	 * 
	 * @param parent
	 */
	public void createPartControl(Composite parent) {
		GridData parentData = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(parentData);

		Composite comGraphViewer = new Composite(parent, SWT.BORDER);
		comGraphViewer.setLayout(new GridLayout(1, false));
		comGraphViewer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		mGraphViewer = new GraphViewer(comGraphViewer, SWT.BORDER);
		Color bgColor = mGraphViewer.getGraphControl().LIGHT_BLUE;
		mGraphViewer.setContentProvider(new CriticsTreeGraphContentProvider());
		mGraphViewer.setLabelProvider(new CriticsTreeGraphLabelProvider(bgColor));

		LayoutAlgorithm layout = setLayout();
		mGraphViewer.setLayoutAlgorithm(layout, true);
		mGraphViewer.applyLayout();
		mGraphViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		mGraphViewer.getGraphControl().addKeyListener(new UTKeyAdapterGNode());
		addEventListener();
		fillToolBar();
		makeRefreshAction();

		// add menu detect listener to record menu location
		mGraphViewer.getControl().addMenuDetectListener(new MenuDetectListener() {
			@Override
			public void menuDetected(MenuDetectEvent e) {
				mPoint = new Point(e.x, e.y);

			}
		});
	}

	void addEventListener() {
		mPopupMenu = new Menu(mGraphViewer.getControl());
		mGraphViewer.getControl().setMenu(mPopupMenu);

		mnParam = new MenuItem(mPopupMenu, SWT.CASCADE);
		mnParam.setText("Generalize");

		MenuItem mSetAllItem = new MenuItem(mPopupMenu, SWT.NONE);
		mSetAllItem.setText("Generalize All");
		mSetAllItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MenuItem paramItem = mPopupMenu.getItem(0);
				for (MenuItem item : paramItem.getMenu().getItems()) {
					// item.setSelection(true);
					SimpleName var = mParamVars.get(item);
					Node selNode = mParamNodes.get(item);
					selNode.parameterize(var);
					propagateParam(var, true);
					mParamNodes.put(item, selNode);
				}
			}
		});

		MenuItem mnFindCxt = new MenuItem(mPopupMenu, SWT.NONE);
		mnFindCxt.setText("Summarize Changes and Detect Anomalies");

		// MenuItem mnGenerization = new MenuItem(mPopupMenu, SWT.NONE);
		// mnGenerization.setText("Generization");

		MenuItem mnRefresh = new MenuItem(mPopupMenu, SWT.NONE);
		mnRefresh.setText("Refresh");

		Menu mVarItem = new Menu(mPopupMenu);
		mnParam.setMenu(mVarItem);

		MenuItem mResetItem = new MenuItem(mPopupMenu, SWT.NONE);
		mResetItem.setText("Reset");
		mResetItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MenuItem paramItem = mPopupMenu.getItem(0);
				for (MenuItem item : paramItem.getMenu().getItems()) {
					// item.setSelection(false);
					SimpleName var = mParamVars.get(item);
					Node selNode = mParamNodes.get(item);
					selNode.disParameterize(var);
					propagateParam(var, false);
					mParamNodes.put(item, selNode);
				}
			}
		});

		// The org.eclipse.swt.widgets.Menu class has a package protected field
		// which determines whether the menu location has been set.
		// We need to use reflection to reset this boolean field to be false.
		// Otherwise,
		// the menu could be probably disposed and recreated.
		mPopupMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuHidden(MenuEvent event) {
				try {
					Field field = Menu.class.getDeclaredField("hasLocation");
					field.setAccessible(true);
					field.set(mPopupMenu, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void menuShown(MenuEvent e) {
			}
		});

		mnFindCxt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
					handlerService.executeCommand(UTPlugin.ID_CMD_CRITICS_FIND_CONTEXT, null);
				} catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException e1) {
					e1.printStackTrace();
				}
			}
		});

		mnRefresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshViews();
			}
		});

		// comment for screen capture for a presentation purpose Feb 26, 2014 4:25:13 PM mksong
		// mnGenerization.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// CriticsGeneralization generalize = new CriticsGeneralization();
		// generalize.generalizeQTreeOldRev();
		// generalize.generalizeQTreeNewRev();
		// }
		// });

		mGraphViewer.getGraphControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				if (mPrvSelectedGNode != null) {
					mPrvSelectedGNode.setBorderWidth(0);
				}

				mNodeSelectedByMouse = getNode(e);
				mGraphNodeSelectedByMouse = getGraphNode(e);
				UTLog.println(false, "[DBG] mouseDown - " + mNodeSelectedByMouse + ", " + mGraphNodeSelectedByMouse);

				if (mNodeSelectedByMouse == null || mGraphNodeSelectedByMouse == null) {
					mnParam.setEnabled(true);
					Menu mVarItem = new Menu(mPopupMenu);
					mnParam.setMenu(mVarItem);
					MenuItem item = new MenuItem(mVarItem, SWT.BUTTON1);
					item.setText("Open Pramaterization Dialog");
					item.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(final SelectionEvent e) {
							ArrayList<SimpleName> vars = getAllVariables();
							ParamDialog dialog = new ParamDialog(mGraphViewer.getControl().getShell(), true, view);
							dialog.setResources(vars);
							dialog.setInitialPattern("**");
							dialog.open();
							return;
						}

						private ArrayList<SimpleName> getAllVariables() {
							ArrayList<SimpleName> vars = new ArrayList<SimpleName>();
							GraphNode root = getRootNode();
							Node queryTree = (Node) root.getData();
							Enumeration<?> e = queryTree.postorderEnumeration();
							Node temp = null;
							while (e.hasMoreElements()) {
								temp = (Node) e.nextElement();
								if (temp.isSelectedByUser()) {
									Map<SimpleName, Boolean> map = temp.getParamMap();
									for (SimpleName name : map.keySet()) {
										// reduce duplication
										if (!isDuplicated(name, vars)) {
											vars.add(name);
										}
									}
								}
							}

							return vars;
						}

						private boolean isDuplicated(SimpleName name, ArrayList<SimpleName> vars) {
							IBinding binding = name.resolveBinding();
							for (int i = 0; i < vars.size(); i++) {
								SimpleName target = vars.get(i);
								IBinding targetBinding = target.resolveBinding();
								if (targetBinding.toString().equals(binding.toString())) {
									return true;
								}
							}
							return false;
						}
					});
					return;
				}

				if (mNodeSelectedByMouse.isExcludedByUser()) {
					mnParam.setEnabled(false);
				} else {
					mnParam.setEnabled(true);

					mPrvSelectedGNode = mGraphNodeSelectedByMouse;
					mGraphNodeSelectedByMouse.setBorderWidth(1);

					MenuItem mParam = mPopupMenu.getItem(0);
					if (mParamMenus.containsKey(mNodeSelectedByMouse)) {
						// This node has been selected before
						// which means its param menu has already been initialized
						// need to refresh menu item first
						Menu paramMenu = mParamMenus.get(mNodeSelectedByMouse);
						Map<SimpleName, Boolean> map = mNodeSelectedByMouse.getParamMap();
						for (MenuItem item : paramMenu.getItems()) {
							SimpleName var = mParamVars.get(item);
							if (map.get(var)) {
								item.setSelection(true);
							} else {
								item.setSelection(false);
							}
						}
						// set menu
						mParam.setMenu(paramMenu);
					} else {
						// This node has not been selected before
						// Need to initialize the menu for this node
						setMenuDynamically(mParam, e);
					}
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent event) {
				GraphNode selGraphNode = getGraphNode(event);
				Node selNode = getNode(event);

				if (selGraphNode == null || selNode == null) {
					return;
				}
				if (selNode.isExcludedByUser()) {
					selNode.setExcludedByUser(false);
					Image imgIn = UTPlugin.getImage(UTPlugin.IMG_ADD);
					selGraphNode.setImage(imgIn);
					selGraphNode.setBackgroundColor(getBGColor(selNode));
				} else {
					selNode.setExcludedByUser(true);
					Image imgEx = UTPlugin.getImage(UTPlugin.IMG_DEL);
					selGraphNode.setImage(imgEx);
					selGraphNode.setBackgroundColor(ColorConstants.gray);
				}
				String strNodeInfo = "(" + selNode.getLabel().name() + ") (" + selNode.getValue() + ")";
				UTLog.println(false, "[DBG]  mouseDoubleClick - " + strNodeInfo);
			}
			
			Color getBGColor(Object entity) {
				Color color = null;
				if (entity instanceof Node) {
					Node node = (Node) entity;
					switch (node.getNodeType()) {
					case User_Selection:
						color = mGraphViewer.getGraphControl().LIGHT_BLUE;
						break;
					case Data_Dependency:
						color = ColorConstants.orange;
						break;
					case Control_Dependency:
						color = ColorConstants.yellow;
						break;
					case Containment_Dependency:
						color = ColorConstants.lightGreen;
						break;
					default:
						color = mGraphViewer.getGraphControl().LIGHT_BLUE;
						break;
					}
				}
				return color;
			}
		});

		/*
		 * handle it in "MouseAdapter.mouseDown()" because of an event problem.
		 * mGraphViewer.getControl().addMenuDetectListener(new
		 * MenuDetectListener() {
		 * 
		 * @Override public void menuDetected(MenuDetectEvent e) { if
		 * (mNodeSelectedByMouse == null || mGraphNodeSelectedByMouse == null) {
		 * mnParam.setEnabled(false); } else { mnParam.setEnabled(true);
		 * System.out.println("[DBG] MenuDetectListener - " +
		 * mNodeSelectedByMouse); } } });
		 */
	}

	Node getNode(TypedEvent e) {
		Object obj = e.getSource();
		Graph instGraph = (Graph) obj;

		if (!instGraph.getSelection().isEmpty() && instGraph.getSelection().get(0) instanceof GraphNode) {
			GraphNode selGraphNode = (GraphNode) instGraph.getSelection().get(0);
			Node selNode = (Node) selGraphNode.getData();
			return selNode;
		}
		return null;
	}

	GraphNode getGraphNode(TypedEvent e) {
		Object obj = e.getSource();
		Graph instGraph = (Graph) obj;

		if (!instGraph.getSelection().isEmpty() && instGraph.getSelection().get(0) instanceof GraphNode) {
			GraphNode selGraphNode = (GraphNode) instGraph.getSelection().get(0);
			return selGraphNode;
		}
		return null;
	}

	public void displayIntCxtSelView(List<Node> aNodes) {
		this.mPrvSelectedGNode = null;
		setZoomValue("75%");
		mGraphViewer.setInput(aNodes);
		setZoomValue("75%");
		mGraphViewer.applyLayout();
		setZoomValue("75%");
	}

	private void setZoomValue(String aVal) {
		ZoomManager zMng = null;
		zMng = new ZoomManager(mGraphViewer.getGraphControl().getRootLayer(), mGraphViewer.getGraphControl().getViewport());
		zMng.setZoomAsText(aVal);
	}

	public void displayGraph(int cnt, List<GNode> aNodes) {
		mGraphViewer.setInput(aNodes);
		mGraphViewer.applyLayout();
		cnt++;
	}

	public void setLayoutManagerZestGraph() {
		switch (mLayout) {
		case 1:
			mGraphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			mLayout++;
			break;
		case 2:
			mGraphViewer.setLayoutAlgorithm(new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			mLayout++;
			break;
		case 3:
			mGraphViewer.setLayoutAlgorithm(new HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			mLayout++;
			break;
		case 4:
			mGraphViewer.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			mLayout++;
			break;
		case 5:
			mGraphViewer.setLayoutAlgorithm(new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			mLayout = 1;
			break;
		}
		mGraphViewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	private LayoutAlgorithm setLayout() {
		LayoutAlgorithm layout;
		layout = new TreeLayoutAlgorithm(LayoutStyles.ENFORCE_BOUNDS);
		return layout;

	}

	private void fillToolBar() {
		ZoomContributionViewItem toolbarZoomContributionViewItem = new ZoomContributionViewItem(this);
		IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().add(toolbarZoomContributionViewItem);
	}

	@Override
	public AbstractZoomableViewer getZoomableViewer() {
		return mGraphViewer;
	}

	/**
	 *
	 */
	void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				CriticsContextSelectionView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(mTbViewer.getControl());
		mTbViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, mTbViewer);
	}

	/**
	 *
	 */
	void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		// fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * 
	 * @param manager
	 */
	void fillLocalPullDown(IMenuManager manager) {
		manager.add(mExampleAction);
		manager.add(new Separator());
	}

	/**
	 * 
	 * @param manager
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(mExampleAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * 
	 * @param manager
	 */
	void fillLocalToolBar(IToolBarManager manager) {
		manager.add(mActTreeRefresh);
	}

	void makeRefreshAction() {
		mActTreeRefresh = new Action() {
			public void run() {
				mGraphViewer.applyLayout();
			}
		};
		mActTreeRefresh.setText("Tree Refresh");
		mActTreeRefresh.setToolTipText("Tree Refresh");
		mActTreeRefresh.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED));
	}

	/**
	 *
	 */
	void makeActions() {
		/*
		 * mExampleAction = new Action() { public void run() {
		 * UTCmdTextSelection cmdTextSelection = new UTCmdTextSelection();
		 * cmdTextSelection.procExampleAction();
		 * System.out.println("DBG__________________________________________");
		 * UTCmdLocalFileHistory.getLocalHistoryInfo();
		 * System.out.println("DBG__________________________________________");
		 * } }; mExampleAction.setText("Example View");
		 * mExampleAction.setToolTipText("Example View");
		 * mExampleAction.setImageDescriptor
		 * (PlatformUI.getWorkbench().getSharedImages
		 * ().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		 * mDoubleClickAction = new Action() { public void run() { ISelection
		 * selection = mTbViewer.getSelection(); Object obj =
		 * ((IStructuredSelection) selection).getFirstElement();
		 * showMessage("Double-click detected on " + obj.toString()); } };
		 */
	}

	/**
	 *
	 */
	void hookDoubleClickAction() {
		mTbViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				mDoubleClickAction.run();
			}
		});
	}

	/**
	 * 
	 * @param message
	 */
	void showMessage(String message) {
		MessageDialog.openInformation(mTbViewer.getControl().getShell(), "Example View", message);
	}

	/**
	 * 
	 */
	public void setFocus() {
		mGraphViewer.getControl().setFocus();
	}

	/**
	 * 
	 */
	public GraphViewer getGraphViewer() {
		return mGraphViewer;
	}

	/**
	 * 
	 */
	public void printNodes() {
		List<?> lstNodes = mGraphViewer.getGraphControl().getNodes();
		for (int i = 0; i < lstNodes.size(); i++) {
			Object elem = lstNodes.get(i);
			GraphNode iGrNode = (GraphNode) elem;
			Node iNode = (Node) iGrNode.getData();
			if (!iNode.isExcludedByUser()) {
				System.out.println("[DBG0]" + iNode);
			}
		}
		System.out.println("[DBG1]------------------------------------------");
	}

	public void refreshViews() {
		String idViewOldRev = "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView.OldRev";
		String idViewNewRev = "edu.utexas.seal.plugins.treegraph.view.CriticsContextSelectionView.NewRev";

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();

		IViewPart vwFinderOldRev = page.findView(idViewOldRev);
		IViewPart vwFinderNewRev = page.findView(idViewNewRev);

		CriticsContextSelectionView vwIntCxtSelOldRev, vwIntCxtSelNewRev;

		vwIntCxtSelOldRev = (CriticsContextSelectionView) vwFinderOldRev;
		vwIntCxtSelNewRev = (CriticsContextSelectionView) vwFinderNewRev;

		vwIntCxtSelNewRev.getGraphViewer().applyLayout();
		vwIntCxtSelOldRev.getGraphViewer().applyLayout();
	}

	public GraphNode getRootNode() {
		if (mGraphViewer == null || mGraphViewer.getGraphControl() == null || mGraphViewer.getGraphControl().getNodes().isEmpty())
			return null;
		return (GraphNode) mGraphViewer.getGraphControl().getNodes().get(0);
	}

	private void setMenuDynamically(MenuItem mParam, MouseEvent e) {
		// Get selected node
		Node selNode = getNode(e);

		// Set each variable as menu item in this menu
		Menu mVarItem = new Menu(mPopupMenu);
		mParam.setMenu(mVarItem);

		// Get variable map
		Map<SimpleName, Boolean> map = selNode.getParamMap();

		for (SimpleName variable : map.keySet()) {
			// create menu item
			MenuItem item = new MenuItem(mVarItem, SWT.CHECK);
			// set selected result based on parameterization propagation result
			item.setSelection(map.get(variable));
			// set text
			item.setText(variable.resolveTypeBinding().getName() + "   " + variable.getIdentifier());
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent e) {
					MenuItem item = (MenuItem) e.widget;
					if (item.getSelection()) {
						if (mParamVars.containsKey(item)) {
							Node selNode = mParamNodes.get(item);
							SimpleName var = mParamVars.get(item);
							// parameterize variable
							selNode.parameterize(var);
							// propagate this parameterization in the query tree
							propagateParam(var, true);
						}
						item.setSelection(true);
					} else {
						if (mParamVars.containsKey(item)) {
							Node selNode = mParamNodes.get(item);
							SimpleName var = mParamVars.get(item);
							selNode.disParameterize(var);
							propagateParam(var, false);
						}
						item.setSelection(false);
					}

					if (mPoint == null)
						return;

					mGraphViewer.getControl().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							mPopupMenu.setLocation(mPoint);
							mPopupMenu.setVisible(true);
						}
					});
				}
			});
			mParamVars.put(item, variable);
			mRevParamVars.put(variable, item); // Build Reverse Map of mParamVars for further use
			mParamNodes.put(item, selNode);
		}

		// Add this menu to the map
		mParamMenus.put(selNode, mVarItem);
	}

	private void propagateParam(SimpleName var, boolean bool) {
		IBinding binding = var.resolveBinding();
		GraphNode root = getRootNode();
		Node queryTree = (Node) root.getData();
		Enumeration<?> e = queryTree.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			if (temp.isSelectedByUser()) {
				Map<SimpleName, Boolean> map = temp.getParamMap();
				for (SimpleName name : map.keySet()) {
					IBinding nameBinding = name.resolveBinding();
					if (binding.toString().equals(nameBinding.toString())) {
						// this identifier refers to the same variable with 'var'
						if (bool)
							temp.parameterize(name);
						else
							temp.disParameterize(name);
					}
				}
			}
		}
	}

	public void propagateParam(ParamDialog dialog) {
		StructuredSelection selection = dialog.getSelectedItems();
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			SimpleName var = findSimpleName(name);
			propagateParam(var, true);
		}
	}

	private SimpleName findSimpleName(String name) {
		GraphNode root = getRootNode();
		Node queryTree = (Node) root.getData();
		Enumeration<?> e = queryTree.postorderEnumeration();
		Node temp = null;
		while (e.hasMoreElements()) {
			temp = (Node) e.nextElement();
			if (temp.isSelectedByUser()) {
				Map<SimpleName, Boolean> map = temp.getParamMap();
				for (SimpleName var : map.keySet()) {
					String varName = var.resolveTypeBinding().getName() + "   " + var.getIdentifier();
					if (varName.equals(name)) {
						return var;
					}
				}
			}
		}
		return null;
	}
}
