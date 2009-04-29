/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.startup.applications;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.LocaleService;
import org.csstudio.platform.security.SecurityFacade;
import org.csstudio.platform.startupservice.ServiceProxy;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.csstudio.platform.ui.dialogs.LoginDialog;
import org.csstudio.platform.ui.workbench.CssWorkbenchAdvisor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.remotercp.common.servicelauncher.ServiceLauncher;
import org.remotercp.login.ui.ChatLoginWizardDialog;

/**
 * The RCP application for the Control System Studio.
 * 
 * @author Alexander Will
 * @author Kay Kasemir
 * @version $Revision$
 * 
 */
public class Application implements IApplication {
	/**
	 * {@inheritDoc}
	 */
	public Object start(IApplicationContext context) throws Exception {
				IPreferenceStore coreStore = new ScopedPreferenceStore(
				new InstanceScope(), CSSPlatformPlugin.getDefault().getBundle()
						.getSymbolicName());

		applyLocaleSetting(coreStore);
		
		Display display = PlatformUI.createDisplay();
		
		try {
			int returnCode = EXIT_OK;
			
			SecurityFacade sf = SecurityFacade.getInstance();
			String lastUser = Platform.getPreferencesService().getString(CSSPlatformPlugin.ID,SecurityFacade.LOGIN_LAST_USER_NAME , "", null);
			sf.setLoginCallbackHandler(new LoginDialog(null,lastUser));
			if (sf.isLoginOnStartupEnabled()) {
				sf.authenticateApplicationUser();
			}
			
			// we will run all the services
			// TODO: implement checking if there is a service the user
			// does not wish to run
			ServiceProxy[] proxies = StartupServiceEnumerator.getServices();
			List<ServiceProxy> lowPriorityProxy = new ArrayList<ServiceProxy>();
			
			
			for(ServiceProxy proxy : proxies) {
				if(proxy.isHighPriority()) {
					proxy.run();
				} else {
					lowPriorityProxy.add(proxy);
				}
			}
			
			// TODO: implement this so that each low priority proxy
			// is created and ran in a separate thread
			for(ServiceProxy proxy : lowPriorityProxy) {
				// TODO: add thread creation code here!
				proxy.run();
			}
			
			ChatLoginWizardDialog wizardDialog = new ChatLoginWizardDialog();
			if (wizardDialog.open() == Window.OK) {
				// start remote services
				ServiceLauncher.startRemoteServices();
			}

			returnCode = PlatformUI.createAndRunWorkbench(display,
					new CssWorkbenchAdvisor());
			
            if (returnCode == PlatformUI.RETURN_RESTART) {
            	// Something called IWorkbench.restart().
                // Is this supposed to be a RESTART or RELAUNCH?
                final Integer exitCode =
                    Integer.getInteger(RelaunchConstants.PROP_EXIT_CODE);
                if (IApplication.EXIT_RELAUNCH.equals(exitCode)) {
                	// RELAUCH with new command line
                    return IApplication.EXIT_RELAUNCH;
                }
                // RESTART without changes
                return IApplication.EXIT_RESTART;
            }
            // Plain exit from IWorkbench.close()
            return IApplication.EXIT_OK;
		} finally {
			if (display != null) {
				display.dispose();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		// TODO: find out how to forcibly stop a running workbench
	}

	/**
	 * Set the system's default locate according to the CSS settings.
	 * 
	 * @param coreStore
	 *            The core preference store.
	 */
	private void applyLocaleSetting(final IPreferenceStore coreStore) {
		String locale = coreStore.getString(LocaleService.PROP_LOCALE);
		LocaleService.setSystemLocale(locale);
	}
}
