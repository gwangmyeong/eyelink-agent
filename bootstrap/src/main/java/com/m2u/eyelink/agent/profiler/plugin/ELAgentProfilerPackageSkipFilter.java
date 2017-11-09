package com.m2u.eyelink.agent.profiler.plugin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ELAgentProfilerPackageSkipFilter implements ClassNameFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<String> packageList;

    public ELAgentProfilerPackageSkipFilter() {
        this(getELAgentPackageList());
    }

    public ELAgentProfilerPackageSkipFilter(List<String> packageList) {
        if (packageList == null) {
            throw new NullPointerException("packageList must not be null");
        }
        this.packageList = new ArrayList<String>(packageList);
    }



    @Override
    public boolean accept(String className) {
        if (className == null) {
            throw new NullPointerException("className must not be null");
        }

        for (String packageName : packageList) {
            if (className.startsWith(packageName)) {
                if (logger.isDebugEnabled()) {
                    logger.info("skip ProfilerPackage:{} Class:{}", packageName, className);
                }
                return REJECT;
            }
        }
        return ACCEPT;
    }

    private static List<String> getELAgentPackageList() {
        List<String> elagentPackageList = new ArrayList<String>();
        elagentPackageList.add("com.m2u.eyelink.agent");
        elagentPackageList.add("com.m2u.eyelink.agent.profiler");
        elagentPackageList.add("com.m2u.eyelink..common");
        elagentPackageList.add("com.m2u.eyelink..exception");
        // TODO move test package
        elagentPackageList.add("com.m2u.eyelink..test");
        return elagentPackageList;
    }
}
