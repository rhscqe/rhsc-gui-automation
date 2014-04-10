package com.redhat.qe.storageconsole.sahi.tasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.calgb.test.performance.html.RegexMatch;

import net.sf.sahi.client.ElementStub;

import com.redhat.qe.storageconsole.helpers.elements.Dialog;
import com.redhat.qe.storageconsole.helpers.jquery.JQuery;
import com.redhat.qe.storageconsole.helpers.jquery.JQueryElement;
import com.redhat.qe.storageconsole.sahi.base.ExtendedBrowser;
import com.redhat.qe.storageconsole.te.Server;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfig;
import com.redhat.qe.storageconsole.te.TestEnvironmentConfigException;

public class StorageBrowser extends ExtendedBrowser {

	protected Logger _logger = Logger.getLogger(GuiTables.class.getName());

	public StorageBrowser(String browserPath, String browserType,
			String browserOpt, String sahiDir, String userDataDir) {
		super(browserPath, browserType, browserOpt, sahiDir, userDataDir);
	}

	private boolean isOpen = false;

	@Override
	public void open() {
		super.open();
		isOpen = true;
	}

	@Override
	public void kill() {
		super.kill();
		isOpen = false;
	}

	@Override
	public void close() {
		super.close();
		isOpen = false;
	}

	public boolean isOpen() {
		return isOpen;
	}

	// -------------------------------------------------------------------------------------
	// Common methods
	// -------------------------------------------------------------------------------------

	/*
	 * High level method to click on the element
	 */
	private void clickOnElement(ElementStub element) {
		element.click();
		_logger.log(Level.FINE, "Clicked on the element [" + element + "]");
	}

	/*
	 * Selects TOP level pages. Can pass parameters as follows, Example:
	 * System->System->Servers (or) System->Servers (or) Servers
	 */
	public boolean selectPage(String pageLocation) {
		if (pageLocation == null) {
			return true;
		}
		String[] resources = pageLocation.split("->");
		for (String resource : resources) {
			if (this.link(resource).exists()) {
				this.clickOnElement(this.link(resource));
			} else if (this.span(resource).exists()) {
				this.clickOnElement(this.span(resource));
			} else if (this.div(resource).exists()) {
				this.clickOnElement(this.div(resource));
			} else {
				throw new RuntimeException(
						"Unable to select/navigate to Page; Resource ["
								+ resource + "] is not available");
			}
		}
		return true;
	}

	public void clickRefresh(String tabName) {
		// String[] intervals = {"5","10","20","30","60"};
		ElementStub refreshElement;
		// for(String interval : intervals){
		// refreshElement =
		// this.image("clear.cache.gif").in(this.byXPath("//div[@title='Refresh Status: Active(running): Rate: Normal("+interval+" sec)']"));
		// if(refreshElement.exists()){
		// refreshElement.click();
		// _logger.log(Level.FINE, "Refresh Element: "+refreshElement);
		// return;
		// }
		// }

		refreshElement = this.div("MainTab" + tabName
				+ "View_table_refreshPanel_refreshButton");
		if (refreshElement.exists()) {
			refreshElement.click();
			_logger.log(Level.FINE, "Refresh Element: " + refreshElement);
			return;
		}

		_logger.log(Level.WARNING, "Unable to Locate the Refresh icon!!");
	}

	// -------------------------------------------------------------------------------------
	// Close error pop-up - temporary work-around
	// -------------------------------------------------------------------------------------

	public void closePopup(String label) {
		for (int i = 0; i < 3; i++) {
			if (this.div(label).exists()) {
				this.div(label).click();
			}
		}

	}

	public String getString(Throwable th) {
		if (th != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			th.printStackTrace(pw);
			return sw.toString();
		} else {
			return null;
		}
	}

	// -------------------------------------------------------------------------------------
	// RHSC Version Information
	// -------------------------------------------------------------------------------------

	/*
	 * Returns Build information
	 */
	public String getversion(){
		this.link("About").click();
		String dialogText = new Dialog("Console Version", this).getText();
		String version = new RegexMatch(dialogText).find(": (\\w|\\.|-)+").get(0).getText().replaceAll(": ", "");
		return version;
	}

	/*
	 * Getting server(s) detail as CSV and returns List<Server>
	 */
	public List<Server> getServers(String serverCSV)
			throws FileNotFoundException, TestEnvironmentConfigException,
			IOException, JAXBException {
		List<Server> servers = new ArrayList<Server>();
		_logger.log(Level.INFO, "CSV: " + serverCSV);
		String[] serverList = serverCSV.split(",");
		for (String server : serverList) {
			servers.add(TestEnvironmentConfig.getTestEnvironemt().getServer(
					server.trim()));
		}
		return servers;
	}

	public void wait(int wait, int retryCount, int itteration) {
		_logger.log(Level.FINE, "Attempt: [" + (itteration + 1) + " of "
				+ retryCount + "]");
		waitFor(wait);
	}

	/**
	 * @return
	 */
	public boolean isDisabled(ElementStub element) {
		return JQuery.toJQuery(element).addCall("is", ":disabled")
				.fetch(browser).equalsIgnoreCase("true");
	}

}
