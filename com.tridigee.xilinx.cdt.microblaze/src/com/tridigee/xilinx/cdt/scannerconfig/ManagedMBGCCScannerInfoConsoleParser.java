package com.tridigee.xilinx.cdt.scannerconfig;

import org.eclipse.cdt.build.core.scannerconfig.CfgInfoContext;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.make.core.scannerconfig.IScannerInfoCollector;
import org.eclipse.cdt.make.core.scannerconfig.InfoContext;
import org.eclipse.cdt.make.internal.core.scannerconfig.gnu.GCCScannerInfoConsoleParser;
import org.eclipse.cdt.make.internal.core.scannerconfig2.PerProjectSICollector;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.resources.IProject;

public class ManagedMBGCCScannerInfoConsoleParser extends GCCScannerInfoConsoleParser {
	private Boolean fManagedBuildOnState;

    public boolean processLine(String string) {
        if (this.isManagedBuildOn()) {
            return false;
        }
        return super.processLine(string);
    }

    public void shutdown() {
        if (!this.isManagedBuildOn()) {
            super.shutdown();
        }
        this.fManagedBuildOnState = null;
    }

    public void startup(IProject iProject, IScannerInfoCollector iScannerInfoCollector) {
        if (this.isManagedBuildOn()) {
            return;
        }
        super.startup(iProject, iScannerInfoCollector);
    }

    private boolean isManagedBuildOn() {
        if (this.fManagedBuildOnState == null) {
            this.fManagedBuildOnState = this.doCalcManagedBuildOnState();
        }
        return this.fManagedBuildOnState;
    }

    private boolean doCalcManagedBuildOnState() {
        IScannerInfoCollector iScannerInfoCollector = this.getCollector();
        if (!(iScannerInfoCollector instanceof PerProjectSICollector)) {
            return false;
        }
        InfoContext infoContext = ((PerProjectSICollector)iScannerInfoCollector).getContext();
        IProject iProject = infoContext.getProject();
        ICProjectDescription iCProjectDescription = CoreModel.getDefault().getProjectDescription(iProject, false);
        CfgInfoContext cfgInfoContext = CfgInfoContext.fromInfoContext((ICProjectDescription)iCProjectDescription, (InfoContext)infoContext);
        if (cfgInfoContext != null) {
            IConfiguration iConfiguration = cfgInfoContext.getConfiguration();
            return iConfiguration.isManagedBuildOn();
        }
        return false;
    }
}
