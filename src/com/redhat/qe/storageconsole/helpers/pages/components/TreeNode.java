/**
 * 
 */
package com.redhat.qe.storageconsole.helpers.pages.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.sahi.client.Browser;
import net.sf.sahi.command.Log;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.redhat.qe.storageconsole.helpers.Times;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.helpers.jquery.JsString;
import com.redhat.qe.storageconsole.sahi.tasks.StorageBrowser;

/**
 * @author dustin 
 * Feb 22, 2013
 * 
 * Each node is a div that represents each node
 * 
 * the div contains a wrapper div.
 * 
 * <pre>
 * div (tree node) 
 *     \_div (wrapper) 
 *         \_div (arrow dir)
 *         \_div(text)
 *         
 * div (children wrapper)
 * </pre>
 */
//TreeNode.getNodeByName(getBrowser(), clustername).getFirstChildWithName("Volumes").getElementStub().click();
public class TreeNode extends JQueryElement{

	public static String downArrowPng = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAt0lEQVR42mNgGPogt6JzRV555/ncys6TuRVdp/Iqu06B2HlVnYeBclfyKjobcGoGSk4HKvoPwnmVINwFwRD+H6B4NE7N+RXtpkBFdyGau6AGdII151d0bqmvr2fD63SgE6eCNSMbUNH5Ob+qO4agvwvKOjWBGu6DNCEM6dxFdMABbZ+dVwHX/CGnoiuUaM35ZZ3q+ZVdL0HOBhpwhORoA9o4E4i/5pV3+JOsObu6Rx7o1+UMww8AANkFdufA16fyAAAAAElFTkSuQmCC";
	public static String rightArrowPng = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAt0lEQVR42mNgGPogt6JzRV555/ncys6TuRVdp/Iqu06B2HlVnYeBclfyKjobcGoGSk4HKvoPwnmVINwFwRD+H6B4NE7N+RXtpkBFdyGau6AGdII151d0bqmvr2fD63SgE6eCNSMbUNH5Ob+qO4agvwvKOjWBGu6DNCEM6dxFdMABbZ+dVwHX/CGnoiuUaM35ZZ3q+ZVdL0HOBhpwhORoA9o4E4i/5pV3+JOsObu6Rx7o1+UMww8AANkFdufA16fyAAAAAElFTkSuQmCC";
	/**
	 * @param browser
	 */
	public TreeNode(JQuery jqueryObject, Browser browser) {
		super(jqueryObject, browser);
	}
	
	public JQuery getTextElement(){
		return getJqueryObject().addCall("find",new JsString("> div > div:eq(1)"));
	}

	public JQuery getCheckBox(){
		return getTextElement().find("input");
	}
	
	public String getText(){
		return getTextElement().addCall("text").fetch(getBrowser());
	}
	
	public List<TreeNode> getChildren(){
		List<TreeNode> results = new ArrayList<TreeNode>();
		for(int index: new Times(numberOfChildren())){
			results.add(getChild(index));
		}
		return results;
	}
	
	public Collection<TreeNode> getChildrenByName(final String name){
		List<TreeNode> children = getChildren();
		return Collections2.filter(children, new Predicate<TreeNode>() {
			@Override
			public boolean apply(TreeNode child){
				System.out.println(child.getText());
				System.out.println(name);
				return child.getText().contains(name);
			}
		});
	}
	
	public TreeNode getFirstChildWithName(final String name){
		Collection<TreeNode> matches = getChildrenByName(name);
		if(matches.size() > 0){
			return matches.iterator().next();
		}else{
			return null;
		}
	}
	
	public TreeNode getChild(int index){
		JQuery elem = getJqueryObject()	
				.addCall("next", new JsString("div"))
				.addCall("find", new JsString("> div > div > div:eq(%s)", index))
				.addCall("find", new JsString("> div:eq(0)"));
		return new TreeNode(elem, getBrowser());
	}
	
	public boolean isOpen(){
		return getImageBackgroundCss().contains(downArrowPng);
	}

	public void open(){
		if(!isOpen()){
			toggleOpenClose();
		}
	}
	
	public void close(){
		if(isOpen()){
			toggleOpenClose();
		}
	}
	
	public void toggleOpenClose(){
		getBrowser().accessor(getArrowElement().toDomObject().toString()).click();
	}
	
	private String getImageBackgroundCss(){
		return getArrowImage().addCall("css", new JsString("background-image")).fetch(getBrowser());
	}

	/**
	 * @return
	 */
	private JQuery getArrowImage() {
		return getArrowElement().find("img");
	}

	/**
	 * @return
	 */
	private JQuery getArrowElement() {
		return getJqueryObject().find(">div>div:eq(0)");
	}

	/**
	 * @return 
	 * 
	 */
	private int numberOfChildren() {
		return Integer.parseInt(getChildrenWrappers().property("length").fetch(getBrowser()));
	}
 
	/**
	 * @return
	 */
	private JQuery getChildrenWrappers() {
		JQuery childrenWrapper = getJqueryObject()
			.addCall("next", new JsString("div"))
			.addCall("find", new JsString("> div > div > div"));
		return childrenWrapper;
	}
	
	
	public static TreeNode getNodeByName(StorageBrowser browser, String name){
		return new TreeNode(new JQuery(String.format("div:has(> div > div > span:contains('%s'))", name)), browser);
	}
	
	public void openAllTags(){
		openAllTags(this);
	}
	
	void openAllTags(TreeNode node){
		node.open();
		if(node.getChildren().size() == 0){
			return;
		}else{
			for(TreeNode child : node.getChildren()){
				openAllTags(child);
			}
		}
	}
}
